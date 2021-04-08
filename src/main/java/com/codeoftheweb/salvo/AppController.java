package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import com.codeoftheweb.salvo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    public GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> findOwner(@PathVariable Long gamePlayerId, Authentication authentication) {
        Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
        ResponseEntity<Map<String, Object>> response;
        if (gp.isPresent()) {
            if (authentication.getName().compareTo(gp.get().getPlayerID().getUserName()) == 0) {
                response = new ResponseEntity<>(getMap(gamePlayerId), HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(Utils.makeMap("problem", "Player is not authorized"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            Map<String, Object> aux = new LinkedHashMap<String, Object>();
            aux.put("problem", "gamePlayer doesn´t exist");
            response = new ResponseEntity<>(aux, HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    private Map<String, Object> getMap(Long gamePlayerId) {
        Map<String, Object> hits  = new LinkedHashMap<String, Object>();
        hits.put("self",new ArrayList<>());
        hits.put("opponent",new ArrayList<>());
        Map<String, Object> datazo = gamePlayerRepository.getOne(gamePlayerId).getGameID().toDTO();
        datazo.put("ships", gamePlayerRepository.getOne(gamePlayerId).getShips().stream().map(a -> a.toShipDTO()).collect(Collectors.toList()));
        datazo.put("salvoes", gamePlayerRepository.getOne(gamePlayerId).getGameID().toSalvoDTO());
        datazo.put("gameState", "PLACESHIPS");
        datazo.put("hits",hits);
        return datazo;
    }
}
