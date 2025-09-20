package ssuchaehwa.it_project.domain.user.application;

import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.user.dto.UserRequestDTO;
import ssuchaehwa.it_project.domain.user.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    // 약관 조회
    List<UserResponseDTO.TermResponse> getAllTerms();

    // 사용자 약관 동의 저장
    void agreeTerms(Long userId, UserRequestDTO.UserTermRequest request);

    // 사용자 약관 동의 여부 확인
    boolean hasAgreedAllRequired(Long userId);

    // 약관 생성
    UserResponseDTO.TermResponse createTerm(UserRequestDTO.TermCreateRequest request);
    
    // 온보딩 완료
    UserResponseDTO.OnboardingCompleteResponse completeOnboarding(Long userId);
}
