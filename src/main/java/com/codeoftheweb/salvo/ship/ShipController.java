package com.codeoftheweb.salvo.ship;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import com.codeoftheweb.salvo.salvo.Salvo;
import com.codeoftheweb.salvo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ShipController {
    @Autowired
    public GamePlayerRepository gamePlayerRepository;
    @Autowired
    public ShipRepository shipRepository;

    /**
     * This method will retrieve all the ships of a certain game player
     * @param gamePlayerId The game player that wants it's ships
     * @return A map with a list of ship info
     */
    @RequestMapping("/games/players/{gamePlayerId}/ships")
    public Map<String, Object> sendShips(@PathVariable Long gamePlayerId) {
        return Utils.makeMap("ships", gamePlayerRepository.getOne(gamePlayerId).getShips().stream().map(a -> a.toShipDTO()).collect(Collectors.toList()));
    }

    /**
     * A method that will assign a set of ships to a game player
     * @param gamePlayerId The game player that will get new ships
     * @param ships The ship set
     * @param authentication The currently logged in user
     * @return A response indicating if the ships were placed correctly or not
     */
    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> postShips(@PathVariable Long gamePlayerId, @RequestBody Set<Ship> ships, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        //If the user is logged in
        if(!Utils.isGuest(authentication)){
            Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);
            //If that game player id exist
            if(gp.isPresent()){
                //If the current logged in user has the same player id as this game player
                if(authentication.getName().compareTo(gp.get().getPlayerID().getUserName()) == 0){
                    //If the user doesn't have ships, add ships
                    if(gp.get().getShips().size()==0){
                        ships.forEach(s -> {
                            s.setGamePlayer(gp.get());
                            shipRepository.save(s);
                        });
                        response = new ResponseEntity<>(Utils.makeMap("OK", "Ships placed"), HttpStatus.CREATED);
                    }else{
                        response = new ResponseEntity<>(Utils.makeMap("error", "Ships are already placed"), HttpStatus.FORBIDDEN);
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
