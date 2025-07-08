package ssuchaehwa.it_project.domain.quest.converter;

import ssuchaehwa.it_project.domain.quest.domain.entity.Quest;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;

public class QuestConverter {

    // 퀘스트 생성
    public static QuestResponseDTO.QuestCreateResponse toQuestCreateResponse(Quest quest) {
        return QuestResponseDTO.QuestCreateResponse.builder()
                .content(quest.getTitle())
                .questType(quest.getQuestType())
                .startDate(quest.getStartDate())
                .dueDate(quest.getDueDate())
                .startTime(quest.getStartTime())
                .endTime(quest.getEndTime())
                .build();
    }
}
