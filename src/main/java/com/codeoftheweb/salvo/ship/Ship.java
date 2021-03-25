package com.codeoftheweb.salvo.ship;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;
    private ShipType shipType;
    @ElementCollection
    @Column(name="cell")
    private Set<String> shipLocations;
    public Ship() {
        shipLocations = new HashSet<>();
    }

    public Ship(GamePlayer gamePlayerID, ShipType shipType, Set<String> shipLocations) {
        this();
        this.gamePlayer = gamePlayerID;
        this.shipType = shipType;
        this.shipLocations = shipLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public ShipType getShipType() {
        return shipType;
    }
    public Map<String,Object> toShipDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type",this.shipType);
        dto.put("locations",this.shipLocations);
        return dto;
    }
    public Set<String> getShipLocations() {
        return shipLocations;
    }
}
