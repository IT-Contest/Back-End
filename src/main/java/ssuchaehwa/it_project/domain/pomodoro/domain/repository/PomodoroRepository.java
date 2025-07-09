package ssuchaehwa.it_project.domain.pomodoro.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssuchaehwa.it_project.domain.pomodoro.domain.entity.Pomodoro;

public interface PomodoroRepository extends JpaRepository<Pomodoro, Long> {
}