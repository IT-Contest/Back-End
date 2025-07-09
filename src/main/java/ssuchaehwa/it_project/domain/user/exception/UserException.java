package ssuchaehwa.it_project.domain.user.exception;

import ssuchaehwa.it_project.global.error.code.status.BaseErrorCode;
import ssuchaehwa.it_project.global.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(BaseErrorCode code) {
        super(code);
    }
}
