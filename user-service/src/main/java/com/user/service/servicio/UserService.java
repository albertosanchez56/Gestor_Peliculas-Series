package com.user.service.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.user.service.Entidades.Role;
import com.user.service.Entidades.Status;
import com.user.service.Entidades.User;
import com.user.service.repositorio.UserRepository;

@Service
public class UserService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private UserRepository userRepository;
	
	public List<User> getAll(){
		return userRepository.findAll();
	}
	
	public User obtenerUsurario(long id) {
		return userRepository.findById(id).orElse(null);
	}
	
	public User save(User usuario) {
		User nuevoUsuario = userRepository.save(usuario);
		return nuevoUsuario;
	}
	
	public User findByIdOrThrow(Long id) {
	    return userRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("User not found: " + id));
	}

	public User updateRole(Long id, Role role) {
	    User u = findByIdOrThrow(id);
	    u.setRole(role);
	    return userRepository.save(u);
	}

	public User updateStatus(Long id, Status status) {
	    User u = findByIdOrThrow(id);
	    u.setStatus(status);
	    return userRepository.save(u);
	}

}
