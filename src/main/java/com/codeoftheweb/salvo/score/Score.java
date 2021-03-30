package com.codeoftheweb.salvo.score;

import com.codeoftheweb.salvo.game.Game;
import com.codeoftheweb.salvo.player.Player;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game gameID;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player playerID;
    private double score;
    private LocalDateTime finishDate;

    public Score() {
    }

    public Score(Game gameID, Player playerID, double score) {
        this.finishDate = gameID.getCreationDate().plusMinutes(30);
        this.gameID = gameID;
        this.playerID = playerID;
        this.score = score;
    }
    public Map<String,Object> toScoreDTO(){
        Map<String,Object> aux = new LinkedHashMap<String, Object>();
        aux.put("score",score);
        aux.put("player",playerID.getId());
        return aux;
    }
    public Game getGame() {
        return gameID;
    }

    public Player getPlayer() {
        return playerID;
    }

    public double getScore() {
        return score;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }
}
