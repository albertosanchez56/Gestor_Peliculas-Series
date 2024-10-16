package com.user.service.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

	@Autowired
	private UserService userService;
	
	@GetMapping
	public ResponseEntity<List<User>> listaUsuarios(){
		List<User> usuarios = userService.getAll();
		if(usuarios.isEmpty()) {
			ResponseEntity.noContent().build();
		}		
		return ResponseEntity.ok(usuarios);
	}
	
	@GetMapping("{/id}")
	public ResponseEntity<User> obtenerUsuario(@PathVariable("id") int id){
		 User usuario = userService.obtenerUsurario(id);
		 if(usuario == null) {
				ResponseEntity.noContent().build();
			}		
		return ResponseEntity.ok(usuario);
	}
	
	@PostMapping
	public ResponseEntity<User> guardarUsuario(@RequestBody User usuario){
		User nuevoUsuario = userService.save(usuario);
		return ResponseEntity.ok(nuevoUsuario);
	}
}
