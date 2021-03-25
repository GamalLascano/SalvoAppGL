package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.game.GameRepository;
import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping("/games")
    public List<Object>getGameID(){
        return gameRepository.findAll().stream().map(game -> game.toDTO()).collect(Collectors.toList());
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
    private Map<String, Object> getMap(Long gamePlayerId){
        Map<String, Object> datazo = gamePlayerRepository.getOne(gamePlayerId).getGameID().toDTO();
        datazo.put("ships",gamePlayerRepository.getOne(gamePlayerId).getShips().stream().map(a -> a.toShipDTO()).collect(Collectors.toList()));
        return datazo;
    }
}
