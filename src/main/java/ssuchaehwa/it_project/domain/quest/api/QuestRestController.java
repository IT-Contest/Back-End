package ssuchaehwa.it_project.domain.quest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssuchaehwa.it_project.domain.quest.application.QuestService;
import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/quests")
public class QuestRestController {

    private final QuestService questService;

    // 퀘스트 생성 API
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

    // 파티 생성 API
    @PostMapping(value = "/{quest-id}/party")
    @Operation(summary = "파티를 추가하는 API", description = "request body에 partyCreateRequest 형식의 데이터와, path로 questId를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PARTY_201", description = "CREATED, 파티가 성공적으로 생성되었습니다.")
    })
    public BaseResponse<QuestResponseDTO.PartyCreateResponse> createParty(
            @PathVariable("quest-id") Long questId,
            @RequestBody @Valid QuestRequestDTO.PartyCreateRequest partyCreateRequest
    ) {
        QuestResponseDTO.PartyCreateResponse result = questService.createParty(partyCreateRequest, questId);

        return BaseResponse.onSuccess(SuccessStatus.PARTY_CREATED, result);
    }

    // 친구 초대 API
    @PostMapping(value = "/{quest-id}/invite")
    @Operation(summary = "친구를 초대하는 API", description = "request body에 friendInviteRequest 형식의 데이터와, path로 questId를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "INVITE_FRIEND_201", description = "CREATED, 친구 초대를 완료했습니다.")
    })
    public BaseResponse<QuestResponseDTO.FriendInviteResponse> createFriendInvite(
            @PathVariable("quest-id") Long questId,
            @RequestBody @Valid QuestRequestDTO.FriendInviteRequest friendInviteRequest
    ) {
        QuestResponseDTO.FriendInviteResponse result = questService.friendInvite(friendInviteRequest, questId);

        return BaseResponse.onSuccess(SuccessStatus.INVITE_FRIEND_CREATED, result);
    }

    // 친구 조회 API
    @GetMapping("/{quest-id}")
    @Operation(summary = "친구 리스트 불러오는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FRIEND_200", description = "OK, 친구 조회를 완료했습니다.")
    })
    public BaseResponse<List<QuestResponseDTO.FriendListResponse>> getFriendList(
            @PathVariable("quest-id") Long questId
    ) {
        List<QuestResponseDTO.FriendListResponse> result = questService.getFriends(questId);

        return BaseResponse.onSuccess(SuccessStatus.FRIEND_VIEW_SUCCESS, result);
    }

    // 퀘스트 조회 API
    @GetMapping("/quest-list")
    @Operation(summary = "퀘스트 리스트 불러오는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUEST_200", description = "OK, 퀘스트 조회를 완료했습니다.")
    })
    public BaseResponse<List<QuestResponseDTO.QuestListResponse>> getQuestList() {
        List<QuestResponseDTO.QuestListResponse> result = questService.getQuests();

        return BaseResponse.onSuccess(SuccessStatus.QUEST_VIEW_SUCCESS, result);
    }
}
