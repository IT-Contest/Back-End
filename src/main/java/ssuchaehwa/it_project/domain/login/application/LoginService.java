package ssuchaehwa.it_project.domain.login.application;

import org.springframework.lang.Nullable;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto.LoginResult;

public interface LoginService {
    LoginResult kakaoLogin(String code, @Nullable String inviterCode);
    AuthResponseDto.LoginResult kakaoLoginWithAccessToken(String kakaoAccessToken, @Nullable String inviterCode);
    AuthResponseDto.LoginResult refreshToken(Long userId, String refreshToken);
    AuthResponseDto.LoginResult guestLogin(String deviceId);
    AuthResponseDto.AutoLoginResult autoLogin(String accessToken);
    void logout(String accessToken);
    void withdraw(String accessToken);
}