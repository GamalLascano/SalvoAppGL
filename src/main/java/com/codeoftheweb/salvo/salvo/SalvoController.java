package com.codeoftheweb.salvo.salvo;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import com.codeoftheweb.salvo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    public GamePlayerRepository gamePlayerRepository;
    @Autowired
    public SalvoRepository salvoRepository;
    @RequestMapping("/games/players/{gamePlayerId}/salvos")
    public Map<String, Object> sendSalvos(@PathVariable Long gamePlayerId) {
        return Utils.makeMap("salvos", gamePlayerRepository.getOne(gamePlayerId).getSalvos().stream().map(a -> a.toFinalSalvoDTO()).collect(Collectors.toList()));
    }

    @PostMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> postSalvos(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if(!Utils.isGuest(authentication)){
            Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
            if(gp.isPresent()){
                if(authentication.getName().compareTo(gp.get().getPlayerID().getUserName()) == 0){
                    Optional<GamePlayer> enemy = gp.get().getGameID().getGamePlayers().stream().filter(p -> p.getId() != gp.get().getId()).findFirst();
                    if(enemy.isPresent()){
                        if(gp.get().getSalvos().size()<=enemy.get().getSalvos().size()){
                            salvo.setNewGamePlayer(gp.get());
                            salvo.setTurn(gp.get().getSalvos().size()+1);
                            salvoRepository.save(salvo);
                            response = new ResponseEntity<>(Utils.makeMap("OK", "Salvo placed"), HttpStatus.CREATED);
                        }else{
                            response = new ResponseEntity<>(Utils.makeMap("error", "It is not your turn yet"), HttpStatus.FORBIDDEN);
                        }
                    }else{
                        response = new ResponseEntity<>(Utils.makeMap("error", "Enemy is not present"), HttpStatus.FORBIDDEN);
                    }
                }else{
                    response = new ResponseEntity<>(Utils.makeMap("error", "Player is not authorized"), HttpStatus.UNAUTHORIZED);
                }
            }else{
                response =new ResponseEntity<>(Utils.makeMap("error", "GamePlayer does not exist"), HttpStatus.UNAUTHORIZED);
            }
        }else{
            response =new ResponseEntity<>(Utils.makeMap("error", "User is not logged in"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }
}
