package ssuchaehwa.it_project.domain.login.application;

import lombok.RequiredArgsConstructor;
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

        // 2. 유저 존재 여부 확인 또는 저장
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

        // 3. JWT 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()));

        // 4. 최종 응답에 kakaoAccessToken도 추가
        return AuthResponseDto.LoginResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .kakaoAccessToken(kakaoAccessToken)
                .kakaoRefreshToken(null)  // 이 흐름에선 refreshToken 없음
                .isNewUser(user.getId() == null)
                .build();
    }
}