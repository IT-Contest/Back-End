package ssuchaehwa.it_project.domain.analysis.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ssuchaehwa.it_project.domain.analysis.dto.AnalysisDataDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatGPTService {
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;
    
    @Value("${openai.model:gpt-4}")
    private String model;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * ChatGPT API를 호출하여 AI 코칭 생성
     * @param analysisData 분석 데이터
     * @return AI 코칭 내용
     */
    public String generateCoaching(AnalysisDataDTO analysisData) {
        try {
            String prompt = generatePrompt(analysisData);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", new Object[]{
                Map.of("role", "system", "content", 
                "You are an executive productivity coach. Be concise, pragmatic, and action-oriented. Only output valid JSON matching the provided schema. Do not include commentary outside JSON. Also, output must be written in Korean limited by 300 words."),
                Map.of("role", "user", "content", prompt)
            });
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            log.info("ChatGPT API 호출 시작 - 분석 타입: {}, 대상: {}", 
                    analysisData.getAnalysisType(), analysisData.getQuestOrPomodoro());
            
            // 실제 ChatGPT API 호출
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
            log.info("ChatGPT API 응답 수신 완료");
            
            // 응답에서 content 추출
            String content = extractContentFromResponse(response);
            
            // JSON 검증 및 파싱
            return validateAndParseJSON(content);
            
            
        } catch (Exception e) {
            log.error("ChatGPT API 호출 중 오류 발생", e);
            return "죄송합니다. AI 코칭 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }
    
    /**
     * ChatGPT 프롬프트 생성
     */
    private String generatePrompt(AnalysisDataDTO analysisData) {
        StringBuilder prompt = new StringBuilder();
        
        String periodText = getPeriodText(analysisData.getAnalysisType());
        prompt.append("IMPORTANT: You are analyzing ").append(analysisData.getAnalysisType().toUpperCase())
              .append(" data for the last ").append(periodText)
              .append(". The analysis_type in your response MUST be \"").append(analysisData.getAnalysisType().toUpperCase()).append("\".\n\n");
        
        // 데이터 부족 체크
        boolean hasQuestData = analysisData.getQuests() != null && !analysisData.getQuests().isEmpty();
        boolean hasPomodoroData = analysisData.getPomodoros() != null && !analysisData.getPomodoros().isEmpty();
        
        if (!hasQuestData && !hasPomodoroData) {
            prompt.append("WARNING: NO DATA AVAILABLE for analysis. The user has no ").append(analysisData.getQuestOrPomodoro().toLowerCase())
                  .append(" data in the specified period. You must acknowledge this in your response.\n\n");
        }
        
        prompt.append("Please respond in the following JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"analysis_type\": \"").append(analysisData.getAnalysisType().toUpperCase()).append("\",\n");
        prompt.append("  \"analysis_target\": \"").append(analysisData.getQuestOrPomodoro().toUpperCase()).append("\",\n");
        prompt.append("  \"key_insights\": \"주요 인사이트 내용\",\n");
        prompt.append("  \"improvement_suggestions\": \"개선 제안 내용\",\n");
        prompt.append("  \"action_plan\": \"구체적 행동 계획\",\n");
        prompt.append("  \"expected_effects\": \"예상 효과\"\n");
        prompt.append("}\n\n");
        
        prompt.append("Inputs:\n");
        
        // 퀘스트 데이터 추가
        if (hasQuestData) {
            prompt.append("퀘스트 데이터:\n");
            for (AnalysisDataDTO.QuestDataDTO quest : analysisData.getQuests()) {
                prompt.append("- 퀘스트명: ").append(quest.getTitle())
                      .append(", ID: ").append(quest.getQuestId())
                      .append(", 우선순위: ").append(quest.getPriority())
                      .append(", 시작/종료시간: ").append(quest.getStartTime()).append("/").append(quest.getEndTime())
                      .append(", 시작일/마감일: ").append(quest.getStartDate()).append("/").append(quest.getDueDate())
                      .append(", 완료여부: ").append(quest.getCompletionStatus())
                      .append(", 분류: ").append(quest.getQuestType()).append("\n");
            }
        } else {
            prompt.append("퀘스트 데이터: 없음\n");
        }
        
        // 뽀모도로 데이터 추가
        if (hasPomodoroData) {
            prompt.append("뽀모도로 데이터:\n");
            for (AnalysisDataDTO.PomodoroDataDTO pomodoro : analysisData.getPomodoros()) {
                prompt.append("- 수행일자: ").append(pomodoro.getPerformanceDate())
                      .append(", 수행시간: ").append(pomodoro.getDurationMinutes()).append("분")
                      .append(", 일자별 수행횟수: ").append(pomodoro.getDailyCount()).append("\n");
            }
        } else {
            prompt.append("뽀모도로 데이터: 없음\n");
        }
        
        prompt.append("\n분석 기간: ").append(analysisData.getAnalysisPeriod());
        
        // 데이터 부족 시 추가 지시사항
        if (!hasQuestData && !hasPomodoroData) {
            prompt.append("\n\nIMPORTANT: Since no data is available, your response should:\n");
            prompt.append("1. Acknowledge that there is insufficient data for meaningful analysis\n");
            prompt.append("2. Suggest starting to use the system to collect data\n");
            prompt.append("3. Provide general productivity tips instead of specific analysis\n");
        }
        
        return prompt.toString();
    }

    /**
     * 분석 타입에 따른 기간 텍스트 반환
     */
    private String getPeriodText(String analysisType) {
        switch (analysisType.toUpperCase()) {
            case "DAILY": return "7 days";
            case "WEEKLY": return "4 weeks";
            case "MONTHLY": return "12 months";
            case "YEARLY": return "10 years";
            default: return "7 days";
        }
    }

    
    /**
     * ChatGPT 응답에서 content 추출
     */
    private String extractContentFromResponse(Map<String, Object> response) {
        try {
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    if (firstChoice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        if (message.containsKey("content")) {
                            return (String) message.get("content");
                        }
                    }
                }
            }
            log.warn("ChatGPT 응답에서 content를 찾을 수 없음: {}", response);
            return "{\"error\": \"응답 형식 오류\", \"message\": \"ChatGPT 응답을 파싱할 수 없습니다.\"}";
        } catch (Exception e) {
            log.error("ChatGPT 응답 파싱 중 오류", e);
            return "{\"error\": \"응답 파싱 실패\", \"message\": \"ChatGPT 응답 처리 중 오류가 발생했습니다.\"}";
        }
    }
    
    /**
     * JSON 검증 및 파싱
     */
    private String validateAndParseJSON(String jsonString) {
        try {
            // JSON 파싱 시도
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            
            // 최소한의 스키마 검증 (영어 키 이름 체크)
            if (jsonNode.has("analysis_type") && jsonNode.has("analysis_target")) {
                log.info("JSON 검증 성공 - 분석 타입: {}, 대상: {}", 
                        jsonNode.get("analysis_type").asText(), jsonNode.get("analysis_target").asText());
                return jsonString;
            } else {
                log.warn("JSON 스키마 검증 실패 - 필수 키 누락. 현재 키: {}", jsonNode.fieldNames());
                return "{\"error\": \"JSON 스키마 검증 실패\", \"message\": \"필수 키가 누락되었습니다.\"}";
            }
            
        } catch (Exception e) {
            log.error("JSON 파싱 실패", e);
            return "{\"error\": \"JSON 파싱 실패\", \"message\": \"유효하지 않은 JSON 형식입니다.\"}";
        }
    }
}
