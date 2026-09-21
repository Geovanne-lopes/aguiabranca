package br.com.fiap.aguiabranca.ai.infra;

public record GeminiCompletion(
        Kind kind,
        String text,
        String model,
        Integer httpStatus
) {

    public enum Kind {
        SUCCESS,
        EMPTY,
        TIMEOUT,
        MODEL_NOT_FOUND,
        FAILED
    }

    public static GeminiCompletion success(String text, String model) {
        return new GeminiCompletion(Kind.SUCCESS, text, model, 200);
    }

    public static GeminiCompletion empty(String model) {
        return new GeminiCompletion(Kind.EMPTY, "", model, 200);
    }

    public static GeminiCompletion timeout(String model) {
        return new GeminiCompletion(Kind.TIMEOUT, "", model, null);
    }

    public static GeminiCompletion modelNotFound(String model) {
        return new GeminiCompletion(Kind.MODEL_NOT_FOUND, "", model, 404);
    }

    public static GeminiCompletion failed(String model, Integer httpStatus) {
        return new GeminiCompletion(Kind.FAILED, "", model, httpStatus);
    }
}
