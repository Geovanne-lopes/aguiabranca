package br.com.fiap.aguiabranca.ai.infra;

public interface GeminiClient {

    GeminiCompletion complete(String dashboardJson);
}
