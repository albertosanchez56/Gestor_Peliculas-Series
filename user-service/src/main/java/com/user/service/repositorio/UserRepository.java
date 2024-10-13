package com.user.service.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.service.Entidades.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

}
