package ru.kpfu.aminovniaz.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.kpfu.aminovniaz.project.aop.ExceptionLog;
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
import java.util.ArrayList;
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

    public List<Game> getAllGames() {
        //return filterByDeleted(gameRepo.findAll());
        return gameRepo.findAllByDeletedFalseOrderByIdDesc();
    }

    public List<Game> searchByCost(int cost) {
        return gameRepo.searchByCostLess(cost);
    }

    public Page<Game> getPageGames(int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.Direction.ASC, "id");
        return gamePagingRepository.findAllByPagination(pageable);
    }

    public Page<Game> getPageGamesPage(int count, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, count, Sort.Direction.ASC, "id");
        return gamePagingRepository.findAllByPagination(pageable);
    }

    public List<Game> getLastGames(int count) {
        //Pageable pageable = PageRequest.of(1, count, Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(0, existGamesCount() - count, Sort.Direction.DESC, "id");
        List<Game> games = gamePagingRepository.findAllByPaginationNotOrdered(pageable).getContent();
        return games;
    }

    public String getLastGamesResponseBody(int count) {
        List<Game> games = getLastGames(count);
        return prepareAjaxResponse(games);
    }

    public int existGamesCount() {
        return gameRepo.findAllByDeletedFalse().size();
    }

    public List<Game> filterGames(List<Game> games) {
        List<Game> filteredGames = new ArrayList<>();
        for (Game game : games) {
            if (!game.isDeleted()) {
                filteredGames.add(game);
            }
        }
        return filteredGames;
    }

    public List<GameGenre> getAllGameGenre() { return gameGenreRepo.findAll(); }

    private List<Game> filterByDeleted(List<Game> allGames) {
        List<Game> games = new ArrayList<>();
        for (Game game : allGames) {
            if (!game.isDeleted()) {
                games.add(game);
            }
        }
        return games;
    }

    @ExceptionLog
    public Game getGameByName(String name) {
        Game game = gameRepo.findByName(name).orElseThrow(() -> new NotFoundException("Не удалось найти данную игру"));
        if (game.isDeleted()) {
            throw new NotFoundException("Данная игра была удалена");
        }
        return game;
    }

    @ExceptionLog
    public GameGenre getGameGenreById(Long id) {
        return gameGenreRepo.findById(id).orElseThrow(() -> new NotFoundException("Не удалось найти данный жанр игры"));
    }

    @ExceptionLog
    public Game getGameById(Long id) {
        Game game = gameRepo.findById(id).orElseThrow(() -> new NotFoundException("Не удалось найти данную игру"));
        if (game.isDeleted()) {
            throw new NotFoundException("Данная игра была удалена");
        }
        return game;
    }


    public void addGame(GameForm gameForm) {
        Game game = createGame(gameForm);
        GameInfo gameInfo = createGameInfo(gameForm);

        game.setGameGenre(createGameGenre(gameForm));
        game.setGameInfo(gameInfo);

        gameInfoRepo.save(gameInfo);
        gameRepo.save(game);
    }

    public void updateGame(GameForm gameForm, Long gameId) {
        Game game = getGameById(gameId);
        game.setName(gameForm.getName());
        game.setCover(gameForm.getCover());
        game.setCost(gameForm.getCost());
        game.setAnnotation(gameForm.getAnnotation());
        Long genreId = Long.parseLong(gameForm.getGenre());
        game.setGameGenre(getGameGenreById(genreId));

        GameInfo gameInfo = game.getGameInfo();
        gameInfo.setDeveloper(gameForm.getDeveloper());
        gameInfo.setPublisher(gameForm.getPublisher());
        gameInfo.setReleaseDate(gameForm.getReleaseDate());

        gameInfoRepo.save(gameInfo);
        gameRepo.save(game);
    }

    public void deleteGame(long gameId) {
        Game game = getGameById(gameId);
        System.out.println("Delete game: " + game.getName());
        game.setDeleted(true);
        gameRepo.save(game);
    }

    private Game createGame(GameForm gameForm) {
        Game game = Game.builder()
                .name(gameForm.getName())
                .annotation(gameForm.getAnnotation())
                .cover(gameForm.getCover())
                .cost(gameForm.getCost())
                .deleted(false)
                .build();
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
        return filterByDeleted(gameRepo.searchByNameStartWith(name));
    }

    @ExceptionLog
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

        return filterByDeleted(games);
    }

    public int getTotalCost(List<Game> games) {
        int totalCost = 0;

        for (Game game : games) {
            totalCost += game.getCost();
        }

        return totalCost;
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
