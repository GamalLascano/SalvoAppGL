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

    /**
     * This function is accessible via the /api/games route, and will contain the current user (if present) and the info from all the games
     *
     * @param authentication The currently logged in user
     * @return The user and a list of games
     */
    @RequestMapping("/games")
    public Map<String, Object> getGameID(Authentication authentication) {
        Map<String, Object> aux = new LinkedHashMap<>();
        //If the user is not authenticated, send guest, otherwise send the user info
        aux.put("player", retPlayer(authentication) == null ? "Guest" : retPlayer(authentication));
        aux.put("games", gameRepository.findAll().stream().map(game -> game.toDTO()).collect(Collectors.toList()));
        return aux;
    }

    /**
     * This function will create a new game when posted into (together with a new game player).
     *
     * @param authentication The currently logged in user
     * @return The id of the new game player id if correctly created, otherwise, an error message
     */
    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createNewGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        //If the user is not a guest
        if (!Utils.isGuest(authentication)) {
            //We create a new game a new gameplayer with the logged in user
            Player p = playerRepository.findByUserName(authentication.getName());
            LocalDateTime ldt = LocalDateTime.now();
            Game g = new Game(ldt);
            GamePlayer gp = new GamePlayer(ldt, g, p);
            gameRepository.save(g);
            gamePlayerRepository.save(gp);
            response = new ResponseEntity<>(Utils.makeMap("gpid", gp.getId()), HttpStatus.OK);
        } else {
            //If the user is not logged in, send error
            response = new ResponseEntity<>(Utils.makeMap("error", "User is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    /**
     * This method will join the currently logged in user into the requested game
     *
     * @param gameId         The id of the game that the user wants to join
     * @param authentication The currently logged in user
     * @return A new game player id if correct, otherwise an error will occur
     */
    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        //If the user is not a guest
        if (!Utils.isGuest(authentication)) {
            //We find a game with id passed by parameter
            Optional<Game> game = gameRepository.findById(gameId);
            //If game exists, proceed
            if (game.isPresent()) {
                //If there are less than two players, create the game player
                if ((long) game.get().getPlayers().size() < 2) {
                    GamePlayer gp = new GamePlayer(LocalDateTime.now(), game.get(), playerRepository.findByUserName(authentication.getName()));
                    gamePlayerRepository.save(gp);
                    response = new ResponseEntity<>(Utils.makeMap("gpid", gp.getId()), HttpStatus.OK);
                } else {
                    //Else, the game is full
                    response = new ResponseEntity<>(Utils.makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
                }
            } else {
                //The game doesnt exist
                response = new ResponseEntity<>(Utils.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
            }
        } else {
            //Happens when the user is not logged in
            response = new ResponseEntity<>(Utils.makeMap("error", "User is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    private Map<String, Object> retPlayer(Authentication authentication) {
        //If the user is in the player repository and is logged in, return info, else return null
        if (!Utils.isGuest(authentication)) {
            return playerRepository.findByUserName(authentication.getName()).toPlayerDTO();
        } else {
            return null;
        }
    }
}
