package ssuchaehwa.it_project.domain.analysis.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.analysis.domain.entity.CoachingRecord;
import ssuchaehwa.it_project.domain.analysis.domain.repository.CoachingRecordRepository;
import ssuchaehwa.it_project.domain.analysis.dto.CoachingRequestDTO;
import ssuchaehwa.it_project.domain.analysis.dto.CoachingResponseDTO;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.user.domain.repository.UserRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CoachingServiceImplTest {

    @Autowired
    private CoachingService coachingService;

    @Autowired
    private CoachingRecordRepository coachingRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("하루 제한 테스트 - 첫 번째 요청은 성공해야 함")
    void testFirstRequest_ShouldSucceed() {
        // Given: 테스트용 사용자 생성
        User testUser = createTestUser("test@example.com");

        CoachingRequestDTO request = CoachingRequestDTO.builder()
                .analysisType("DAILY")
                .questOrPomodoro("QUEST")
                .build();

        // When: 첫 번째 AI 코칭 요청
        CoachingResponseDTO response = coachingService.requestAICoaching(testUser.getId(), request);

        // Then: 분석 가능해야 함
        assertThat(response.isCanAnalyze()).isTrue();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getCoachingContent()).isNotNull();
    }

    @Test
    @DisplayName("하루 제한 테스트 - 오늘 이미 분석한 경우 제한되어야 함")
    void testSecondRequest_ShouldBeLimited() {
        // Given: 테스트용 사용자 생성 및 오늘 분석 기록 생성
        User testUser = createTestUser("test2@example.com");
        createTodayCoachingRecord(testUser);

        CoachingRequestDTO request = CoachingRequestDTO.builder()
                .analysisType("DAILY")
                .questOrPomodoro("QUEST")
                .build();

        // When: AI 코칭 요청
        CoachingResponseDTO response = coachingService.requestAICoaching(testUser.getId(), request);

        // Then: 제한되어야 함
        assertThat(response.isCanAnalyze()).isFalse();
        assertThat(response.getMessage()).isEqualTo("daily_limit_reached");
        assertThat(response.getCoachingContent()).isNull();
    }

    @Test
    @DisplayName("하루 제한 테스트 - 어제 분석한 경우는 오늘 요청 가능해야 함")
    void testRequestAfterYesterday_ShouldSucceed() {
        // Given: 테스트용 사용자 생성 및 어제 분석 기록 생성
        User testUser = createTestUser("test3@example.com");
        createYesterdayCoachingRecord(testUser);

        CoachingRequestDTO request = CoachingRequestDTO.builder()
                .analysisType("DAILY")
                .questOrPomodoro("QUEST")
                .build();

        // When: AI 코칭 요청
        CoachingResponseDTO response = coachingService.requestAICoaching(testUser.getId(), request);

        // Then: 분석 가능해야 함 (어제 기록은 오늘에 영향 없음)
        assertThat(response.isCanAnalyze()).isTrue();
        assertThat(response.getMessage()).isNull();
    }

    // 헬퍼 메서드들
    private User createTestUser(String email) {
        User user = User.builder()
                .socialId("test-social-id-" + System.currentTimeMillis())
                .provider(ssuchaehwa.it_project.domain.model.enums.SocialProvider.APPLE)
                .email(email)
                .nickname("테스트유저")
                .inviteCode("TEST" + System.currentTimeMillis())
                .build();
        return userRepository.save(user);
    }

    private void createTodayCoachingRecord(User user) {
        CoachingRecord record = CoachingRecord.createCoachingRecord(
                user,
                CoachingRecord.AnalysisType.DAILY,
                CoachingRecord.QuestOrPomodoro.QUEST,
                "오늘의 코칭 내용"
        );
        coachingRecordRepository.save(record);
    }

    private void createYesterdayCoachingRecord(User user) {
        CoachingRecord record = CoachingRecord.builder()
                .user(user)
                .analysisType(CoachingRecord.AnalysisType.DAILY)
                .questOrPomodoro(CoachingRecord.QuestOrPomodoro.QUEST)
                .analysisDate(LocalDate.now().minusDays(1))
                .coachingContent("어제의 코칭 내용")
                .build();
        coachingRecordRepository.save(record);
    }
}
