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
    
    // 온보딩 완료 응답
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OnboardingCompleteResponse {
        private Long userId;
        private int exp;
        private int level;
        private boolean onboardingCompleted;
        private int rewardExp;
    }
}
