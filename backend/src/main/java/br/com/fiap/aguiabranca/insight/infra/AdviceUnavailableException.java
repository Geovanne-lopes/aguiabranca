package br.com.fiap.aguiabranca.insight.infra;

public class AdviceUnavailableException extends RuntimeException {

    private final boolean retryable;

    public AdviceUnavailableException(boolean retryable) {
        this(retryable, null);
    }

    public AdviceUnavailableException(boolean retryable, Throwable cause) {
        super(retryable ? "AdviceSlip temporariamente indisponível." : "AdviceSlip indisponível.", cause);
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
