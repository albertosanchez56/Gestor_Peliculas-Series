package com.user.service.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.user.service.Entidades.User;
import com.user.service.servicio.UserService;

@RestController
@RequestMapping("/usuario/admin")
public class UserAdminController {

	private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    // ✅ LISTAR TODOS
    @GetMapping("/users")
    public ResponseEntity<List<User>> listar() {
        List<User> usuarios = userService.getAll();
        if (usuarios.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(usuarios);
    }

    // ✅ OBTENER POR ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> obtener(@PathVariable int id) {
        User usuario = userService.obtenerUsurario(id);
        if (usuario == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(usuario);
    }
}