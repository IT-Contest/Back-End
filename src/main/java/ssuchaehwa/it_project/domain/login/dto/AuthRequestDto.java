package ssuchaehwa.it_project.domain.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

public class AuthRequestDto {

    // Flutter 에서 카카오 로그인을 통해 발급받은 accessToken 을 서버에 전달
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccessToken {
        private String accessToken;

        @Nullable
        private String inviterCode; // 초대 코드 (딥링크로 받은 값, 없을 수도 있음)
    }

    // 클라이언트가 토큰 재발급 요청을 보낼 때 사용하는 요청 데이터 객체
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshToken {
        private Long userId;
        private String refreshToken;
    }

    // 게스트 유저 생성 요청 dto
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestRequest {
        private String deviceId; // 예: UUID 또는 디바이스 고유값
    }

    // Apple 로그인 요청 dto
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppleIdentityToken {
        private String identityToken; // Apple에서 발급받은 Identity Token

        @Nullable
        private String name; // 사용자 이름 (Apple에서 제공, 초회 로그인 시에만 제공될 수 있음)

        @Nullable
        private String inviterCode; // 초대 코드 (딥링크로 받은 값, 없을 수도 있음)
    }

    // Apple Mock 로그인 요청 dto
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppleMockRequest {
        private String sub; // Apple 고유 사용자 ID
        private String email; // 이메일
        private Boolean emailVerified; // 이메일 검증 여부
        
        @Nullable
        private String inviterCode; // 초대 코드
    }
}