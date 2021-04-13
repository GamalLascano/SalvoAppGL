package com.codeoftheweb.salvo.ship;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShipType {
    @JsonProperty("carrier")
    CARRIER(0),
    @JsonProperty("battleship")
    BATTLESHIP(1),
    @JsonProperty("submarine")
    SUBMARINE(2),
    @JsonProperty("destroyer")
    DESTROYER(3),
    @JsonProperty("patrolboat")
    PATROL_BOAT(4);
    private int value;
    ShipType(int value){
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
