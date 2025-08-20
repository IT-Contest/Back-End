package ssuchaehwa.it_project.global.error.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode{

    // 기본 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // user
    NO_SUCH_USER(HttpStatus.BAD_REQUEST, "USER_4001", "유저가 존재하지 않습니다."),

    // quest
    NO_SUCH_QUEST(HttpStatus.BAD_REQUEST, "QUEST_4001", "해당 퀘스트가 존재하지 않습니다."),
    QUEST_STATUS_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QUEST_4002", "퀘스트의 상태를 변경할 수 없습니다."),
    NO_PARTY_INVITATION(HttpStatus.BAD_REQUEST, "QUEST_4003", "초대 받은 파티가 존재하지 않습니다."),
    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "QUEST_404", "해당 퀘스트를 찾을 수 없습니다."),
    QUEST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "QUEST_403", "퀘스트에 대한 접근 권한이 없습니다."),

    // token
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_401", "유효하지 않거나 만료된 refreshToken입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_402", "유효하지 않거나 잘못된 accessToken입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOKEN_404", "Redis에 해당 userId의 refreshToken이 존재하지 않습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
