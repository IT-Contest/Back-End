package ssuchaehwa.it_project.domain.model.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssuchaehwa.it_project.domain.model.exception.TestException;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class ModelController {

    @GetMapping("/execute")
    @Operation(summary = "테스트 API", description = "예 : path variable을 통해 id를 주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON_200", description = "OK, 테스트를 성공했습니다.")
    })
    public BaseResponse<Void> test(@RequestParam String error) {

        if (error.equals("yes")) {
            throw new TestException(ErrorStatus._BAD_REQUEST);
        }

        return BaseResponse.onSuccess(SuccessStatus.OK, null);
    }
}
