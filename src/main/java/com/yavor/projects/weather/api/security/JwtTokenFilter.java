package com.yavor.projects.weather.api.security;

import com.yavor.projects.weather.api.service.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends GenericFilterBean {
    private final Logger Logger = LoggerFactory.getLogger(getClass());

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final AuthorizationService authorizationService;

    public JwtTokenFilter(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        String token = resolveToken((HttpServletRequest) req);
        if (!token.isEmpty()) {
            try {
                var user = authorizationService.verifyAndGetUser(token);
                var authorization = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority(user.getRole()))
                );
                SecurityContextHolder.getContext().setAuthentication(authorization);
            } catch (Exception e) {
                Logger.error(String.format("Exception: %s %s", e.getMessage(), e.getLocalizedMessage()));
            }
        }
        filterChain.doFilter(req, res);
    }

    private String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTH_HEADER);
        if (bearerToken == null || bearerToken.isEmpty() || !bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
            return "";
        }
        return bearerToken.substring(BEARER_TOKEN_PREFIX.length(), bearerToken.length()).trim();
    }
}