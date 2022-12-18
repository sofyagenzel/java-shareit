package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", schema = "public")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", nullable = false)
    @NotEmpty
    private String name;
    @NotEmpty
    @Email(message = "не корректный e-mail")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public User(User newUser) {
        this.setId(newUser.getId());
        this.setName(newUser.getName());
        this.setEmail(newUser.getEmail());
    }
}