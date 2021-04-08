package com.codeoftheweb.salvo.game;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import com.codeoftheweb.salvo.player.Player;
import com.codeoftheweb.salvo.player.PlayerRepository;
import com.codeoftheweb.salvo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    public GameRepository gameRepository;
    @Autowired
    public PlayerRepository playerRepository;
    @Autowired
    public GamePlayerRepository gamePlayerRepository;
    @RequestMapping("/games")
    public Map<String, Object> getGameID(Authentication authentication) {
        Map<String, Object> aux = new LinkedHashMap<>();
        aux.put("player", retPlayer(authentication) == null ? "Guest" : retPlayer(authentication));
        aux.put("games", gameRepository.findAll().stream().map(game -> game.toDTO()).collect(Collectors.toList()));
        return aux;
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createNewGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (!Utils.isGuest(authentication)) {
            Player p = playerRepository.findByUserName(authentication.getName());
            LocalDateTime ldt = LocalDateTime.now();
            Game g = new Game(ldt);
            GamePlayer gp = new GamePlayer(ldt, g, p);
            gameRepository.save(g);
            gamePlayerRepository.save(gp);
            response = new ResponseEntity<>(Utils.makeMap("gpid", gp.getId()), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(Utils.makeMap("error", "User is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }
    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (!Utils.isGuest(authentication)) {
            Optional<Game> game = gameRepository.findById(gameId);
            if (game.isPresent()) {
                if (game.get().getPlayers().stream().count() < 2) {
                    GamePlayer gp = new GamePlayer(LocalDateTime.now(), game.get(), playerRepository.findByUserName(authentication.getName()));
                    gamePlayerRepository.save(gp);
                    response = new ResponseEntity<>(Utils.makeMap("gpid", gp.getId()), HttpStatus.OK);
                } else {
                    response = new ResponseEntity<>(Utils.makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
                }
            } else {
                response = new ResponseEntity<>(Utils.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
            }
        } else {
            response = new ResponseEntity<>(Utils.makeMap("error", "User is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }
    private Map<String, Object> retPlayer(Authentication authentication) {
        if (!Utils.isGuest(authentication)) {
            return playerRepository.findByUserName(authentication.getName()).toPlayerDTO();
        } else {
            return null;
        }
    }
}
