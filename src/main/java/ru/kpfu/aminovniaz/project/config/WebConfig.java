package ru.kpfu.aminovniaz.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.kpfu.aminovniaz.project.model.Game;
import ru.kpfu.aminovniaz.project.model.GameGenre;
import ru.kpfu.aminovniaz.project.model.GameInfo;
import ru.kpfu.aminovniaz.project.model.User;
import ru.kpfu.aminovniaz.project.utils.EntityConverter;

@Configuration
@ComponentScan("ru.kpfu.aminovniaz.project.controller")
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
    }

    @Bean
    public EntityConverter userGenericConverter() {
        return new EntityConverter(User.class);
    }

    @Bean
    public EntityConverter gameGenericConverter() {
        return new EntityConverter(Game.class);
    }

    @Bean
    public EntityConverter gameGenreGenericConverter() {
        return new EntityConverter(GameGenre.class);
    }

    @Bean
    public EntityConverter gameInfoGenericConverter() {
        return new EntityConverter(GameInfo.class);
    }
}
