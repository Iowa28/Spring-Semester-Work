package ru.kpfu.aminovniaz.project.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kpfu.aminovniaz.project.model.Game;
import ru.kpfu.aminovniaz.project.service.GameService;

import javax.annotation.security.PermitAll;
import java.util.List;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @PermitAll
    @RequestMapping(value = "/home",method = RequestMethod.GET)
    public String homePage(ModelMap map) {
        map.addAttribute("header", "Главная");
        map.addAttribute("games", gameService.getAllGames());
        map.addAttribute("categories", gameService.getAllGameGenre());
        return "homePage";
    }

    @PermitAll
    @RequestMapping(value = "/home/filtered", method = RequestMethod.POST)
    public String homePageFiltered(@RequestParam("name") String name, ModelMap map) {
        List<Game> games = null;
        if (!StringUtils.isEmpty(name) && !name.equals(" ")) {
            games = gameService.getFilteredGames(name.toUpperCase());

            if (games.isEmpty()) {
                System.out.println("EMPTY");
            }
            map.addAttribute("header", "По запросу " + name + " найдено: " + games.size());
        }
        else {
            return "redirect:/home";
        }

        map.addAttribute("games", games);
        return "homePage";
    }

    @PermitAll
    @RequestMapping(value = "/home/genre/{name}", method = RequestMethod.GET)
    public String getGamesByGenre(@PathVariable String name, ModelMap map) {
        List<Game> games = null;

        map.addAttribute("games", games);
        map.addAttribute("header", "Игр с жанром " + name + ":" + games.size());
        return "homePage";
    }

    //TODO: Exception Handling
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/home/game/{id}", method = RequestMethod.GET)
    public String getGame(@PathVariable String id, ModelMap map) {
        try {
            Game someGame = gameService.getGameById(Long.parseLong(id));
            if (someGame == null) {
                throw new Exception("No game here");
            }
            map.addAttribute("game", someGame);
        } catch (Exception e) {
            System.out.println("NO GAME HERE");
        }

        return "game";
    }
}