package br.com.fiap.aguiabranca.user.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.aguiabranca.common.api.ApiPage;
import br.com.fiap.aguiabranca.security.current.CurrentUserAccessor;
import br.com.fiap.aguiabranca.user.api.dto.UpdateProfileRequest;
import br.com.fiap.aguiabranca.user.api.dto.UserResponse;
import br.com.fiap.aguiabranca.user.application.UserService;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final UserService userService;
    private final CurrentUserAccessor currentUserAccessor;

    public UserController(UserService userService, CurrentUserAccessor currentUserAccessor) {
        this.userService = userService;
        this.currentUserAccessor = currentUserAccessor;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'LEADER')")
    @Operation(summary = "Listar usuários (MANAGER e LEADER)")
    public ApiPage<UserResponse> list(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction
    ) {
        return userService.list(role, q, page, size, sort, direction);
    }

    @PatchMapping("/me")
    @Operation(summary = "Atualizar perfil do usuário autenticado")
    public UserResponse updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateMe(currentUserAccessor.require(), request);
    }
}
