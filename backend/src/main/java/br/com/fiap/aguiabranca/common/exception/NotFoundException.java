package br.com.fiap.aguiabranca.common.exception;

public class NotFoundException extends ApiException {

    public NotFoundException() {
        super(org.springframework.http.HttpStatus.NOT_FOUND, "NOT_FOUND", "Recurso não encontrado.");
    }
}
