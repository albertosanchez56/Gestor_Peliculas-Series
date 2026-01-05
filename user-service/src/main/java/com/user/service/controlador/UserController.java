package com.user.service.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.service.Entidades.User;
import com.user.service.servicio.UserService;

@RestController
@RequestMapping("/usuario")
public class UserController {

  @Autowired private UserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<User>> listaUsuarios() {
    List<User> usuarios = userService.getAll();
    if (usuarios.isEmpty()) return ResponseEntity.noContent().build();
    return ResponseEntity.ok(usuarios);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<User> obtenerUsuario(@PathVariable int id) {
    User usuario = userService.obtenerUsurario(id);
    if (usuario == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(usuario);
  }

  // ⚠️ Yo este POST lo quitaría o lo haría ADMIN-only y MUY controlado.
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<User> guardarUsuario(@RequestBody User usuario){
    User nuevoUsuario = userService.save(usuario);
    return ResponseEntity.ok(nuevoUsuario);
  }
}
