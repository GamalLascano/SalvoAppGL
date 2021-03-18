package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    @OneToMany(mappedBy="playerID", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;
    public Player() {
    }

    public Player(String user) {
        this.userName = user;
    }

    public String getUserName() {
        return userName;
    }

    public void addGamePlayers(GamePlayer gamePla) {
        gamePlayers.add(gamePla);
    }
    @JsonIgnore
    public List<Game> getGames(){
        return gamePlayers.stream().map(pla -> pla.getGameID()).collect(Collectors.toList());
    }
    public Map<String,Object> toPlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id",this.id);
        dto.put("email",this.userName);
        return dto;
    }

}
