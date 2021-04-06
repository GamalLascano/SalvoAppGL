package com.codeoftheweb.salvo;

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
    public ResponseEntity<Map<String, Object>> findOwner(@PathVariable Long gamePlayerId) {
        Optional<GamePlayer> gp=gamePlayerRepository.findById(gamePlayerId);
        ResponseEntity<Map<String, Object>> response;
        if(gp.isPresent()){
            response=new ResponseEntity<>(getMap(gamePlayerId), HttpStatus.OK);
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
