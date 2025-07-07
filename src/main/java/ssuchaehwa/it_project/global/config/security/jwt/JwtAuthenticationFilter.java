package ssuchaehwa.it_project.global.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ssuchaehwa.it_project.global.config.security.auth.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("🛡️ JwtAuthenticationFilter 진입 - URI: {}", request.getRequestURI());

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/dev/login/kakao")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        log.debug("🔐 Authorization 헤더: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("📦 추출된 토큰: {}", token);

            if (jwtUtil.validateToken(token)) {
                log.debug("✅ 토큰 유효함");
                String userId = jwtUtil.getUserIdFromToken(token);
                log.debug("👤 토큰에서 추출한 userId: {}", userId);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
                log.debug("🔍 userDetails 로딩 완료 - 사용자명: {}", userDetails.getUsername());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("✅ SecurityContextHolder에 인증정보 설정 완료");
            } else {
                log.warn("❌ 토큰 검증 실패");
            }
        } else {
            //log.warn("🚫 Authorization 헤더 없음 또는 Bearer 형식 아님");
        }

        filterChain.doFilter(request, response);
    }
}