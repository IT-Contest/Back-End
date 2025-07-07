package ssuchaehwa.it_project.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssuchaehwa.it_project.global.error.code.status.BaseCode;
import ssuchaehwa.it_project.global.error.code.status.BaseErrorCode;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "statusCode", "message", "result"})
public class BaseResponse<T> {

    @JsonProperty("isSuccess")
    private boolean isSuccess;

    private String statusCode;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> BaseResponse<T> onSuccess(BaseCode code, T result) {
        return new BaseResponse<>(
                true,
                code.getCode(),
                code.getMessage(),
                result);
    }

    public static <T> BaseResponse<T> onFailure(BaseErrorCode code, T result) {
        return new BaseResponse<>(
                false,
                code.getCode(),
                code.getMessage(),
                result);
    }
}
