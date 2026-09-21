package br.com.fiap.aguiabranca.security.current;

import br.com.fiap.aguiabranca.user.domain.UserRole;

public record CurrentUser(
        String id,
        String email,
        UserRole role
) {
}
