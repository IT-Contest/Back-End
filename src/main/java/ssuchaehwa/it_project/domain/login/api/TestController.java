package ssuchaehwa.it_project.domain.login.api;

import lombok.RequiredArgsConstructor;
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
        AuthResponseDto.KakaoToken kakaoToken = kakaoOAuthClient.requestToken(code);
        AuthResponseDto.KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(kakaoToken.getAccessToken());

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

        String jwtAccessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));
        String jwtRefreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()));

        model.addAttribute("authCode", code);
        model.addAttribute("kakaoAccessToken", kakaoToken.getAccessToken());
        model.addAttribute("kakaoRefreshToken", kakaoToken.getRefreshToken());
        model.addAttribute("accessToken", jwtAccessToken);
        model.addAttribute("refreshToken", jwtRefreshToken);
        return "kakaoCallback";
    }
}