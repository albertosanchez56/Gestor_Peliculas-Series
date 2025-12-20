package com.user.service.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
}
