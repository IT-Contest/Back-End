package ssuchaehwa.it_project.domain.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequestDto {

    // Flutter 에서 카카오 로그인을 통해 발급받은 accessToken 을 서버에 전달
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccessToken {
        private String accessToken;
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
}