package ssuchaehwa.it_project.domain.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssuchaehwa.it_project.domain.user.domain.entity.Term;
import ssuchaehwa.it_project.domain.user.domain.entity.User;
import ssuchaehwa.it_project.domain.user.domain.entity.UserTerm;
import ssuchaehwa.it_project.domain.user.domain.repository.TermRepository;
import ssuchaehwa.it_project.domain.user.domain.repository.UserRepository;
import ssuchaehwa.it_project.domain.user.domain.repository.UserTermRepository;
import ssuchaehwa.it_project.domain.user.dto.UserRequestDTO;
import ssuchaehwa.it_project.domain.user.dto.UserResponseDTO;
import ssuchaehwa.it_project.domain.user.exception.UserException;
import ssuchaehwa.it_project.global.error.code.status.ErrorStatus;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TermRepository termRepository;
    private final UserTermRepository userTermRepository;

    // 약관 조회
    @Override
    public List<UserResponseDTO.TermResponse> getAllTerms() {
        return termRepository.findAll().stream()
                .map(term -> new UserResponseDTO.TermResponse(
                        term.getId(),
                        term.getTitle(),
                        term.getUrl(),
                        term.isRequired()))
                .toList();
    }

    // 약관 동의 저장
    @Transactional
    @Override
    public void agreeTerms(Long userId, UserRequestDTO.UserTermRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        // 필수 약관 체크
        List<Term> allTerms = termRepository.findAll();
        List<Term> requiredTerms = allTerms.stream()
                .filter(Term::isRequired)
                .toList();

        if (!request.getTermIds().containsAll(
                requiredTerms.stream().map(Term::getId).toList())) {
            throw new UserException(ErrorStatus.TERMS_REQUIRED_NOT_AGREED);
        }

        // 이미 동의한 약관 중복 저장 방지
        for (Long termId : request.getTermIds()) {
            Term term = allTerms.stream()
                    .filter(t -> t.getId().equals(termId))
                    .findFirst()
                    .orElseThrow(() -> new UserException(ErrorStatus.TERM_NOT_FOUND));

            if (!userTermRepository.existsByUserAndTerm(user, term)) {
                UserTerm userTerm = UserTerm.builder()
                        .user(user)
                        .term(term)
                        .build();
                userTermRepository.save(userTerm);
            }
        }
    }

    // 약관 동의 여부 체크
    @Override
    public boolean hasAgreedAllRequired(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorStatus.NO_SUCH_USER));

        List<Long> requiredTermIds = termRepository.findAll().stream()
                .filter(Term::isRequired)
                .map(Term::getId)
                .toList();

        if (requiredTermIds.isEmpty()) {
            return true; // 필수 약관 자체가 없으면 true
        }

        // 유저가 동의한 약관 ID
        List<Long> agreedTermIds = userTermRepository.findByUser(user).stream()
                .map(userTerm -> userTerm.getTerm().getId())
                .toList();

        // 모든 필수 약관이 포함됐는지 체크
        return agreedTermIds.containsAll(requiredTermIds);
    }

    // 약관 생성
    @Transactional
    @Override
    public UserResponseDTO.TermResponse createTerm(UserRequestDTO.TermCreateRequest request) {
        Term term = Term.builder()
                .title(request.getTitle())
                .url(request.getUrl())
                .isRequired(request.isRequired())
                .build();

        Term saved = termRepository.save(term);

        return new UserResponseDTO.TermResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getUrl(),
                saved.isRequired()
        );
    }

}
