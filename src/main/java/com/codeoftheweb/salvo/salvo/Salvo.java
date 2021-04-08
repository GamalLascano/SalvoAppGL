package com.codeoftheweb.salvo.salvo;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayerID;
    private int turn;
    @ElementCollection
    @Column(name = "cell")
    @JsonProperty("locations")
    private Set<String> locations;

    public Salvo() {
        locations = new HashSet<>();
    }

    public Salvo(GamePlayer gamePlayerID, int turn, Set<String> salvoLocations) {
        this();
        this.gamePlayerID = gamePlayerID;
        this.turn = turn;
        this.locations = salvoLocations;
    }

    public GamePlayer getGamePlayerID() {
        return gamePlayerID;
    }

    public int getTurn() {
        return turn;
    }

    public void setNewGamePlayer(GamePlayer gamePlayerID) {
        this.gamePlayerID = gamePlayerID;
    }

    public Set<String> getSalvoLocations() {
        return locations;
    }

    public Map<String, Object> toFinalSalvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", turn);
        dto.put("player", gamePlayerID.getPlayerID().getId());
        dto.put("locations", locations);
        return dto;
    }

}
