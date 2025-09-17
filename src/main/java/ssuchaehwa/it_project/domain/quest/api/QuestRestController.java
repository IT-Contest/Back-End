package ssuchaehwa.it_project.domain.quest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ssuchaehwa.it_project.domain.quest.application.QuestAnalysisService;
import ssuchaehwa.it_project.domain.quest.application.QuestService;
import ssuchaehwa.it_project.domain.quest.converter.QuestConverter;
import ssuchaehwa.it_project.domain.quest.dto.AnalysisResponseDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestRequestDTO;
import ssuchaehwa.it_project.domain.quest.dto.QuestResponseDTO;
import ssuchaehwa.it_project.global.common.response.BaseResponse;
import ssuchaehwa.it_project.global.config.security.auth.UserPrincipal;
import ssuchaehwa.it_project.global.error.code.status.SuccessStatus;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/quests")
public class QuestRestController {

    private final QuestService questService;
    private final QuestAnalysisService questAnalysisService;

    // 퀘스트 생성 API
    @PostMapping(value = "")
    @Operation(summary = "퀘스트를 추가하는 API", description = "request body에 questCreateRequest 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUEST_201", description = "CREATED, 퀘스트가 성공적으로 생성되었습니다.")
    })
    public BaseResponse<QuestResponseDTO.QuestCreateResponse> createQuest(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid QuestRequestDTO.QuestCreateRequest questCreateRequest
            ) {
        QuestResponseDTO.QuestCreateResponse result = questService.createQuest(questCreateRequest, principal.getId());

        return BaseResponse.onSuccess(SuccessStatus.QUEST_CREATED, result);
    }

    // 퀘스트 수정 API
    @PutMapping(value = "/{questId}")
    @Operation(summary = "퀘스트를 수정하는 API", description = "questId와 questUpdateRequest를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUEST_202", description = "OK, 퀘스트가 성공적으로 수정되었습니다.")
    })
    public BaseResponse<QuestResponseDTO.QuestUpdateResponse> updateQuest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long questId,
            @RequestBody @Valid QuestRequestDTO.QuestUpdateRequest questUpdateRequest
    ) {
        QuestResponseDTO.QuestUpdateResponse result = questService.updateQuest(questId, questUpdateRequest, principal.getId());

        return BaseResponse.onSuccess(SuccessStatus.QUEST_UPDATED, result);
    }

    // 퀘스트 삭제 API
    @DeleteMapping(value = "/{questId}")
    @Operation(summary = "퀘스트를 삭제하는 API", description = "questId를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUEST_203", description = "OK, 퀘스트가 성공적으로 삭제되었습니다.")
    })
    public BaseResponse<QuestResponseDTO.QuestDeleteResponse> deleteQuest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long questId
    ) {
        QuestResponseDTO.QuestDeleteResponse result = questService.deleteQuest(questId, principal.getId());

        return BaseResponse.onSuccess(SuccessStatus.QUEST_DELETED, result);
    }

    // 친구 초대 링크 발급
    @PostMapping("/invite")
    @Operation(summary = "친구 초대 링크 발급 API", description = "친구 초대용 링크를 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "INVITE_FRIEND_201",
                    description = "CREATED, 친구 초대를 완료했습니다."
            )
    })
    public BaseResponse<QuestResponseDTO.FriendInviteResponse> createFriendInvite(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        QuestResponseDTO.FriendInviteResponse result =
                questService.friendInvite(principal.getId());

        return BaseResponse.onSuccess(SuccessStatus.INVITE_FRIEND_CREATED, result);
    }

    // 친구 초대 수락
    @PostMapping("/invite/accept")
    @Operation(summary = "친구 초대 수락 API", description = "토큰을 받아 로그인한 사용자와 친구를 맺습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "FRIEND_201",
                    description = "CREATED, 친구 추가를 완료했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "INVITE_4001",
                    description = "잘못되었거나 만료된 초대입니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "INVITE_4002",
                    description = "이미 처리된 초대입니다."
            )
    })
    public BaseResponse<String> acceptFriendInvite(
            @RequestParam("token") String token,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        questService.acceptFriendInvite(token, principal.getId());
        return BaseResponse.onSuccess(SuccessStatus.FRIEND_ADDED, "친구 추가 완료");
    }

    // 친구 초대 거절
    @PostMapping("/invite/reject")
    @Operation(summary = "친구 초대 거절 API", description = "토큰을 받아 로그인한 사용자가 초대를 거절합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "INVITE_201",
                    description = "CREATED, 친구 초대를 거절했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "INVITE_4001",
                    description = "잘못되었거나 만료된 초대입니다."
            )
    })
    public BaseResponse<String> rejectFriendInvite(
            @RequestParam("token") String token,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        questService.rejectFriendInvite(token, principal.getId());
        return BaseResponse.onSuccess(SuccessStatus.INVITE_FRIEND_CREATED, "친구 초대 거절 완료");
    }

    // 친구 조회 API
    @GetMapping("/friend-list")
    @Operation(summary = "친구 리스트 불러오는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FRIEND_200", description = "OK, 친구 조회를 완료했습니다.")
    })
    public BaseResponse<List<QuestResponseDTO.FriendListResponse>> getFriendList(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<QuestResponseDTO.FriendListResponse> result = questService.getFriends(principal.getId());

        return BaseResponse.onSuccess(SuccessStatus.FRIEND_VIEW_SUCCESS, result);
    }

    // 퀘스트 조회 API (로그인한 사용자 기준)
    @GetMapping("/quest-list")
    @Operation(summary = "퀘스트 리스트 불러오는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUEST_200", description = "OK, 퀘스트 조회를 완료했습니다.")
    })
    public BaseResponse<List<QuestResponseDTO.QuestListResponse>> getQuestList(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<QuestResponseDTO.QuestListResponse> result = questService.getQuests(principal.getId());
        return BaseResponse.onSuccess(SuccessStatus.QUEST_VIEW_SUCCESS, result);
    }

    // 메인 화면 조회 API
    @GetMapping("/mainpage")
    @Operation(summary = "메인 화면을 불러오는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_200", description = "OK, 메인 페이지 조회를 완료했습니다.")
    })
    public BaseResponse<QuestResponseDTO.MainPageResponse> getMainPage(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        QuestResponseDTO.MainPageResponse result = questService.getMainPage(principal.getId());

        return BaseResponse.onSuccess(SuccessStatus.MAIN_PAGE_VIEW_SUCCESS, result);
    }

    // 퀘스트 완료 상태 변경 API
    @PatchMapping("/change")
    @Operation(summary = "퀘스트 완료 상태를 변경하는 API", description = "유저의 퀘스트 정보를 request body를 통해 리스트로 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUEST_201", description = "CREATED, 퀘스트의 상태 변경 완료했습니다.")
    })
    public BaseResponse<List<QuestResponseDTO.QuestStatusChangeResponse>> updateQuestStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid QuestRequestDTO.QuestStatusChangeRequest questStatusChangeRequest
    ) {
        List<QuestResponseDTO.QuestStatusChangeResponse> result = questService.changeQuestStatus(questStatusChangeRequest, principal.getId());

        return BaseResponse.onSuccess(SuccessStatus.QUEST_STATUS_CHANGE, result);
    }

    // 파티 생성 API
    @PostMapping(value = "/party/create")
    @Operation(summary = "파티를 추가하는 API", description = "request body에 partyCreateRequest 형식의 데이터와, path로 questId를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PARTY_201", description = "CREATED, 파티가 성공적으로 생성되었습니다.")
    })
    public BaseResponse<QuestResponseDTO.PartyCreateResponse> createParty(
            @RequestBody @Valid QuestRequestDTO.PartyCreateRequest partyCreateRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        QuestResponseDTO.PartyCreateResponse result = questService.createParty(principal.getId(), partyCreateRequest);

        return BaseResponse.onSuccess(SuccessStatus.PARTY_CREATED, result);
    }

    /*
    *
    {
      "content": "test의 test",
      "questTitle": "test",
      "priority": 1,
      "questType": "DAILY",
      "completionStatus": "INCOMPLETE",
      "startDate": "2025-09-18",
      "dueDate": "2025-09-18",
      "startTime": "09:00:00",
      "endTime": "10:00:00",
      "hashtags": [test]
    }
    * */

    // 파티 초대
    @PostMapping("/party/{party-id}/invite")
    @Operation(summary = "파티에 친구 초대 API", description = "파티에 여러 명의 친구를 초대하고, 초대한 사람과 초대된 친구 목록을 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "INVITE_FRIEND_201: 친구 초대가 완료되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PARTY_404: 파티가 존재하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "USER_4001: 존재하지 않는 유저입니다.")
    })
    public BaseResponse<QuestResponseDTO.PartyInviteResponse> inviteFriends(
            @PathVariable("party-id") Long partyId,
            @RequestBody QuestRequestDTO.PartyInviteRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        QuestResponseDTO.PartyInviteResponse result = questService.inviteFriends(principal.getId(), partyId, request.getFriendIds());
        return BaseResponse.onSuccess(SuccessStatus.INVITE_FRIEND_CREATED, result);
    }

    // 파티 조회 (내가 만든 파티 + 내가 속한 파티)
    @GetMapping("/party/list")
    @Operation(summary = "내 파티 리스트 조회 API", description = "내가 만든 파티와 내가 속한 파티들을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "PARTY_200",
                    description = "OK, 파티 리스트를 성공적으로 조회했습니다."
            ),
    })
    public BaseResponse<List<QuestResponseDTO.PartyListResponse>> getMyParties(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<QuestResponseDTO.PartyListResponse> result = questService.getMyParties(principal.getId());
        return BaseResponse.onSuccess(SuccessStatus.PARTY_LIST_VIEW_SUCCESS, result);
    }


    // 파티 수정
    @PatchMapping("/party/{party-id}")
    @Operation(summary = "파티 수정 API", description = "Path로 partyId를 넘기고, body에는 수정할 데이터를 넣어주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PARTY_202: 파티가 성공적으로 수정되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PARTY_404: 해당 파티가 존재하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PARTY_403: 파티에 접근 권한이 없습니다.")
    })
    public BaseResponse<QuestResponseDTO.PartyUpdateResponse> updateParty(
            @PathVariable("party-id") Long partyId,
            @RequestBody @Valid QuestRequestDTO.PartyUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return BaseResponse.onSuccess(
                SuccessStatus.PARTY_UPDATED,
                questService.updateParty(principal.getId(), partyId, request)
        );
    }

    // 파티 삭제
    @DeleteMapping("/party/{party-id}")
    @Operation(summary = "파티 삭제 API", description = "Path로 partyId를 넘겨주면 해당 파티를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PARTY_203: 파티가 성공적으로 삭제되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "PARTY_404: 해당 파티가 존재하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "PARTY_403: 파티에 접근 권한이 없습니다.")
    })
    public BaseResponse<QuestResponseDTO.PartyDeleteResponse> deleteParty(
            @PathVariable("party-id") Long partyId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return BaseResponse.onSuccess(
                SuccessStatus.PARTY_DELETED,
                questService.deleteParty(principal.getId(), partyId)
        );
    }

    // 파티 초대 리스트 조회
    @GetMapping("/party/party-list")
    @Operation(summary = "초대 받은 파티 리스트를 조회하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PARTY_200", description = "OK, 초대 받은 파티 리스트를 조회 완료했습니다.")
    })
    public BaseResponse<List<QuestResponseDTO.PartyInvitationListResponse>> getPartyList(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<QuestResponseDTO.PartyInvitationListResponse> result = questService.getInvitedPartyList(principal.getId());

        return BaseResponse.onSuccess(SuccessStatus.INVITE_PARTY_LIST_VIEW_SUCCESS, result);
    }

    // 파티 수락 / 거절
    @PostMapping("/party/party-response")
    @Operation(summary = "초대 받은 파티에 대한 수락 혹은 거절 상태 변경 API", description = "초대가 온 파티 정보를 request body를 통해 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PARTY_201", description = "CREATED, 초대 받은 파티에 대한 응답을 완료했습니다.")
    })
    public BaseResponse<QuestResponseDTO.PartyInvitationResponse> updatePartyResponse(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid QuestRequestDTO.PartyInvitationResponseRequest request
    ) {
        QuestResponseDTO.PartyInvitationResponse result = questService.respondToInvitation(principal.getId(), request);

        return BaseResponse.onSuccess(SuccessStatus.INVITE_PARTY_STATUS_CHANGE, result);
    }
}
