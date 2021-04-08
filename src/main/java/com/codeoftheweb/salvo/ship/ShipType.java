package com.codeoftheweb.salvo.ship;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ShipType {
    @JsonProperty("carrier")
    CARRIER,
    @JsonProperty("battleship")
    BATTLESHIP,
    @JsonProperty("submarine")
    SUBMARINE,
    @JsonProperty("destroyer")
    DESTROYER,
    @JsonProperty("patrol boat")
    PATROL_BOAT
}
