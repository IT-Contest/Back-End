package ssuchaehwa.it_project.domain.pomodoro.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro;
import ssuchaehwa.it_project.domain.pomodoro.domain.entity.PomodoroStatus;
import ssuchaehwa.it_project.domain.pomodoro.domain.repository.PomodoroRepository;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;
import ssuchaehwa.it_project.domain.pomodoro.exception.PomodoroException;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.user.domain.repository.UserRepository;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;
import ssuchaehwa.it_project.domain.pomodoro.converter.PomodoroConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
        
        user.addExp(session.getRewardExp());
        user.addGold(session.getRewardGold());

        return PomodoroConverter.toCompleteResponse(session);
    }

    @Transactional
    @Override
    public void cancelPomodoro(Long userId, Long pomodoroId) {
        Pomodoro session = pomodoroRepository.findById(pomodoroId)
                .orElseThrow(() -> new PomodoroException(ErrorStatus.NO_SUCH_POMODORO));
        
        session.cancel();
    }
}