package com.cleanyco.weblab4back.controller;

import com.cleanyco.weblab4back.model.User;
import com.cleanyco.weblab4back.repository.UserRepository;
import com.cleanyco.weblab4back.util.MD5PasswordEncoder;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private static final Logger log = Logger.getLogger(UserController.class.getName()); //имя логгера

    static {
        try { //fixme заменить путь на относительный (как этого добиться?)
            FileHandler fileHandler = new FileHandler("C:\\ramm\\WebLab4\\web-lab-4-BACK\\src\\main\\java\\com\\cleanyco\\weblab4back\\log\\info.log");
            log.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            log.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserController(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpSession session, @RequestBody User user) {
        log.info("Пользователь регистрируется, уточняем, нет ли пользователя с таким именем...");
        User existingUser = userRepository.findUserByUsername(user.getUsername());
        if (existingUser == null) {
            user.setSessionID(session.getId());
            user.setPassword(MD5PasswordEncoder.hash(user.getPassword()));
            userRepository.save(user);
            log.info("Регистрация пользователя прошла успешно. Имя: " + user.getUsername() + "; пароль: " + user.getPassword());
            log.info("Пользователю " + user.getUsername() + " соответствует сессия: " + user.getSessionID());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.info("Ошибка регистрации: пользователь с именем " + user.getUsername() + " уже существует.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpSession session, @RequestBody User user) {
        log.info("Производится попытка входа в аккаунт...");
        User existingUser = userRepository.findUserByUsername(user.getUsername());
        if (existingUser != null && existingUser.getSessionID() != null && !existingUser.getSessionID().equals(session.getId())) {
            log.info("Попытка входа заблокирована! Пользователь уже авторизован в другом браузере");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (existingUser != null && MD5PasswordEncoder.hash(user.getPassword()).equals(existingUser.getPassword())) {
            if (existingUser.getSessionID() == null) {
                user.setSessionID(session.getId());
                userRepository.save(user);
                log.info("Пользователь успешно авторизован. Имя: " + user.getUsername() + "; сессия: " + user.getSessionID());
                return new ResponseEntity(HttpStatus.OK);
            } else {
                log.info("Пользователь успешно авторизован. Имя: " + user.getUsername() + "; сессия: " + user.getSessionID());
                return new ResponseEntity(HttpStatus.OK);
            }
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
       log.info("Поступил запрос на выход из сессии: " + session.getId());
       log.info("Проверяем, существует ли такая сессия...");
       User user = userRepository.findBySessionID(session.getId());
       if (user != null) {
           user.setSessionID(null);
           userRepository.save(user);
           log.info("Сессия: " + session.getId() + " пользователя " + user.getUsername() + " завершена!");
           return new ResponseEntity(HttpStatus.OK);
       } else {
           log.info("Произошла ошибка при попытке завершения сессии: сессия не существует");
           return new ResponseEntity(HttpStatus.BAD_REQUEST);
       }
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/checksession")
    public ResponseEntity<?> checkSession(HttpSession session) {
        User user = userRepository.findBySessionID(session.getId());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}