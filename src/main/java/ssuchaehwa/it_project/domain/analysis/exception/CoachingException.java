package ssuchaehwa.it_project.domain.analysis.exception;

import ssuchaehwa.it_project.global.exception.GeneralException;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;

public class CoachingException extends GeneralException {
    public CoachingException(ErrorStatus code) {
        super(code);
    }
}
