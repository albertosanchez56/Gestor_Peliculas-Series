package com.user.service.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.user.service.Entidades.Role;
import com.user.service.Entidades.Status;
import com.user.service.Entidades.User;
import com.user.service.repositorio.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User obtenerUsurario(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User save(User usuario) {
        return userRepository.save(usuario);
    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    // ✅ Protegido: actorId no puede cambiarse a sí mismo
    public User updateRole(Long actorId, Long targetId, Role role) {
        if (actorId != null && actorId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes cambiar tu propio rol.");
        }

        User u = findByIdOrThrow(targetId);
        u.setRole(role);
        return userRepository.save(u);
    }

    // ✅ Protegido: actorId no puede banearse a sí mismo
    public User updateStatus(Long actorId, Long targetId, Status status) {
        if (actorId != null && actorId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes cambiar tu propio estado.");
        }

        User u = findByIdOrThrow(targetId);
        u.setStatus(status);
        return userRepository.save(u);
    }
}
