package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.game.Game;
import com.codeoftheweb.salvo.game.GameRepository;
import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import com.codeoftheweb.salvo.player.Player;
import com.codeoftheweb.salvo.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    public GameRepository gameRepository;
    @Autowired
    public GamePlayerRepository gamePlayerRepository;
    @Autowired
    public PlayerRepository playerRepository;
    @RequestMapping("/games")
    public Map<String, Object>getGameID(Authentication authentication){
        Map<String, Object> aux = new LinkedHashMap<>();
        aux.put("player",retPlayer(authentication)==null ? "Guest":retPlayer(authentication));
        aux.put("games",gameRepository.findAll().stream().map(game -> game.toDTO()).collect(Collectors.toList()));
        return aux;
    }
    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createNewGame(Authentication authentication){
        ResponseEntity<Map<String, Object>> response;
        if(!isGuest(authentication)){
                Player p = playerRepository.findByUserName(authentication.getName());
                LocalDateTime ldt = LocalDateTime.now();
                Game g = new Game(ldt);
                GamePlayer gp = new GamePlayer(ldt,g,p);
                gameRepository.save(g);
                gamePlayerRepository.save(gp);
                response = new ResponseEntity<>(makeMap("gpid",gp.getId()),HttpStatus.OK);
        }else{
            response = new ResponseEntity<>(makeMap("error","User is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }
    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId,Authentication authentication){
        ResponseEntity<Map<String, Object>> response;
        if(!isGuest(authentication)){
            Optional<Game> game = gameRepository.findById(gameId);
            if(game.isPresent()){
                if(game.get().getPlayers().stream().count()<2){
                    GamePlayer gp = new GamePlayer(LocalDateTime.now(),game.get(),playerRepository.findByUserName(authentication.getName()));
                    gamePlayerRepository.save(gp);
                    response = new ResponseEntity<>(makeMap("gpid",gp.getId()),HttpStatus.OK);
                }else{
                    response = new ResponseEntity<>(makeMap("error","Game is full"), HttpStatus.FORBIDDEN);
                }
            }else{
                response = new ResponseEntity<>(makeMap("error","No such game"), HttpStatus.FORBIDDEN);
            }
        }else{
            response = new ResponseEntity<>(makeMap("error","User is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }
    private Map<String, Object> makeMap(String s, Object o){
        Map<String, Object> aux = new LinkedHashMap<>();
        aux.put(s,o);
        return aux;
    }
    private Map<String,Object> retPlayer(Authentication authentication){
        if(!isGuest(authentication)){
            return playerRepository.findByUserName(authentication.getName()).toPlayerDTO();
        }else{
            return null;
        }
    }
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findOwner(@PathVariable Long gamePlayerId,Authentication authentication) {
        Optional<GamePlayer> gp=gamePlayerRepository.findById(gamePlayerId);
        ResponseEntity<Map<String, Object>> response;
        if(gp.isPresent()){
            if (authentication.getName().compareTo(gp.get().getPlayerID().getUserName())==0){
                response=new ResponseEntity<>(getMap(gamePlayerId), HttpStatus.OK);
            }else{
                response =new ResponseEntity<>(makeMap("problem","Player is not authorized"),HttpStatus.UNAUTHORIZED);
            }
        }else{
            Map<String, Object> aux = new LinkedHashMap<String, Object>();
            aux.put("problem","gamePlayer doesn´t exist");
            response =new ResponseEntity<>(aux,HttpStatus.UNAUTHORIZED);
        }
        return response;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String username, @RequestParam String password) {

        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(username) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    private Map<String, Object> getMap(Long gamePlayerId){
        Map<String, Object> datazo = gamePlayerRepository.getOne(gamePlayerId).getGameID().toDTO();
        datazo.put("ships",gamePlayerRepository.getOne(gamePlayerId).getShips().stream().map(a -> a.toShipDTO()).collect(Collectors.toList()));
        datazo.put("salvoes",gamePlayerRepository.getOne(gamePlayerId).getGameID().toSalvoDTO());
        return datazo;
    }
}
