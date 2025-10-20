package ssuchaehwa.it_project.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialProvider {
    KAKAO("카카오"),
    APPLE("애플"),
    GUEST("게스트");

    private final String description;
}