package ssuchaehwa.it_project.domain.login.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto;
import ssuchaehwa.it_project.domain.user.entity.User;
import ssuchaehwa.it_project.domain.user.repository.UserRepository;
import ssuchaehwa.it_project.global.config.security.jwt.JwtUtil;
import ssuchaehwa.it_project.domain.login.domain.KakaoOAuthClient;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    // 웹용
    @Override
    public AuthResponseDto.LoginResult kakaoLogin(String code) {
        // 1. 카카오 accessToken 발급
        AuthResponseDto.KakaoToken token = kakaoOAuthClient.requestToken(code);

        // 2. accessToken으로 사용자 정보 요청
        AuthResponseDto.KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(token.getAccessToken());

        // 3. 유저 존재 여부 확인
        User user = userRepository.findBySocialId(String.valueOf(userInfo.getId()))
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialId(String.valueOf(userInfo.getId()))
                                .nickname(userInfo.getKakaoAccount().getProfile().getNickname())
                                .profileImageUrl(userInfo.getKakaoAccount().getProfile().getProfileImageUrl())
                                .level(1)
                                .exp(0)
                                .gold(0)
                                .diamond(0)
                                .onboardingCompleted(false)
                                .build()
                ));

        boolean isNewUser = !userRepository.existsBySocialId(String.valueOf(userInfo.getId()));

        // 4. JWT 토큰 발급
        String jwtAccessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));
        String jwtRefreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()));
        // Redis에 refreshToken 저장
        redisTemplate.opsForValue().set(
                "refresh:userId:" + user.getId(),
                jwtRefreshToken,
                jwtUtil.getRefreshTokenValidity(),
                TimeUnit.MILLISECONDS
        );

        // 5. 최종 응답 (카카오 access/refresh 토큰도 포함해서 반환)
        return AuthResponseDto.LoginResult.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .isNewUser(isNewUser)
                .kakaoAccessToken(token.getAccessToken())
                .kakaoRefreshToken(token.getRefreshToken())
                .build();
    }

    // 앱용
    @Override
    public AuthResponseDto.LoginResult kakaoLoginWithAccessToken(String kakaoAccessToken) {
        // 1. accessToken으로 사용자 정보 요청
        AuthResponseDto.KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(kakaoAccessToken);
        String socialId = String.valueOf(userInfo.getId());

        // 2. 유저 존재 여부 먼저 판단
        Optional<User> existingUser = userRepository.findBySocialId(socialId);
        boolean isNewUser = existingUser.isEmpty();

        // 3. 없으면 새로 저장
        User user = existingUser.orElseGet(() -> userRepository.save(
                User.builder()
                        .socialId(socialId)
                        .nickname(userInfo.getKakaoAccount().getProfile().getNickname())
                        .profileImageUrl(userInfo.getKakaoAccount().getProfile().getProfileImageUrl())
                        .level(1)
                        .exp(0)
                        .gold(0)
                        .diamond(0)
                        .onboardingCompleted(false)
                        .build()
        ));

        // 4. JWT 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()));

        // 5. Redis 저장
        redisTemplate.opsForValue().set(
                "refresh:userId:" + user.getId(),
                refreshToken,
                jwtUtil.getRefreshTokenValidity(),
                TimeUnit.MILLISECONDS
        );

        // 6. 응답 반환
        return AuthResponseDto.LoginResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .kakaoAccessToken(kakaoAccessToken)
                .kakaoRefreshToken(null)
                .isNewUser(isNewUser)
                .build();
    }

    @Override
    public AuthResponseDto.LoginResult refreshToken(Long userId, String refreshToken) {
        String redisKey = "refresh:userId:" + userId;
        String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("❌ 유효하지 않은 refresh token입니다.");
        }

        // 새 JWT 토큰 생성
        String newAccessToken = jwtUtil.generateAccessToken(String.valueOf(userId));
        String newRefreshToken = jwtUtil.generateRefreshToken(String.valueOf(userId));

        // Redis에 새 refresh 토큰 갱신
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, jwtUtil.getRefreshTokenValidity(), TimeUnit.MILLISECONDS);

        return AuthResponseDto.LoginResult.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .kakaoAccessToken(null)
                .kakaoRefreshToken(null)
                .isNewUser(false)
                .build();
    }

    // 게스트 로그인
    @Override
    public AuthResponseDto.LoginResult guestLogin(String deviceId) {
        // 1. 유저 존재 여부 먼저 판단
        Optional<User> existingUser = userRepository.findBySocialId(deviceId);
        boolean isNewUser = existingUser.isEmpty();

        // 2. 없으면 새로 저장
        User user = existingUser.orElseGet(() -> userRepository.save(
                User.builder()
                        .socialId(deviceId)
                        .nickname("게스트_" + deviceId.substring(0, 5))
                        .profileImageUrl(null)
                        .level(1)
                        .exp(5000)
                        .gold(1000)
                        .diamond(0)
                        .onboardingCompleted(false)
                        .build()
        ));

        // 3. 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()));

        // 4. Redis 저장
        redisTemplate.opsForValue().set(
                "refresh:userId:" + user.getId(),
                refreshToken,
                jwtUtil.getRefreshTokenValidity(),
                TimeUnit.MILLISECONDS
        );

        // 5. 응답 반환
        return AuthResponseDto.LoginResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .kakaoAccessToken(null)
                .kakaoRefreshToken(null)
                .build();
    }
}