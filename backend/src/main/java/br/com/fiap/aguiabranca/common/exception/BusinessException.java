package br.com.fiap.aguiabranca.common.exception;

public class BusinessException extends ApiException {

    public BusinessException(String message) {
        super(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE_VIOLATION", message);
    }
}
