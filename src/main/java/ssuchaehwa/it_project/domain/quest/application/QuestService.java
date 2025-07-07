package ssuchaehwa.it_project.domain.quest.application;

import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.domain.user.entity.User;

public interface QuestService {

    QuestResponseDTO.QuestCreateResponse createQuest(QuestRequestDTO.QuestCreateRequest request);
}
