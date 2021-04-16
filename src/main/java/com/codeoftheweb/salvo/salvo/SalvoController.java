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

    /**
     * This function will return the info of the salvos of a certain game player
     * @param gamePlayerId The game player that contain the salvos
     * @return A map with the necessary info
     */
    @RequestMapping("/games/players/{gamePlayerId}/salvos")
    public Map<String, Object> sendSalvos(@PathVariable Long gamePlayerId) {
        return Utils.makeMap("salvos", gamePlayerRepository.getOne(gamePlayerId).getSalvos().stream().map(a -> a.toFinalSalvoDTO()).collect(Collectors.toList()));
    }

    /**
     * This function will post a new salvo to a certain game player
     * @param gamePlayerId The game player that will recieve a new salvo
     * @param salvo The action itself
     * @param authentication The currently logged in user
     * @return Will return a OK response if correct, and an error otherwise
     */
    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> postSalvos(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        //If the user is logged in, enter
        if(!Utils.isGuest(authentication)){
            Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
            //If the game player with the gamePlayerId variable exists...
            if(gp.isPresent()){
                //... and it matches with the logged in user
                if(authentication.getName().compareTo(gp.get().getPlayerID().getUserName()) == 0){
                    //This function will search this game player's game and see the game player that doesn't have this game player's id (enemy)
                    Optional<GamePlayer> enemy = gp.get().getGameID().getGamePlayers().stream().filter(p -> p.getId() != gp.get().getId()).findFirst();
                    //If the enemy exists
                    if(enemy.isPresent()){
                        //If the game is not over
                        if(gp.get().getGameID().getScores().size()==0){
                            //If it is your turn, add the salvo
                            if(gp.get().getSalvos().size()<=enemy.get().getSalvos().size()){
                                salvo.setNewGamePlayer(gp.get());
                                salvo.setTurn(gp.get().getSalvos().size()+1);
                                salvoRepository.save(salvo);
                                response = new ResponseEntity<>(Utils.makeMap("OK", "Salvo placed"), HttpStatus.CREATED);
                            }else{
                                response = new ResponseEntity<>(Utils.makeMap("error", "It is not your turn yet"), HttpStatus.FORBIDDEN);
                            }
                        }else{
                            response = new ResponseEntity<>(Utils.makeMap("error", "Game is over"), HttpStatus.FORBIDDEN);
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
