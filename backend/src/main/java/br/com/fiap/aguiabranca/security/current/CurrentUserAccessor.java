package br.com.fiap.aguiabranca.security.current;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.com.fiap.aguiabranca.common.exception.ApiException;

@Component
public class CurrentUserAccessor {

    public CurrentUser require() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser user)) {
            throw ApiException.unauthenticated();
        }
        return user;
    }
}
