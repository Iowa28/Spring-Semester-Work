package ru.kpfu.aminovniaz.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kpfu.aminovniaz.project.config.WebConfig;

@SpringBootApplication
@Import(WebConfig.class)
public class Application {

    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
