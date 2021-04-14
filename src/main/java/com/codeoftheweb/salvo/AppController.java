package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import com.codeoftheweb.salvo.salvo.Salvo;
import com.codeoftheweb.salvo.score.Score;
import com.codeoftheweb.salvo.score.ScoreRepository;
import com.codeoftheweb.salvo.ship.Ship;
import com.codeoftheweb.salvo.ship.ShipType;
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
    @Autowired
    public ScoreRepository scoreRepository;
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
            Map<String, Object> aux = new LinkedHashMap<>();
            aux.put("problem", "gamePlayer doesn´t exist");
            response = new ResponseEntity<>(aux, HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    private Map<String, Object> generateHits(GamePlayer gpuser) {
        Optional<GamePlayer> gpenemy = gpuser.getOpponent();
        Map<String, Object> hits = new LinkedHashMap<>();
        if (gpenemy.isPresent()) {
            hits.put("self", calculateMap(gpenemy.get(),gpuser));
            hits.put("opponent", calculateMap(gpuser,gpenemy.get()));
        }else{
            hits.put("self", new ArrayList<>());
            hits.put("opponent", new ArrayList<>());
        }
        return hits;
    }

private List<Map<String, Object>> calculateMap(GamePlayer gp1, GamePlayer gp2) {
    int[] hits = new int[5];
    int[] total = new int[5];
    Arrays.fill(total,0);
    int missedShots;
    List<Map<String, Object>> mapListAux = new LinkedList<>();
    Map<String, Object> mapAux;
    Map<String, Object> mapDamages;
    List<Salvo> salvoAux = new ArrayList<>(gp1.getSalvos());
    salvoAux.sort(Comparator.comparing(Salvo::getTurn));
    List<Optional<Ship>> enemyList = new LinkedList<>();
    for(ShipType st :ShipType.values()){//0 CARRIER,1 BATTLESHIP,2 SUBMARINE,3 DESTROYER,4 PATROL BOAT
        enemyList.add(gp2.getShips().stream().filter(s -> s.getType() == st).findFirst());
    }
    if(enemyList.get(0).isPresent()&&enemyList.get(1).isPresent()&&enemyList.get(2).isPresent()&&enemyList.get(3).isPresent()&&enemyList.get(4).isPresent()){
        for (Salvo aux : salvoAux) {
            Arrays.fill(hits, 0);
            mapAux = new LinkedHashMap<>();
            mapAux.put("turn", aux.getTurn());
            Set<String> hitLoc = new HashSet<>();
            mapDamages = new LinkedHashMap<>();
            missedShots = aux.getSalvoLocations().size();
            for (String locAux : aux.getSalvoLocations()) {
                for (int j = 0; j < enemyList.size(); j++) {
                    if(enemyList.get(j).isPresent()){
                        if (enemyList.get(j).get().getLocations().contains(locAux)) {
                            hitLoc.add(locAux);
                            missedShots--;
                            hits[j]++;
                            total[j]++;
                        }
                    }
                }
            }
            for(ShipType st : ShipType.values()){
                mapDamages.put(st.name().replace("_","").toLowerCase(Locale.ROOT).concat("Hits"),hits[st.getValue()]);
                mapDamages.put(st.name().replace("_","").toLowerCase(Locale.ROOT), total[st.getValue()]);
            }
            mapAux.put("hitLocations", hitLoc);
            mapAux.put("damages", mapDamages);
            mapAux.put("missed", missedShots);
            mapListAux.add(mapAux);
        }
    }
    return mapListAux;
}
private boolean gameWon(Map<String, Object> lastTurn){
    int[] damage = new int[5];
    damage[0] = (int)lastTurn.get("carrier");
    damage[1] = (int)lastTurn.get("battleship");
    damage[2] = (int)lastTurn.get("submarine");
    damage[3] = (int)lastTurn.get("destroyer");
    damage[4] = (int)lastTurn.get("patrolboat");
    if(damage[0]==5&&damage[1]==4&&damage[2]==3&&damage[3]==3&&damage[4]==2){
        return true;
    }else return false;
}
private String getGameStateCalculation(GamePlayer gp,Map<String, Object> hitMap){
        if(gp.getShips().size()==0){
            return "PLACESHIPS";
        }
        Optional <GamePlayer> opponent = gp.getOpponent();
        if(opponent.isEmpty()){
            return "WAITINGFOROPP";
        }
        if(opponent.get().getShips().size()==0){
            return "WAITINGFOROPP";
        }
        if(gp.getSalvos().size()<opponent.get().getSalvos().size()){
            return "PLAY";
        }else{
           if(gp.getSalvos().size()>opponent.get().getSalvos().size()) return "WAIT";
            if(gp.getSalvos().size()==0) {
                return "PLAY";
            }else{
                if(opponent.get().getSalvos().size()==0) return "WAIT";
            }
            List<Map<String, Object>> gpListHit = (List<Map<String, Object>>) hitMap.get("self");
            Map<String, Object> lastTurnAlly = gpListHit.get(gpListHit.size()-1);
            lastTurnAlly = (Map<String, Object>)lastTurnAlly.get("damages");
            List<Map<String, Object>> enemyListHit = (List<Map<String, Object>>) hitMap.get("opponent");
            Map<String, Object> lastTurnOpponent = enemyListHit.get(enemyListHit.size()-1);
            lastTurnOpponent = (Map<String, Object>)lastTurnOpponent.get("damages");
            if(!gameWon(lastTurnAlly)&&!gameWon(lastTurnOpponent)){
                return "PLAY";
            }else{
                if(gameWon(lastTurnAlly)){
                    if(gameWon(lastTurnOpponent)){
                        return setScores(gp,opponent.get(),1);
                    }else{
                        return setScores(gp,opponent.get(),2);
                    }
                }else{
                    return setScores(gp, opponent.get(),0);
                }
            }
        }
}
private String setScores(GamePlayer gp1, GamePlayer gp2, int mode){
    if(gp1.getGameID().getScores().size()==0){
        switch (mode){
            case 0: //Win
                scoreRepository.save(new Score(gp1.getGameID(),gp1.getPlayerID(),1));
                scoreRepository.save(new Score(gp2.getGameID(),gp2.getPlayerID(),0));
                break;
            case 1: //Tie
                scoreRepository.save(new Score(gp1.getGameID(),gp1.getPlayerID(),0.5));
                scoreRepository.save(new Score(gp2.getGameID(),gp2.getPlayerID(),0.5));
                break;
            case 2: //Lose
                scoreRepository.save(new Score(gp1.getGameID(),gp1.getPlayerID(),0));
                scoreRepository.save(new Score(gp2.getGameID(),gp2.getPlayerID(),1));
            break;
        }
    }
    switch(mode){
        case 0: //Win
            return "WIN";
        case 1: //Tie
            return "TIE";
        case 2: //Lose
            return "LOST";
    }
    return "EXCEPTION";
}
private Map<String, Object> getMap(Long gamePlayerId) {
        Map<String, Object> hitMap = generateHits(gamePlayerRepository.getOne(gamePlayerId));
        String gst = getGameStateCalculation(gamePlayerRepository.getOne(gamePlayerId),hitMap);
        Map<String, Object> datazo = gamePlayerRepository.getOne(gamePlayerId).getGameID().toDTO();
        datazo.put("ships", gamePlayerRepository.getOne(gamePlayerId).getShips().stream().map(Ship::toShipDTO).collect(Collectors.toList()));
        datazo.put("salvoes", gamePlayerRepository.getOne(gamePlayerId).getGameID().toSalvoDTO());
        datazo.put("gameState",gst );
        datazo.put("hits", hitMap);
        return datazo;
    }
}
