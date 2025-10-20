package ssuchaehwa.it_project.domain.login.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ssuchaehwa.it_project.domain.login.application.LoginService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginService loginService;

    @Test
    @DisplayName("애플 로그인 Mock 테스트 - 실패 케이스 (아직 구현 안됨)")
    void mockAppleLogin_ShouldFail_WhenEndpointNotExists() throws Exception {
        // Given: Mock 요청 데이터
        String requestBody = """
            {
                "sub": "mock-apple-user-12345",
                "email": "mockuser@icloud.com",
                "emailVerified": true,
                "inviterCode": null
            }
            """;

        // When & Then: /auth/login/apple/mock 엔드포인트가 없어서 404 에러가 나야 함
        mockMvc.perform(post("/auth/login/apple/mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound()); // 404 에러 기대
    }

    @Test
    @DisplayName("애플 로그인 Mock 테스트 - 성공 케이스 (구현 후)")
    void mockAppleLogin_ShouldSuccess_WhenImplemented() throws Exception {
        // 일단 주석 처리 - 구현 후 테스트
        
        // Given: Mock 요청 데이터
        String requestBody = """
            {
                "sub": "mock-apple-user-12345",
                "email": "mockuser@icloud.com",
                "emailVerified": true,
                "inviterCode": null
            }
            """;

        // When & Then: 성공 응답이 와야 함 (아직 실패할 것)
        mockMvc.perform(post("/auth/login/apple/mock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
}