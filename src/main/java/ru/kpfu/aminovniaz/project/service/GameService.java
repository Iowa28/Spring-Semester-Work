package ru.kpfu.aminovniaz.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.kpfu.aminovniaz.project.dto.GameForm;
import ru.kpfu.aminovniaz.project.exception.NotFoundException;
import ru.kpfu.aminovniaz.project.model.Game;
import ru.kpfu.aminovniaz.project.model.GameGenre;
import ru.kpfu.aminovniaz.project.model.GameInfo;
import ru.kpfu.aminovniaz.project.repository.GameGenreRepository;
import ru.kpfu.aminovniaz.project.repository.GameInfoRepository;
import ru.kpfu.aminovniaz.project.repository.GamePagingRepository;
import ru.kpfu.aminovniaz.project.repository.GameRepository;

import javax.persistence.criteria.Predicate;
import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GamePagingRepository gamePagingRepository;
    @Autowired
    private GameGenreRepository gameGenreRepo;
    @Autowired
    private GameInfoRepository gameInfoRepo;

    //TODO: get rid of the hardcode
    private static int gameCount = 16;
    private static int gameSize = 12;

    public List<Game> getAllGames() { return gameRepo.findAll(); }

    public Page<Game> getPageGames() {
        Pageable pageable = PageRequest.of(0, gameSize, Sort.Direction.ASC, "id");

        return gamePagingRepository.findAll(pageable);
    }

    public List<Game> getLastGames() {
        initGameCount();
        Pageable pageable = PageRequest.of(0, gameCount - gameSize, Sort.Direction.DESC, "id");

        return gamePagingRepository.findAll(pageable).getContent();
    }

    public String getLastGamesResponseBody() {
        List<Game> games = getLastGames();
        String response = prepareAjaxResponse(games);
        return response;
    }

    private void initGameCount() {
        List<Game> games = getAllGames();
        gameCount = games.size();
    }

    public List<GameGenre> getAllGameGenre() { return gameGenreRepo.findAll(); }

    public List<GameInfo> getAllGameInfos() { return gameInfoRepo.findAll(); }

    public Game getGameByName(String name) {
        return gameRepo.findByName(name).orElseThrow(() -> new NotFoundException("Не удалось найти данную игру"));
    }

    public GameGenre getGameGenreById(Long id) {
        return gameGenreRepo.findById(id).orElseThrow(() -> new NotFoundException("Не удалось найти данный жанр игры"));
    }

    public Game getGameById(Long id) {
        return gameRepo.findById(id).orElseThrow(() -> new NotFoundException("Не удалось найти данную игру"));
    }


    public void addGame(GameForm gameForm) {
        Game game = createGame(gameForm);
        GameInfo gameInfo = createGameInfo(gameForm);

        game.setGameGenre(createGameGenre(gameForm));
        game.setGameInfo(gameInfo);

        gameInfoRepo.save(gameInfo);
        gameRepo.save(game);
    }

    private Game createGame(GameForm gameForm) {
        Game game = Game.builder()
                .name(gameForm.getName())
                .annotation(gameForm.getAnnotation())
                .cover(gameForm.getCover())
                .cost(gameForm.getCost()).build();
        return game;
    }

    private GameGenre createGameGenre(GameForm gameForm) {
        String genreName = gameForm.getGenre();
        GameGenre gameGenre = getGameGenreById(Long.parseLong(genreName));
        return gameGenre;
    }

    private GameInfo createGameInfo(GameForm gameForm) {
        GameInfo gameInfo = GameInfo.builder()
                .developer(gameForm.getDeveloper())
                .publisher(gameForm.getPublisher())
                .releaseDate(gameForm.getReleaseDate()).build();
        return  gameInfo;
    }

    public List<Game> getFilteredGames(String name) {
        return gameRepo.searchByNameStartWith(name);
    }

    public List<Game> getGamesByGenre(String name) {
        GameGenre gameGenre = gameGenreRepo.findByName(name).orElseThrow(() ->
                new NotFoundException("Не удалось найти данный жанр игры"));

        List<Game> games = gameRepo.findAll((Specification<Game>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate p = criteriaBuilder.conjunction();
            if (!StringUtils.isEmpty(name)) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("gameGenre"), gameGenre.getId()));
            }
            return p;
        });

        return games;
    }


    private String prepareAjaxResponse(List<Game> games) {
        String response = "";

        for (Game game : games) {
            response += "<div class=\"col-md-3 col-xs-3\" style=\"margin-bottom: 30px\">";
            response += "<div class=\"card item\">";
            response += "<img src=\"" + game.getCover() + "\" class=\"card-img-top\" height=\"350\" width=\"200\" alt=\"\">";
            response += "<div class=\"card-body\">";
            response += "<h5 class=\"card-title\" style=\"text-align: center\">";
            response += "<a href=\"/home/game/" + game.getId() + "\" class=\"item-link\">" + game.getName() +"</a>";
            response += "</h5>";
            response += "<p class=\"card-text item-cost\">" + game.getCost() + "</p>";
            response += "</div>";
            response += "</div>";
            response += "</div>";
        }

        return response;
    }
}
