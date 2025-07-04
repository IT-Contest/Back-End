package ssuchaehwa.it_project.global.config.security.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ssuchaehwa.it_project.domain.user.entity.User;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String socialId;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.socialId = user.getSocialId();
    }

    public Long getId() {
        return id;
    }

    public String getSocialId() {
        return socialId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 사용 안하므로 빈 리스트 반환
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인이라 패스워드 없음
    }

    @Override
    public String getUsername() {
        return socialId; // socialId를 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}