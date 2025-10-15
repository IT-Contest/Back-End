package ssuchaehwa.it_project.domain.login.domain;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto;

import java.net.URL;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleOAuthClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.oauth.apple.team-id}")
    private String teamId;

    @Value("${spring.oauth.apple.key-id}")
    private String keyId;

    @Value("${spring.oauth.apple.client-id}")
    private String clientId;

    @Value("${spring.oauth.apple.issuer}")
    private String issuer;

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

    /**
     * Apple Identity Token을 검증하고 사용자 정보를 추출하는 메서드
     * @param identityToken Apple에서 발급받은 Identity Token
     * @return Apple 사용자 정보 응답 DTO
     */
    public AuthResponseDto.AppleUserInfo verifyIdentityToken(String identityToken) {
        try {
            log.info("🍎 Apple Identity Token 검증 시작");

            // 1. JWT 파싱
            SignedJWT signedJWT = SignedJWT.parse(identityToken);
            
            // 2. JWT 헤더에서 kid(Key ID) 추출
            String kid = signedJWT.getHeader().getKeyID();
            
            // 3. Apple 공개 키 가져오기
            RSAKey publicKey = getApplePublicKey(kid);
            
            // 4. JWT 서명 검증
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Apple Identity Token 서명 검증 실패");
            }
            
            // 5. JWT Claims 검증
            Map<String, Object> claims = signedJWT.getJWTClaimsSet().getClaims();
            validateClaims(claims);
            
            // 6. 사용자 정보 추출
            String sub = (String) claims.get("sub"); // Apple 고유 사용자 ID
            String email = (String) claims.get("email");
            Boolean emailVerified = (Boolean) claims.get("email_verified");
            
            log.info("🍎 Apple 사용자 정보 추출 완료 - sub: {}, email: {}", sub, email);
            
            return AuthResponseDto.AppleUserInfo.builder()
                    .sub(sub)
                    .email(email)
                    .emailVerified(emailVerified != null ? emailVerified : false)
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ Apple Identity Token 검증 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Apple Identity Token 검증 실패", e);
        }
    }

    /**
     * Apple 공개 키를 가져오는 메서드
     */
    private RSAKey getApplePublicKey(String kid) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(APPLE_PUBLIC_KEYS_URL, String.class);
            JWKSet jwkSet = JWKSet.parse(response.getBody());
            
            JWK jwk = jwkSet.getKeyByKeyId(kid);
            if (jwk == null) {
                throw new RuntimeException("Apple 공개 키를 찾을 수 없습니다. kid: " + kid);
            }
            
            return (RSAKey) jwk;
        } catch (Exception e) {
            log.error("❌ Apple 공개 키 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("Apple 공개 키 조회 실패", e);
        }
    }

    /**
     * JWT Claims 검증
     */
    private void validateClaims(Map<String, Object> claims) {
        // iss (issuer) 검증
        String iss = (String) claims.get("iss");
        if (!issuer.equals(iss)) {
            throw new RuntimeException("Invalid issuer: " + iss);
        }

        // aud (audience) 검증
        String aud = (String) claims.get("aud");
        if (!clientId.equals(aud)) {
            throw new RuntimeException("Invalid audience: " + aud);
        }

        // exp (expiration time) 검증
        Long exp = (Long) claims.get("exp");
        if (exp == null || new Date(exp * 1000).before(new Date())) {
            throw new RuntimeException("Token expired");
        }

        log.info("🍎 Apple JWT Claims 검증 완료");
    }
}