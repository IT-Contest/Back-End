package ssuchaehwa.it_project.domain.login.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponseDto {

    // 카카오 Access/Refresh Token 응답 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoToken {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Long expiresIn;

        @JsonProperty("refresh_token_expires_in")
        private Long refreshTokenExpiresIn;
    }

    // 카카오 유저 정보 응답 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoUserInfo {

        private Long id;

        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class KakaoAccount {

            private String email;

            private Profile profile;

            @Getter
            @Setter
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Profile {
                private String nickname;

                @JsonProperty("profile_image_url")
                private String profileImageUrl;
            }
        }
    }

    // 최종 로그인 응답 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResult {
        private String accessToken;
        private String refreshToken;
        private boolean isNewUser;
        private String kakaoAccessToken;
        private String kakaoRefreshToken;
    }
}