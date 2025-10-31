package ssuchaehwa.it_project.domain.login.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ssuchaehwa.it_project.domain.analysis.domain.repository.CoachingRecordRepository;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto;
import ssuchaehwa.it_project.domain.model.enums.FriendStatus;
import ssuchaehwa.it_project.domain.pomodoro.domain.repository.PomodoroRepository;
import ssuchaehwa.it_project.domain.quest.domain.entity.InvitedFriend;
import ssuchaehwa.it_project.domain.quest.domain.repository.*;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.user.domain.repository.UserRepository;
import ssuchaehwa.it_project.domain.user.domain.repository.UserTermRepository;
import ssuchaehwa.it_project.global.config.security.jwt.JwtUtil;
import ssuchaehwa.it_project.domain.login.domain.KakaoOAuthClient;
import ssuchaehwa.it_project.domain.login.domain.AppleOAuthClient;
import ssuchaehwa.it_project.domain.model.enums.SocialProvider;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;
import ssuchaehwa.it_project.global.exception.GeneralException;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final AppleOAuthClient appleOAuthClient;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final InvitedFriendRepository invitedFriendRepository;
    private final CoachingRecordRepository coachingRecordRepository;
    private final PomodoroRepository pomodoroRepository;
    private final PartyUserRepository partyUserRepository;
    private final PartyRepository partyRepository;
    private final QuestRepository questRepository;
    private final QuestOccurrenceRepository questOccurrenceRepository;
    private final UserTermRepository userTermRepository;


    // 웹용
    @Override
    public AuthResponseDto.LoginResult kakaoLogin(String code, @Nullable String inviterCode) {
        // 1. 카카오 accessToken 발급
        AuthResponseDto.KakaoToken token = kakaoOAuthClient.requestToken(code);

        // 2. accessToken으로 사용자 정보 요청
        AuthResponseDto.KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(token.getAccessToken());

        // 3. 유저 존재 여부 확인
        String socialId = String.valueOf(userInfo.getId());
        User user = userRepository.findBySocialIdAndProvider(socialId, SocialProvider.KAKAO)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialId(socialId)
                                .provider(SocialProvider.KAKAO)
                                .nickname(userInfo.getKakaoAccount().getProfile().getNickname())
                                .profileImageUrl(userInfo.getKakaoAccount().getProfile().getProfileImageUrl())
                                .level(0)
                                .exp(0)
                                .gold(0)
                                .diamond(0)
                                .onboardingCompleted(false)
                                .inviteCode(UUID.randomUUID().toString().substring(0, 8)) // 초대 코드 생성
                                .build()
                ));

        boolean isNewUser = !userRepository.existsBySocialIdAndProvider(socialId, SocialProvider.KAKAO);

        if (isNewUser && inviterCode != null) {
            userRepository.findByInviteCode(inviterCode).ifPresent(inviter -> {
                InvitedFriend invitedFriend = InvitedFriend.builder()
                        .fromUser(inviter) // 초대한 사람
                        .toUser(user)       // 새로 가입한 사람
                        .status(FriendStatus.ACCEPTED)
                        .build();
                invitedFriendRepository.save(invitedFriend);
            });
        }

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
    public AuthResponseDto.LoginResult kakaoLoginWithAccessToken(String kakaoAccessToken, @Nullable String inviterCode) {
        // 1. accessToken으로 사용자 정보 요청
        AuthResponseDto.KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(kakaoAccessToken);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(userInfo);
            log.info("카카오 응답 전체(JSON): {}", json);
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 실패", e);
        }
        String socialId = String.valueOf(userInfo.getId());

        // 2. 유저 존재 여부 먼저 판단
        Optional<User> existingUser = userRepository.findBySocialIdAndProvider(socialId, SocialProvider.KAKAO);
        boolean isNewUser = existingUser.isEmpty();

        // 3. 없으면 새로 저장
        User user = existingUser.orElseGet(() -> userRepository.save(
                User.builder()
                        .socialId(socialId)
                        .provider(SocialProvider.KAKAO)
                        .nickname(userInfo.getKakaoAccount().getProfile().getNickname())
                        .profileImageUrl(userInfo.getKakaoAccount().getProfile().getProfileImageUrl())
                        .email(userInfo.getKakaoAccount().getEmail())
                        .level(0)
                        .exp(0)
                        .gold(0)
                        .diamond(0)
                        .onboardingCompleted(false)
                        .inviteCode(UUID.randomUUID().toString().substring(0, 8))
                        .build()
        ));

        // 4. 초대한 유저와 친구 관계 저장
        if (isNewUser && inviterCode != null) {
            userRepository.findByInviteCode(inviterCode).ifPresent(inviter -> {
                InvitedFriend invitedFriend = InvitedFriend.builder()
                        .fromUser(inviter) // 초대한 사람
                        .toUser(user)       // 새로 가입한 사람
                        .status(FriendStatus.ACCEPTED)
                        .build();
                invitedFriendRepository.save(invitedFriend);
            });
        }

        // 5. JWT 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()));

        // 6. Redis 저장
        redisTemplate.opsForValue().set(
                "refresh:userId:" + user.getId(),
                refreshToken,
                jwtUtil.getRefreshTokenValidity(),
                TimeUnit.MILLISECONDS
        );

        // 7. 응답 반환
        return AuthResponseDto.LoginResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .kakaoAccessToken(kakaoAccessToken)
                .kakaoRefreshToken(null)
                .isNewUser(isNewUser)
                .build();
    }

    // 토큰 재발급
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
        Optional<User> existingUser = userRepository.findBySocialIdAndProvider(deviceId, SocialProvider.GUEST);
        boolean isNewUser = existingUser.isEmpty();

        // 2. 없으면 새로 저장
        User user = existingUser.orElseGet(() -> userRepository.save(
                User.builder()
                        .socialId(deviceId)
                        .provider(SocialProvider.GUEST)
                        .nickname("게스트_" + deviceId.substring(0, 5))
                        .level(0)
                        .exp(0)
                        .gold(0)
                        .diamond(0)
                        .profileImageUrl(null)
                        .onboardingCompleted(false)
                        .inviteCode(UUID.randomUUID().toString().substring(0, 8))
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

    // 자동 로그인
    @Override
    public AuthResponseDto.AutoLoginResult autoLogin(String accessToken) {
        String userIdStr;

        try {
            userIdStr = jwtUtil.validateAndGetUserId(accessToken);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_ACCESS_TOKEN);
        }

        Long userId = Long.parseLong(userIdStr);
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new GeneralException(ErrorStatus.NO_SUCH_USER);
        }

        return AuthResponseDto.AutoLoginResult.builder()
                .valid(true)
                .userId(userId)
                .build();
    }

    // 로그아웃
    @Override
    public void logout(String accessToken) {
        String userIdStr;
        try {
            userIdStr = jwtUtil.validateAndGetUserId(accessToken); // 유효성 + ID 추출
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_ACCESS_TOKEN);
        }

        Long userId = Long.parseLong(userIdStr);
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new GeneralException(ErrorStatus.NO_SUCH_USER);
        }

        String redisKey = "refresh:userId:" + userId;
        redisTemplate.delete(redisKey);
    }

    // 회원탈퇴
    @Transactional
    @Override
    public void withdraw(String accessToken) {
        String userIdStr = jwtUtil.validateAndGetUserId(accessToken);
        Long userId = Long.parseLong(userIdStr);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_SUCH_USER));

        // 자식 엔티티 삭제
        coachingRecordRepository.deleteByUser(user);
        pomodoroRepository.deleteByUser(user);
        invitedFriendRepository.deleteByFromUserOrToUser(user, user);
        partyUserRepository.deleteByUser(user);
        partyRepository.deleteByUser(user);
        questRepository.deleteByUser(user);
        questOccurrenceRepository.deleteByUserId(userId);
        userTermRepository.deleteByUser(user);

        // 유저 삭제
        userRepository.delete(user);

        // refreshToken 삭제
        redisTemplate.delete("refresh:userId:" + userId);
    }

    // 애플 Mock 로그인 (테스트용)
    @Override
    public AuthResponseDto.LoginResult mockAppleLogin(String sub, String email, Boolean emailVerified, @Nullable String inviterCode) {
        log.info("🍎 애플 Mock 로그인 처리 시작 - sub: {}, email: {}", sub, email);

        // 1. Mock 애플 사용자 정보 생성 (실제 애플 서버 검증 생략)
        AuthResponseDto.AppleUserInfo mockAppleUserInfo = AuthResponseDto.AppleUserInfo.builder()
                .sub(sub)
                .email(email)
                .emailVerified(emailVerified != null ? emailVerified : false)
                .build();

        // 2. 실제 애플 로그인과 동일한 비즈니스 로직 수행
        return processAppleLogin(mockAppleUserInfo, inviterCode);
    }

    // 애플 로그인 공통 처리 로직 (실제 + Mock 공용)
    private AuthResponseDto.LoginResult processAppleLogin(AuthResponseDto.AppleUserInfo appleUserInfo, @Nullable String inviterCode) {
        String socialId = appleUserInfo.getSub();

        // 사용자 조회/생성
        Optional<User> existingUser = userRepository.findBySocialIdAndProvider(socialId, SocialProvider.APPLE);
        boolean isNewUser = existingUser.isEmpty();

        User user = existingUser.orElseGet(() -> userRepository.save(
                User.builder()
                        .socialId(socialId)
                        .provider(SocialProvider.APPLE)
                        .nickname("애플사용자_" + socialId.substring(0, 5))
                        .email(appleUserInfo.getEmail())
                        .level(1)
                        .exp(0)
                        .gold(0)
                        .diamond(0)
                        .onboardingCompleted(false)
                        .inviteCode(UUID.randomUUID().toString().substring(0, 8))
                        .build()
        ));

        // 초대 코드 처리
        if (isNewUser && inviterCode != null) {
            userRepository.findByInviteCode(inviterCode).ifPresent(inviter -> {
                InvitedFriend invitedFriend = InvitedFriend.builder()
                        .fromUser(inviter)
                        .toUser(user)
                        .status(FriendStatus.ACCEPTED)
                        .build();
                invitedFriendRepository.save(invitedFriend);
            });
        }

        // JWT 토큰 발급
        String jwtAccessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));
        String jwtRefreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()));

        // Redis에 refreshToken 저장
        redisTemplate.opsForValue().set(
                "refresh:userId:" + user.getId(),
                jwtRefreshToken,
                jwtUtil.getRefreshTokenValidity(),
                TimeUnit.MILLISECONDS
        );

        log.info("🍎 애플 로그인 완료 - userId: {}, isNewUser: {}", user.getId(), isNewUser);

        return AuthResponseDto.LoginResult.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .isNewUser(isNewUser)
                .kakaoAccessToken(null)
                .kakaoRefreshToken(null)
                .build();
    }

    // 애플 로그인
    @Override
    public AuthResponseDto.LoginResult appleLoginWithIdentityToken(String identityToken, @Nullable String name, @Nullable String inviterCode) {
        // 1. Apple Identity Token 검증 및 사용자 정보 추출
        AuthResponseDto.AppleUserInfo appleUserInfo = appleOAuthClient.verifyIdentityToken(identityToken);

        String socialId = appleUserInfo.getSub(); // Apple 고유 사용자 ID
        log.info("🍎 애플 로그인 처리 시작 - sub: {}, email: {}, name: {}", socialId, appleUserInfo.getEmail(), name);

        // 2. 유저 존재 여부 먼저 판단
        Optional<User> existingUser = userRepository.findBySocialIdAndProvider(socialId, SocialProvider.APPLE);
        boolean isNewUser = existingUser.isEmpty();

        // 3. 없으면 새로 저장
        User user = existingUser.orElseGet(() -> {
            // 닉네임 결정: name이 있으면 사용, 없으면 기본값
            String nickname = (name != null && !name.trim().isEmpty())
                ? name
                : "애플사용자_" + socialId.substring(0, 5);

            return userRepository.save(
                User.builder()
                        .socialId(socialId)
                        .provider(SocialProvider.APPLE)
                        .nickname(nickname)
                        .email(appleUserInfo.getEmail())
                        .level(1)
                        .exp(0)
                        .gold(0)
                        .diamond(0)
                        .onboardingCompleted(false)
                        .inviteCode(UUID.randomUUID().toString().substring(0, 8))
                        .build()
            );
        });

        // 4. 초대한 유저와 친구 관계 저장
        if (isNewUser && inviterCode != null) {
            userRepository.findByInviteCode(inviterCode).ifPresent(inviter -> {
                InvitedFriend invitedFriend = InvitedFriend.builder()
                        .fromUser(inviter) // 초대한 사람
                        .toUser(user)       // 새로 가입한 사람
                        .status(FriendStatus.ACCEPTED)
                        .build();
                invitedFriendRepository.save(invitedFriend);
            });
        }

        // 5. JWT 토큰 발급
        String jwtAccessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));
        String jwtRefreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()));
        
        // Redis에 refreshToken 저장
        redisTemplate.opsForValue().set(
                "refresh:userId:" + user.getId(),
                jwtRefreshToken,
                jwtUtil.getRefreshTokenValidity(),
                TimeUnit.MILLISECONDS
        );

        log.info("🍎 애플 로그인 완료 - userId: {}, isNewUser: {}", user.getId(), isNewUser);

        // 6. 최종 응답
        return AuthResponseDto.LoginResult.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .isNewUser(isNewUser)
                .kakaoAccessToken(null)
                .kakaoRefreshToken(null)
                .build();
    }

}