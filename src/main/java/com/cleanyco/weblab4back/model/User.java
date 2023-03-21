package com.cleanyco.weblab4back.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Collection;
import java.util.List;



@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    private String username;
    private String password;
    private String sessionID;
}
