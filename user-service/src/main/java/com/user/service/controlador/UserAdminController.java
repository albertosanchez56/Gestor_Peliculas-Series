package com.user.service.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.user.service.Entidades.User;
import com.user.service.dto.UpdateUserRoleRequest;
import com.user.service.dto.UpdateUserStatusRequest;
import com.user.service.dto.UserAdminDTO;
import com.user.service.servicio.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario/admin/users")
public class UserAdminController {

    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    // ✅ LISTAR
    @GetMapping
    public ResponseEntity<List<UserAdminDTO>> listar() {
        List<User> usuarios = userService.getAll();
        if (usuarios.isEmpty()) return ResponseEntity.noContent().build();

        List<UserAdminDTO> dto = usuarios.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dto);
    }

    // ✅ GET por id
    @GetMapping("/{id}")
    public ResponseEntity<UserAdminDTO> get(@PathVariable Long id) {
        User u = userService.findByIdOrThrow(id);
        return ResponseEntity.ok(toDto(u));
    }

    // ✅ CAMBIAR STATUS (ACTIVE/BANNED)
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserAdminDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest req,
            Authentication auth
    ) {
        String actorUsername = auth.getName(); // ✅ username
        User updated = userService.updateStatus(actorUsername, id, req.status());
        return ResponseEntity.ok(toDto(updated));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserAdminDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest req,
            Authentication auth
    ) {
        String actorUsername = auth.getName(); // ✅ username
        User updated = userService.updateRole(actorUsername, id, req.role());
        return ResponseEntity.ok(toDto(updated));
    }



    private UserAdminDTO toDto(User u) {
        return new UserAdminDTO(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                u.getDisplayName(),
                u.getRole(),
                u.getStatus(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}