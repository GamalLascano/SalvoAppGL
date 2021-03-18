package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime creationDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game gameID;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player playerID;

    public GamePlayer() {
    }

    public GamePlayer(LocalDateTime creationDate, Game gameID, Player playerID) {
        this.creationDate = creationDate;
        this.gameID = gameID;
        this.playerID = playerID;
    }

    public Game getGameID() {
        return gameID;
    }

    public Player getPlayerID() {
        return playerID;
    }
    public Map<String,Object> toGPDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id",this.id);
        Map<String, Object> aux = this.playerID.toPlayerDTO();
        dto.put("player",aux);
        return dto;
    }
}
