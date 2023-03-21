package com.cleanyco.weblab4back.repository;


import com.cleanyco.weblab4back.model.Point;
import com.cleanyco.weblab4back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PointRepository extends JpaRepository<Point, Long> {
    Collection<Point> findAllByOwner(User user);
}
