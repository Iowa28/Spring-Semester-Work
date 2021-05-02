package ru.kpfu.aminovniaz.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.aminovniaz.project.dto.GameForm;
import ru.kpfu.aminovniaz.project.model.Game;
import ru.kpfu.aminovniaz.project.service.GameService;

import javax.validation.Valid;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private GameService gameService;

    @GetMapping
    public String getAdminPage() {

        return "admin/adminMain";
    }

    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public String getGamesPage(ModelMap map) {
        map.addAttribute("gameForm", new GameForm());
        map.addAttribute("genres", gameService.getAllGameGenre());
        return "admin/games";
    }

    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public String addGame(@Valid GameForm gameForm,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          ModelMap map) {
        if (!result.hasErrors()) {
            gameService.addGame(gameForm);
            redirectAttributes.addFlashAttribute("message", "Game '" + gameForm.getName() + "' has been added.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            System.out.println("Game validation error...");
            return "admin/games";
        }


        return "redirect:/admin";
    }

}
