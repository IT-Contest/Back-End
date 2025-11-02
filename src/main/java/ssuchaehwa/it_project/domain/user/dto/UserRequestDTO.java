package ssuchaehwa.it_project.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserRequestDTO {

    // 약관 요청
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserTermRequest {
        private List<Long> termIds;
    }

    // 약관 생성
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TermCreateRequest {
        private String title;
        private String url;
        private boolean isRequired;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FcmTokenRequest {
        private String token;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyNotificationRequest {
        private boolean enabled;
    }


}
