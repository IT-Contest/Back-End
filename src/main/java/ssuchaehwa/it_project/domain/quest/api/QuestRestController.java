package ssuchaehwa.it_project.domain.quest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssuchaehwa.it_project.domain.quest.application.QuestService;
import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/quests")
public class QuestRestController {

    private final QuestService questService;

    @PostMapping(value = "")
    @Operation(summary = "퀘스트를 추가하는 API", description = "request body에 questCreateRequest 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUEST_201", description = "CREATED, 퀘스트가 성공적으로 생성되었습니다.")
    })
    public BaseResponse<QuestResponseDTO.QuestCreateResponse> createQuest(
            @RequestBody @Valid QuestRequestDTO.QuestCreateRequest questCreateRequest
            ) {
        QuestResponseDTO.QuestCreateResponse result = questService.createQuest(questCreateRequest);

        return BaseResponse.onSuccess(SuccessStatus.QUEST_CREATED, result);
    }
}
