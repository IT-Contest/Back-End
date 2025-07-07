package ssuchaehwa.it_project.domain.login.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ssuchaehwa.it_project.domain.login.application.LoginService;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    // [앱용] 카카오 accessToken 을 받아 JWT 토큰 발급
    @Operation(summary = "카카오 로그인 - iOS/Flutter", description = "앱에서 받은 카카오 access token을 사용해 JWT 토큰을 발급")
    @PostMapping("/login/kakao/token")
    public ResponseEntity<AuthResponseDto.LoginResult> kakaoLoginWithAccessToken(@RequestBody Map<String, String> body) {
        String kakaoAccessToken = body.get("accessToken");
        // 이 메서드는 아직 LoginService에 없으니 구현 필요
        AuthResponseDto.LoginResult result = loginService.kakaoLoginWithAccessToken(kakaoAccessToken);
        return ResponseEntity.ok(result);
    }
}