package ssuchaehwa.it_project.domain.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequestDto {

    // 카카오 로그인 요청 DTO (프론트에서 인가 코드 받은 후 보내는 것)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoLogin {
        private String authorizationCode; // 프론트에서 받은 카카오 인가 코드
        private String redirectUri;       // 리다이렉트 URI (선택)
    }
}