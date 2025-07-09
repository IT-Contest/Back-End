package ssuchaehwa.it_project.domain.login.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {
    private final RestTemplate restTemplate = new RestTemplate();

    // application.yml에서 주입받는 설정 값들 (카카오 API 정보)
    @Value("${spring.oauth.kakao.token-uri}")
    private String tokenUri;

    @Value("${spring.oauth.kakao.user-info-uri}")
    private String userInfoUri;

    @Value("${spring.oauth.kakao.client-id}")
    private String clientId;

    @Value("${spring.oauth.kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 인가 코드를 사용해 카카오로부터 AccessToken 및 RefreshToken 발급받는 메서드
     * @param code 인가 코드 (프론트엔드에서 전달받은 값)
     * @return 카카오 토큰 응답 DTO
     */
    public AuthResponseDto.KakaoToken requestToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", clientId);
            body.add("redirect_uri", redirectUri);
            body.add("code", code);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<AuthResponseDto.KakaoToken> response =
                    restTemplate.postForEntity(tokenUri, request, AuthResponseDto.KakaoToken.class);

            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException("카카오 토큰 응답이 null입니다."));
        } catch (Exception e) {
            log.error("❌ 카카오 토큰 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 토큰 요청 실패", e);
        }
    }

    /**
     * 발급받은 AccessToken 이용해 카카오 사용자 정보를 요청하는 메서드
     * @param accessToken 카카오 인증 토큰
     * @return 카카오 사용자 정보 응답 DTO
     */
    public AuthResponseDto.KakaoUserInfo requestUserInfo(String accessToken) {
        try {
            log.info("🟡 requestUserInfo() - accessToken: {}", accessToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<AuthResponseDto.KakaoUserInfo> response =
                    restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, AuthResponseDto.KakaoUserInfo.class);

            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RuntimeException("카카오 사용자 정보 응답이 null입니다."));
        } catch (Exception e) {
            log.error("❌ 카카오 사용자 정보 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("카카오 사용자 정보 요청 실패", e);
        }
    }
}