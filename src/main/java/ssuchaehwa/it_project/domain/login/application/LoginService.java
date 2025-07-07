package ssuchaehwa.it_project.domain.login.application;

import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto;
import ssuchaehwa.it_project.domain.login.dto.AuthResponseDto.LoginResult;

public interface LoginService {
    LoginResult kakaoLogin(String code);
    AuthResponseDto.LoginResult kakaoLoginWithAccessToken(String kakaoAccessToken);

}