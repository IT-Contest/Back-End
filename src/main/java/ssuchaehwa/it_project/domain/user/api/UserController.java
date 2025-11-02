package ssuchaehwa.it_project.domain.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssuchaehwa.it_project.domain.user.application.UserService;
import ssuchaehwa.it_project.domain.user.dto.UserRequestDTO;
import ssuchaehwa.it_project.domain.user.dto.UserResponseDTO;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.config.security.auth.UserPrincipal;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/terms")
    @Operation(summary = "약관 전체 조회", description = "모든 약관 목록을 조회합니다. 필수 여부(isRequired) 포함")
    @ApiResponses({
            @ApiResponse(responseCode = "TERM_200", description = "약관 목록 조회 성공")
    })
    public BaseResponse<List<UserResponseDTO.TermResponse>> getAllTerms() {
        return BaseResponse.onSuccess(
                SuccessStatus.TERM_FETCH_SUCCESS,
                userService.getAllTerms()
        );
    }

    @PostMapping("/terms/agree")
    @Operation(summary = "약관 동의 저장", description = "사용자가 선택한 약관에 동의합니다. 필수 약관이 누락되면 400 에러가 발생합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TERM_200", description = "약관 동의 성공"),
            @ApiResponse(responseCode = "TERM_4001", description = "필수 약관에 동의하지 않았습니다."),
            @ApiResponse(responseCode = "TERM_402", description = "존재하지 않는 약관입니다."),
    })
    public BaseResponse<Void> agreeTerms(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UserRequestDTO.UserTermRequest request) {
        userService.agreeTerms(principal.getId(), request);
        return BaseResponse.onSuccess(SuccessStatus.TERM_AGREE_SUCCESS, null);
    }

    @GetMapping("/terms/check")
    @Operation(summary = "필수 약관 동의 여부 확인", description = "해당 사용자가 모든 필수 약관에 동의했는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TERM_200", description = "동의 여부 반환 성공"),
    })
    public BaseResponse<Boolean> checkTerms(
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean agreed = userService.hasAgreedAllRequired(principal.getId());
        return BaseResponse.onSuccess(SuccessStatus.TERM_CHECK_SUCCESS, agreed);
    }

    // 약관 생성
    @PostMapping("/terms/create")
    @Operation(summary = "약관 생성", description = "관리자가 새로운 약관을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "약관 생성 성공"),
    })
    public BaseResponse<UserResponseDTO.TermResponse> createTerm(
            @RequestBody UserRequestDTO.TermCreateRequest request) {
        return BaseResponse.onSuccess(
                SuccessStatus.TERMS_CREATE_SUCCESS,
                userService.createTerm(request));
    }

    // push token
    @PostMapping("/fcm-token")
    @Operation(summary = "FCM 토큰 저장", description = "로그인한 사용자의 FCM 토큰을 갱신합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "FCM 토큰 저장 성공"),
    })
    public BaseResponse<    Void> updateFcmToken(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UserRequestDTO.FcmTokenRequest request
    ) {
        userService.updateFcmToken(principal.getId(), request.getToken());
        log.info("✅ FCM 토큰 저장 요청: userId={}, token={}", principal.getId(), request.getToken());
        return BaseResponse.onSuccess(SuccessStatus.USER_UPDATE_SUCCESS, null);
    }

    @PostMapping("/onboarding/complete")
    @Operation(summary = "온보딩 완료", description = "사용자의 온보딩을 완료하고 보상을 지급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "온보딩 완료 성공"),
    })
    public BaseResponse<UserResponseDTO.OnboardingCompleteResponse> completeOnboarding(
            @AuthenticationPrincipal UserPrincipal principal) {
        return BaseResponse.onSuccess(
                SuccessStatus.ONBOARDING_COMPLETE_SUCCESS,
                userService.completeOnboarding(principal.getId()));
    }

    @PatchMapping("/notifications/party")
    @Operation(summary = "파티 초대장 알림 설정 변경", description = "사용자의 파티 초대장 푸시 알림 수신 여부를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파티 초대장 알림 설정 변경 성공"),
            @ApiResponse(responseCode = "USER_404", description = "존재하지 않는 사용자입니다."),
    })
    public BaseResponse<UserResponseDTO.PartyNotificationResponse> updatePartyNotification(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UserRequestDTO.PartyNotificationRequest request
    ) {
        log.info("📩 파티 알림 설정 변경 요청: userId={}, enabled={}", principal.getId(), request.isEnabled());

        UserResponseDTO.PartyNotificationResponse response =
                userService.updatePartyNotification(principal.getId(), request.isEnabled());

        return BaseResponse.onSuccess(SuccessStatus.USER_UPDATE_SUCCESS, response);
    }


}
