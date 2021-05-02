package ru.kpfu.aminovniaz.project.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
public class UserForm {
    @NotNull
    @NotEmpty(message = "Это поле не может быть пустым.")
    private String username;
    @NotNull
    @NotEmpty(message = "Это поле не может быть пустым.")
    @Email(message = "Некорректный email адрес.")
    private String email;
    @NotNull
    @NotEmpty(message = "Это поле не может быть пустым.")
    private String password;
}
