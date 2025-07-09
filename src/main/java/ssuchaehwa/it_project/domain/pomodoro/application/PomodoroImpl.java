package ssuchaehwa.it_project.domain.pomodoro.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro;
import ssuchaehwa.it_project.domain.pomodoro.domain.repository.PomodoroRepository;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroRequestDTO;
import ssuchaehwa.it_project.domain.pomodoro.dto.PomodoroResponseDTO;
import ssuchaehwa.it_project.domain.pomodoro.exception.PomodoroException;
import ssuchaehwa.it_project.domain.user.entity.User;
import ssuchaehwa.it_project.domain.user.repository.UserRepository;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;
import ssuchaehwa.it_project.domain.pomodoro.converter.PomodoroConverter;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PomodoroImpl implements PomodoroService {

    private final PomodoroRepository pomodoroRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public PomodoroResponseDTO.PomodoroCompleteResponse completePomodoro(Long userId, PomodoroRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PomodoroException(ErrorStatus.NO_SUCH_USER));

        int rewardExp = 10;
        int rewardGold = 5;

        user.addExp(rewardExp);
        user.addGold(rewardGold);

        Pomodoro session = Pomodoro.builder()
                .user(user)
                .startTime(LocalDateTime.now().minusMinutes(request.getDurationMinutes()))
                .endTime(LocalDateTime.now())
                .rewardExp(rewardExp)
                .rewardGold(rewardGold)
                .build();

        pomodoroRepository.save(session);

        return PomodoroConverter.toCompleteResponse(session);
    }
}