package com.codeoftheweb.salvo.ship;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class includes all the info relevant for a ship, like the game player, the ship type and the location of the ship
 */
@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    private ShipType type;

    @ElementCollection
    @Column(name="cell")
    private Set<String> locations;
    public Ship() {
        locations = new HashSet<>();
    }

    public Ship(GamePlayer gamePlayerID, ShipType shipType, Set<String> shipLocations) {
        this();
        this.gamePlayer = gamePlayerID;
        this.type = shipType;
        this.locations = shipLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public ShipType getType() {
        return type;
    }

    public void setType(ShipType type) {
        this.type = type;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public void setLocations(Set<String> locations) {
        this.locations = locations;
    }

    /**
     * This method includes all the relevant ship information, including the type and the position of the ship
     * @return A map with said info
     */
    public Map<String,Object> toShipDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type",this.type);
        dto.put("locations",this.locations.stream().sorted().collect(Collectors.toList()));
        return dto;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Ship(ShipType shipType, Set<String> shipLocations) {
        this();
        this.type = shipType;
        this.locations = shipLocations;
    }

    public Set<String> getShipLocations() {
        return locations;
    }
}
