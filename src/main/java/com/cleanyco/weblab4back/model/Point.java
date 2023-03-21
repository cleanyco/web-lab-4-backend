package com.cleanyco.weblab4back.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue
    private long id;
    double x, y, r;
    String execTime;
    String curTime;
    String hit;
    @ManyToOne
    private User owner;
}
