package ssuchaehwa.it_project.domain.login.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssuchaehwa.it_project.domain.login.application.LoginService;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto;
import ssuchaehwa.it_project.domain.login.dto.AuthRequestDto;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    // 카카오 accessToken 을 받아 JWT access/refresh 토큰을 발급하고 refreshToken은 Redis에 저장
    @Operation(
        summary = "카카오 로그인",
        description = "앱에서 받은 카카오 access token을 사용해 JWT access/refresh 토큰을 발급하고 refresh token은 Redis에 저장"
    )
    @PostMapping("/login/kakao")
    public ResponseEntity<AuthResponseDto.LoginResult> kakaoLoginWithAccessToken(@RequestBody AuthRequestDto.KakaoAccessToken request) {
        String kakaoAccessToken = request.getAccessToken();
        AuthResponseDto.LoginResult result = loginService.kakaoLoginWithAccessToken(kakaoAccessToken);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "토큰 재발급 - refreshToken",
            description = "만료된 accessToken을 대체할 새로운 accessToken과 refreshToken을 발급합니다. \n\n" +
                    "클라이언트는 userId와 refreshToken을 함께 전송해야 하며, 서버는 Redis에 저장된 refreshToken과 일치하는지 검증 후 새로운 JWT를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "TOKEN_200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "TOKEN_401", description = "유효하지 않거나 만료된 refreshToken"),
            @ApiResponse(responseCode = "TOKEN_404", description = "Redis에 해당 userId의 refreshToken이 존재하지 않음")
    })
    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponseDto.LoginResult> refreshToken(@RequestBody AuthRequestDto.RefreshToken request) {
        AuthResponseDto.LoginResult result = loginService.refreshToken(request.getUserId(), request.getRefreshToken());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게스트 로그인", description = "UUID 기반의 게스트 로그인 - JWT 발급")
    @PostMapping("/login/guest")
    public ResponseEntity<AuthResponseDto.LoginResult> guestLogin(@RequestBody AuthRequestDto.GuestRequest request) {
        AuthResponseDto.LoginResult result = loginService.guestLogin(request.getDeviceId());
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "자동 로그인 - accessToken 인증",
            description = "앱 실행 시 저장된 accessToken이 유효한지 확인하여 자동 로그인 수행"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "COMMON_200", description = "자동 로그인 성공"),
            @ApiResponse(responseCode = "TOKEN_402", description = "유효하지 않거나 잘못된 accessToken입니다."),
            @ApiResponse(responseCode = "USER_4001", description = "유저가 존재하지 않습니다.")
    })
    @GetMapping("/auto-login")
    public ResponseEntity<BaseResponse<AuthResponseDto.AutoLoginResult>> autoLogin(@RequestHeader("Authorization") String accessTokenHeader) {
        String accessToken = accessTokenHeader.replace("Bearer ", "");
        AuthResponseDto.AutoLoginResult result = loginService.autoLogin(accessToken);

        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.OK, result));
    }

    @Operation(
            summary = "로그아웃",
            description = "accessToken을 기반으로 Redis에서 refreshToken을 제거하여 로그아웃합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "COMMON_200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "TOKEN_402", description = "유효하지 않거나 잘못된 accessToken입니다."),
            @ApiResponse(responseCode = "USER_4001", description = "유저가 존재하지 않습니다.")
    })
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout(@RequestHeader("Authorization") String accessTokenHeader) {
        String accessToken = accessTokenHeader.replace("Bearer ", "");
        loginService.logout(accessToken);
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.OK, "로그아웃되었습니다."));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "accessToken을 기반으로 DB에서 유저를 삭제하고 Redis에서 refreshToken도 제거합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "COMMON_200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "TOKEN_402", description = "유효하지 않거나 잘못된 accessToken입니다."),
            @ApiResponse(responseCode = "USER_4001", description = "유저가 존재하지 않습니다.")
    })
    @DeleteMapping("/withdraw")
    public ResponseEntity<BaseResponse<String>> withdraw(@RequestHeader("Authorization") String accessTokenHeader) {
        String accessToken = accessTokenHeader.replace("Bearer ", "");
        loginService.withdraw(accessToken);
        return ResponseEntity.ok(BaseResponse.onSuccess(SuccessStatus.OK, "회원 탈퇴가 완료되었습니다."));
    }
}