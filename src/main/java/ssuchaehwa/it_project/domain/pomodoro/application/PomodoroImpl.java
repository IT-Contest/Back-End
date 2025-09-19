package ssuchaehwa.it_project.domain.pomodoro.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro;
import ssuchaehwa.it_project.domain.pomodoro.domain.entity.PomodoroStatus;
import ssuchaehwa.it_project.domain.pomodoro.domain.repository.PomodoroRepository;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroRequestDTO;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;
import ssuchaehwa.it_project.domain.pomodoro.exception.PomodoroException;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.user.domain.repository.UserRepository;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;
import ssuchaehwa.it_project.domain.pomodoro.converter.PomodoroConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class PomodoroImpl implements PomodoroService {

    private final PomodoroRepository pomodoroRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Long startPomodoro(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PomodoroException(ErrorStatus.NO_SUCH_USER));

        Pomodoro session = Pomodoro.builder()
                .user(user)
                .startTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .endTime(null) // 시작 시점에는 endTime이 없음
                .status(PomodoroStatus.IN_PROGRESS) // 상태는 '진행중'
                .build();

        Pomodoro savedSession = pomodoroRepository.save(session);
        return savedSession.getId();
    }

    @Transactional
    @Override
    public PomodoroResponseDTO.PomodoroCompleteResponse completePomodoro(Long userId, Long pomodoroId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PomodoroException(ErrorStatus.NO_SUCH_USER));
        
        Pomodoro session = pomodoroRepository.findById(pomodoroId)
                .orElseThrow(() -> new PomodoroException(ErrorStatus.NO_SUCH_POMODORO));

        // 상태 변경 및 보상 로직
        session.complete();
        
        // 레벨업 체크를 위해 이전 레벨 저장
        int oldLevel = user.getLevel();
        user.addExpAndUpdateLevel(session.getRewardExp());
        user.addGoldAndUpdateLevel(session.getRewardGold());
        int newLevel = user.getLevel();
        
        if (newLevel > oldLevel) {
            log.info("🎉 뽀모도로 완료로 레벨업! {} -> {} (exp: {})", oldLevel, newLevel, user.getExp());
        }

        return PomodoroConverter.toCompleteResponse(session);
    }

    @Transactional
    @Override
    public void cancelPomodoro(Long userId, Long pomodoroId) {
        Pomodoro session = pomodoroRepository.findById(pomodoroId)
                .orElseThrow(() -> new PomodoroException(ErrorStatus.NO_SUCH_POMODORO));
        
        session.cancel();
    }
    
    @Transactional
    @Override
    public PomodoroResponseDTO.PomodoroCompleteResponse completeSession(Long userId, PomodoroRequestDTO.PomodoroCompleteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PomodoroException(ErrorStatus.NO_SUCH_USER));

        // 프론트엔드 데이터로 뽀모도로 세션 생성 및 즉시 완료 처리
        LocalDateTime startTime = request.getCompletedAt().minusMinutes(request.getTotalMinutes());
        LocalDateTime endTime = request.getCompletedAt();
        
        // 뽀모도로 세션 생성 (이미 완료된 상태로)
        Pomodoro session = Pomodoro.builder()
                .user(user)
                .startTime(startTime)
                .endTime(endTime)
                .status(PomodoroStatus.COMPLETED)
                .rewardExp(10)  // 고정 보상
                .rewardGold(5)  // 고정 보상
                .build();

        Pomodoro savedSession = pomodoroRepository.save(session);
        
        // 사용자에게 보상 지급 및 레벨 자동 계산
        int oldLevel = user.getLevel();
        user.addExpAndUpdateLevel(session.getRewardExp());
        user.addGoldAndUpdateLevel(session.getRewardGold());
        int newLevel = user.getLevel();
        
        if (newLevel > oldLevel) {
            log.info("🎉 뽀모도로 완료로 레벨업! {} -> {} (exp: {})", oldLevel, newLevel, user.getExp());
        }

        return PomodoroConverter.toCompleteResponse(savedSession);
    }
}