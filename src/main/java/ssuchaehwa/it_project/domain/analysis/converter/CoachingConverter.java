package ssuchaehwa.it_project.domain.analysis.converter;

import ssuchaehwa.it_project.domain.analysis.domain.entity.CoachingRecord;
import ssuchaehwa.it_project.domain.analysis.dto.CoachingRecordDTO;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CoachingConverter {

    // 코칭 기록을 DTO로 변환
    public static CoachingRecordDTO toCoachingRecordDTO(CoachingRecord record) {
        String contentSummary = record.getCoachingContent();
        if (contentSummary.length() > 100) {
            contentSummary = contentSummary.substring(0, 100) + "...";
        }

        return CoachingRecordDTO.builder()
                .id(record.getId())
                .analysisDate(record.getAnalysisDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                .analysisType(convertAnalysisTypeToKorean(record.getAnalysisType()))
                .questOrPomodoro(convertQuestOrPomodoroToKorean(record.getQuestOrPomodoro()))
                .coachingContentSummary(contentSummary)
                .coachingContent(record.getCoachingContent())
                .createdAt(record.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                .build();
    }

    // 코칭 기록 목록을 DTO 목록으로 변환
    public static List<CoachingRecordDTO> toCoachingRecordDTOList(List<CoachingRecord> records) {
        return records.stream()
                .map(CoachingConverter::toCoachingRecordDTO)
                .collect(Collectors.toList());
    }

    // 코칭 기록 조회 응답 변환
    public static BaseResponse<List<CoachingRecordDTO>> toCoachingRecordListResponse(
            List<CoachingRecordDTO> records) {
        return BaseResponse.onSuccess(SuccessStatus.OK, records);
    }

    // 분석 타입을 한국어로 변환
    private static String convertAnalysisTypeToKorean(CoachingRecord.AnalysisType type) {
        switch (type) {
            case DAILY: return "일일";
            case WEEKLY: return "주간";
            case MONTHLY: return "월간";
            case YEARLY: return "연간";
            default: return type.name();
        }
    }

    // 분석 대상을 한국어로 변환
    private static String convertQuestOrPomodoroToKorean(CoachingRecord.QuestOrPomodoro target) {
        switch (target) {
            case QUEST: return "퀘스트";
            case POMODORO: return "뽀모도로";
            default: return target.name();
        }
    }
}
