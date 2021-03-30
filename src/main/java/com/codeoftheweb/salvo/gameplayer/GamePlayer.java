package com.codeoftheweb.salvo.gameplayer;

import com.codeoftheweb.salvo.player.Player;
import com.codeoftheweb.salvo.game.Game;
import com.codeoftheweb.salvo.salvo.Salvo;
import com.codeoftheweb.salvo.score.Score;
import com.codeoftheweb.salvo.ship.Ship;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

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
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Ship> ships;
    @OneToMany(mappedBy="gamePlayerID", fetch=FetchType.EAGER)
    private Set<Salvo> salvos;
    public GamePlayer() {
        this.ships = new HashSet<>();
        this.salvos = new HashSet<>();
    }

    public GamePlayer(LocalDateTime creationDate, Game gameID, Player playerID) {
        this.creationDate = creationDate;
        this.gameID = gameID;
        this.playerID = playerID;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Game getGameID() {
        return gameID;
    }

    public Player getPlayerID() {
        return playerID;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public long getId() {
        return id;
    }

    public Optional<Score> getScore(){
        return gameID.getScores().stream().filter(a-> a.getPlayer().getId() == this.playerID.getId()).findFirst();
    }
    public Map<String,Object> toGPDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id",this.id);
        Map<String, Object> aux = this.playerID.toPlayerDTO();
        dto.put("player",aux);
        if (this.getScore().isPresent()){
            dto.put("score",this.getScore().get().getScore());
        }
        return dto;
    }
}
