package com.cleanyco.weblab4back.controller;

import com.cleanyco.weblab4back.model.Point;
import com.cleanyco.weblab4back.model.User;
import com.cleanyco.weblab4back.repository.PointRepository;
import com.cleanyco.weblab4back.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@RestController
@Data
public class PointController {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    private static final Logger log = Logger.getLogger(UserController.class.getName());

    static {
        try { //fixme заменить путь на относительный
            FileHandler fileHandler = new FileHandler("C:\\ramm\\WebLab4\\web-lab-4-BACK\\src\\main\\java\\com\\cleanyco\\weblab4back\\log\\point.log");
            log.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            log.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String checkHit(double x, double y, double r) {
        if (checkCircle(x, y, r) || checkRectangle(x, y, r) || checkTriangle(x, y, r)) {
            return "Hit";
        } else {
            return "Miss";
        }
    }


    public boolean checkTriangle(double x, double y, double r) {
        return (x > 0) && (y > 0) && (y < -x + r);
    }

    public boolean checkCircle(double x, double y, double r) {
        return (x < 0) && (y < 0) && (Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(r, 2));
    }

    public boolean checkRectangle(double x, double y, double r) {
        return (x < 0) && (y > 0) && (x > -r/2) && (y < r);
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/getpoints")
    Collection<Point> points(HttpSession session) {
        User user = userRepository.findBySessionID(session.getId());
        if (user != null) {
            return pointRepository.findAllByOwner(user);
        } else {
            return null;
        }
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/addpoint")
    ResponseEntity<?> addPoint(HttpSession session, @RequestBody Point point) {
        log.info("Была получена точка: " + point.getX() +"; " + point.getY() +"; " + point.getR());
        User user = userRepository.findBySessionID(session.getId());
        if (user != null) {
            long  startTime = System.currentTimeMillis();
            point.setHit(checkHit(point.getX(), point.getY(), point.getR()));
            point.setExecTime(String.valueOf(System.currentTimeMillis() - startTime));
            point.setCurTime(LocalDateTime.now().toString());
            point.setOwner(user);
            pointRepository.save(point);
                return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.info("пользователь оказался с сессией null!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
