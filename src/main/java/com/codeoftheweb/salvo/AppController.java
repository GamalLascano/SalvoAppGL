package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import com.codeoftheweb.salvo.salvo.Salvo;
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
        Optional<GamePlayer> gpenemy = gpuser.getGameID().getGamePlayers().stream().filter(p -> p.getId() != gpuser.getId()).findFirst();
        Map<String, Object> hits = new LinkedHashMap<>();
        if (gpenemy.isPresent()) {
            hits.put("self", calculateMap(gpuser,gpenemy.get()));
            hits.put("opponent", calculateMap(gpenemy.get(),gpuser));
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
    private Map<String, Object> getMap(Long gamePlayerId) {
        Map<String, Object> datazo = gamePlayerRepository.getOne(gamePlayerId).getGameID().toDTO();
        datazo.put("ships", gamePlayerRepository.getOne(gamePlayerId).getShips().stream().map(Ship::toShipDTO).collect(Collectors.toList()));
        datazo.put("salvoes", gamePlayerRepository.getOne(gamePlayerId).getGameID().toSalvoDTO());
        datazo.put("gameState", "PLACESHIPS");
        datazo.put("hits", generateHits(gamePlayerRepository.getOne(gamePlayerId)));
        return datazo;
    }
}
