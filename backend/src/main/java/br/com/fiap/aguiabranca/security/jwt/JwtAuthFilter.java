package br.com.fiap.aguiabranca.security.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.fiap.aguiabranca.common.exception.ApiException;
import br.com.fiap.aguiabranca.common.web.ApiErrorWriter;
import br.com.fiap.aguiabranca.common.web.RequestContext;
import br.com.fiap.aguiabranca.security.current.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApiErrorWriter errorWriter;

    public JwtAuthFilter(JwtService jwtService, ApiErrorWriter errorWriter) {
        this.jwtService = jwtService;
        this.errorWriter = errorWriter;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!header.startsWith("Bearer ")) {
            request.setAttribute(RequestContext.AUTH_ERROR_CODE, "INVALID_TOKEN");
            request.setAttribute(RequestContext.AUTH_ERROR_MESSAGE, "Token inválido.");
            errorWriter.write(request, response, ApiException.invalidToken());
            return;
        }
        String token = header.substring(7).trim();
        if (token.isEmpty()) {
            errorWriter.write(request, response, ApiException.invalidToken());
            return;
        }
        try {
            CurrentUser user = jwtService.parseAccessToken(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name()))
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (ApiException ex) {
            request.setAttribute(RequestContext.AUTH_ERROR_CODE, ex.getCode());
            request.setAttribute(RequestContext.AUTH_ERROR_MESSAGE, ex.getMessage());
            errorWriter.write(request, response, ex);
        }
    }
}
