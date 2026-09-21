package br.com.fiap.aguiabranca.common.web;

public final class RequestContext {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String TRACE_ID_MDC = "traceId";
    public static final String AUTH_ERROR_CODE = "auth.error.code";
    public static final String AUTH_ERROR_MESSAGE = "auth.error.message";

    private RequestContext() {
    }
}
