package br.com.fiap.aguiabranca.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.fiap.aguiabranca.common.exception.ApiException;
import br.com.fiap.aguiabranca.common.web.ApiErrorWriter;
import br.com.fiap.aguiabranca.common.web.RequestContext;
import br.com.fiap.aguiabranca.security.jwt.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ApiErrorWriter errorWriter;
    private final boolean openApiPublic;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            ApiErrorWriter errorWriter,
            @Value("${app.openapi.public:false}") boolean openApiPublic
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.errorWriter = errorWriter;
        this.openApiPublic = openApiPublic;
    }

    @Bean
    FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    auth.requestMatchers(
                            "/api/v1/auth/login",
                            "/api/v1/auth/register",
                            "/api/v1/auth/refresh",
                            "/api/v1/auth/reset-password"
                    ).permitAll();
                    auth.requestMatchers("/actuator/health", "/actuator/health/**").permitAll();
                    if (openApiPublic) {
                        auth.requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll();
                    }
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> writeAuthError(request, response);
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                errorWriter.write(request, response, ApiException.forbidden());
    }

    private void writeAuthError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = (String) request.getAttribute(RequestContext.AUTH_ERROR_CODE);
        String message = (String) request.getAttribute(RequestContext.AUTH_ERROR_MESSAGE);
        if (code == null) {
            errorWriter.write(request, response, ApiException.unauthenticated());
            return;
        }
        errorWriter.write(request, response, HttpStatus.UNAUTHORIZED, code, message, List.of());
    }
}
