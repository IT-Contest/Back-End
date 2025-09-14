package ssuchaehwa.it_project.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponseDTO {

    // 약관 응답
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TermResponse {
        private Long id;
        private String title;
        private String content;
        private boolean isRequired;
    }
}
