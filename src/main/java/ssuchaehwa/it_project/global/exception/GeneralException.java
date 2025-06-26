package ssuchaehwa.it_project.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ssuchaehwa.it_project.global.error.code.status.BaseErrorCode;
import ssuchaehwa.it_project.global.error.code.status.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
