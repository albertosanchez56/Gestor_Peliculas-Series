package com.user.service.controlador;

import com.user.service.dto.UserPublicDTO;
import com.user.service.servicio.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario/internal/users")
public class UserInternalController {

    private final UserService userService;

    public UserInternalController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPublicDTO> getPublicById(@PathVariable Long id) {
        var u = userService.findByIdOrThrow(id);
        return ResponseEntity.ok(new UserPublicDTO(u.getId(), u.getUsername(), u.getDisplayName()));
    }
}
