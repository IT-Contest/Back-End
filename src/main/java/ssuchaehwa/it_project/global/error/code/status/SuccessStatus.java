package ssuchaehwa.it_project.global.error.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 기본 성공
    OK(HttpStatus.OK, "COMMON_200", "성공입니다."),

    // quest
    QUEST_CREATED(HttpStatus.CREATED, "QUEST_201", "퀘스트가 성공적으로 생성되었습니다."),

    // 뽀모도로
    POMODORO_COMPLETED(HttpStatus.CREATED, "POMODORO_201", "뽀모도로가 성공적으로 완료되었습니다."),

    // token
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "TOKEN_200", "토큰 재발급 성공");

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
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
