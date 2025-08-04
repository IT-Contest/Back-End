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
    QUEST_VIEW_SUCCESS(HttpStatus.OK, "QUEST_200", "퀘스트 리스트 조회를 완료했습니다"),
    QUEST_CREATED(HttpStatus.CREATED, "QUEST_201", "퀘스트가 성공적으로 생성되었습니다."),
    QUEST_UPDATED(HttpStatus.OK, "QUEST_202", "퀘스트가 성공적으로 수정되었습니다."),
    QUEST_DELETED(HttpStatus.OK, "QUEST_203", "퀘스트가 성공적으로 삭제되었습니다."),

    PARTY_CREATED(HttpStatus.CREATED, "PARTY_201", "파티가 성공적으로 생성되었습니다."),
    INVITE_FRIEND_CREATED(HttpStatus.CREATED, "INVITE_FRIEND_201", "친구 초대를 완료했습니다."),
    FRIEND_VIEW_SUCCESS(HttpStatus.OK, "FRIEND_200", "친구 리스트 조회를 완료했습니다."),
    QUEST_STATUS_CHANGE(HttpStatus.CREATED, "QUEST_201", "퀘스트의 상태가 성공적으로 변경되었습니다."),
    INVITE_PARTY_LIST_VIEW_SUCCESS(HttpStatus.OK, "PARTY_200", "초대 받은 파티 리스트 조회를 완료했습니다."),
    INVITE_PARTY_STATUS_CHANGE(HttpStatus.CREATED, "PARTY_201", "초대 받은 파티에 대한 응답을 완료했습니다."),


    // 뽀모도로
    POMODORO_COMPLETED(HttpStatus.CREATED, "POMODORO_201", "뽀모도로가 성공적으로 완료되었습니다."),

    // token
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "TOKEN_200", "토큰 재발급 성공"),

    // user
    MAIN_PAGE_VIEW_SUCCESS(HttpStatus.OK, "USER_200", "메인 페이지 조회를 완료했습니다.");

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
