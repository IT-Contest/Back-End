package ssuchaehwa.it_project.domain.pomodoro.exception;

import ssuchaehwa.it_project.global.error.code.status.BaseErrorCode;
import ssuchaehwa.it_project.global.exception.GeneralException;

public class PomodoroException extends GeneralException {
    public PomodoroException(BaseErrorCode code) {
        super(code);
    }
}
