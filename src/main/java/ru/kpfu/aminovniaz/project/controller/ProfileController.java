package ru.kpfu.aminovniaz.project.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.kpfu.aminovniaz.project.model.UserDetailsImpl;

@Controller
public class ProfileController {

    @PreAuthorize("isAuthenticated()")
    //@PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getProfilePage(@AuthenticationPrincipal UserDetailsImpl user, ModelMap map) {
        System.out.println(user.getUsername());
        map.addAttribute("user", user);

        return "profile";
    }
}
