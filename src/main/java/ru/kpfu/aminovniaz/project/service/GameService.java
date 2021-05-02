package ru.kpfu.aminovniaz.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.kpfu.aminovniaz.project.dto.GameForm;
import ru.kpfu.aminovniaz.project.model.Game;
import ru.kpfu.aminovniaz.project.model.GameGenre;
import ru.kpfu.aminovniaz.project.model.GameInfo;
import ru.kpfu.aminovniaz.project.repository.GameGenreRepository;
import ru.kpfu.aminovniaz.project.repository.GameInfoRepository;
import ru.kpfu.aminovniaz.project.repository.GameRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GameGenreRepository gameGenreRepo;
    @Autowired
    private GameInfoRepository gameInfoRepo;

    public List<Game> getAllGames() { return gameRepo.findAll(); }

    public List<GameGenre> getAllGameGenre() { return gameGenreRepo.findAll(); }

    public List<GameInfo> getAllGameInfos() { return gameInfoRepo.findAll(); }

    public Game getGameByName(String name) throws Exception {
        return gameRepo.findByName(name).orElseThrow(() -> new Exception("Didn't find any game"));
    }

    public GameGenre getGameGenreById(Long id) {
        return gameGenreRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("ERROR"));
    }

    public Game getGameById(Long id) throws Exception {
        return gameRepo.findById(id).orElseThrow(() -> new Exception("Didn't find any game"));
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
                new IllegalArgumentException("No game genre here"));

//        List<Game> games = gameGenreRepo.findAll((Specification<Game>) (root, criteriaQuery, criteriaBuilder) -> {
//
//        }).;

        return null;
    }
}
