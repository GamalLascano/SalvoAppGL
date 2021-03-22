package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime creationDate;
    @OneToMany(mappedBy="gameID", fetch=FetchType.EAGER)
    Set<GamePlayer> players;
    public Game() {
    }

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public List<Player> getPlayers(){
        return players.stream().map(pla -> pla.getPlayerID()).collect(Collectors.toList());
    }
    public void addGamePlayers(GamePlayer gamePla) {
        players.add(gamePla);
    }

    public long getId() {
        return id;
    }
    public Map<String,Object> toDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id",this.id);
        dto.put("created",this.creationDate);
        List<Object> aux = players.stream().map(a -> a.toGPDTO()).collect(Collectors.toList());
        dto.put("gamePlayers",aux);
        return dto;
    }
}
