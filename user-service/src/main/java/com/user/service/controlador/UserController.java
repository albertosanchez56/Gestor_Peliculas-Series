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

	@PreAuthorize("isAuthenticated()")
	  @GetMapping("/ping")
	  public ResponseEntity<String> ping() {
	    return ResponseEntity.ok("OK");
	  }
}
