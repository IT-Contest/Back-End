package ssuchaehwa.it_project.domain.login.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ResponseBody;
import ssuchaehwa.it_project.domain.login.application.LoginService;
import ssuchaehwa.it_project.domain.login.domain.KakaoOAuthClient;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto;
import ssuchaehwa.it_project.domain.user.entity.User;
import ssuchaehwa.it_project.domain.user.repository.UserRepository;
import ssuchaehwa.it_project.global.config.security.jwt.JwtUtil;
import ssuchaehwa.it_project.global.config.security.auth.UserPrincipal;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TestController {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final LoginService loginService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.oauth.kakao.js-key}")
    private String kakaoApiKey;

    @Value("${spring.oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @GetMapping("/")
    public String loginPage(Model model) {
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        model.addAttribute("kakaoRedirectUri", kakaoRedirectUri);
        return "index";
    }

    @GetMapping("/login/oauth/kakao")
    public String kakaoCallback(@RequestParam String code, Model model) {
        // 1. 카카오 토큰 및 사용자 정보 요청
        AuthResponseDto.KakaoToken kakaoToken = kakaoOAuthClient.requestToken(code);
        AuthResponseDto.KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(kakaoToken.getAccessToken());

        // 2. Optional 기반 null-safe 파싱
        Optional<AuthResponseDto.KakaoUserInfo.KakaoAccount> kakaoAccountOpt = Optional.ofNullable(userInfo.getKakaoAccount());

        String nickname = kakaoAccountOpt
                .map(AuthResponseDto.KakaoUserInfo.KakaoAccount::getProfile)
                .map(AuthResponseDto.KakaoUserInfo.KakaoAccount.Profile::getNickname)
                .orElse("게스트");

        String profileImageUrl = kakaoAccountOpt
                .map(AuthResponseDto.KakaoUserInfo.KakaoAccount::getProfile)
                .map(AuthResponseDto.KakaoUserInfo.KakaoAccount.Profile::getProfileImageUrl)
                .orElse(null);

        // 3. 디버깅 로그로 확인
        log.info("✅ [카카오 로그인 응답] id={}, nickname={}, profileImageUrl={}",
                userInfo.getId(), nickname, profileImageUrl);

        // 4. 기존 사용자 조회 또는 신규 저장
        User user = userRepository.findBySocialId(String.valueOf(userInfo.getId()))
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialId(String.valueOf(userInfo.getId()))
                                .nickname(nickname)
                                .profileImageUrl(profileImageUrl)
                                .level(1)
                                .exp(0)
                                .gold(0)
                                .diamond(0)
                                .onboardingCompleted(false)
                                .build()
                ));

        // 5. JWT 발급
        String jwtAccessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));
        String jwtRefreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()));

        // 6. 모델에 전달
        model.addAttribute("authCode", code);
        model.addAttribute("kakaoAccessToken", kakaoToken.getAccessToken());
        model.addAttribute("kakaoRefreshToken", kakaoToken.getRefreshToken());
        model.addAttribute("accessToken", jwtAccessToken);
        model.addAttribute("refreshToken", jwtRefreshToken);

        return "kakaoCallback";
    }
}