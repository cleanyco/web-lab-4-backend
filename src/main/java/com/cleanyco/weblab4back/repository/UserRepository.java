package com.cleanyco.weblab4back.repository;

import com.cleanyco.weblab4back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findUserByUsername(String username);
    User findBySessionID(String sessionID);
}
