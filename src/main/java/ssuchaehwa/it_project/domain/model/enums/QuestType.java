package ssuchaehwa.it_project.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestType {
    DAILY("일일"),
    WEEKLY("주간"),
    MONTHLY("월간"),
    YEARLY("연간");

    private final String label;
}