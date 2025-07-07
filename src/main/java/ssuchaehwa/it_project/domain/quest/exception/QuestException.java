package ssuchaehwa.it_project.domain.quest.exception;

import ssuchaehwa.it_project.global.error.code.status.BaseErrorCode;
import ssuchaehwa.it_project.global.exception.GeneralException;

public class QuestException extends GeneralException {
    public QuestException(BaseErrorCode code) {
        super(code);
    }
}
