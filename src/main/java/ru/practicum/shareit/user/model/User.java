package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    @NotEmpty
    private String name;
    @NotEmpty
    @Email(message = "не корректный e-mail")
    private String email;

    public User(User newUser) {
        this.setId(newUser.getId());
        this.setName(newUser.getName());
        this.setEmail(newUser.getEmail());
    }
}