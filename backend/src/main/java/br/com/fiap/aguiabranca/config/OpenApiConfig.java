package br.com.fiap.aguiabranca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI aguiaBrancaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Águia Branca API")
                        .version("v1")
                        .description("API de gestão de inovação — Sprint 2. Autenticação JWT (access 1h + refresh 7d). Inclui auth, usuários, diretrizes, ideias, projetos, sugestões, notificações, insight diário (AdviceSlip), dashboards calculados no servidor e insights Gemini só para o líder (fallback 200 se a IA falhar). A API key do Gemini fica em GEMINI_API_KEY e não sai na resposta."))
                .components(new Components().addSecuritySchemes(
                        "bearer-jwt",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }
}
