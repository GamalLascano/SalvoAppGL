package com.codeoftheweb.salvo.game;

import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.player.Player;
import com.codeoftheweb.salvo.score.Score;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime creationDate;
    @OneToMany(mappedBy = "gameID", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GamePlayer> players = new HashSet<>();
    @OneToMany(mappedBy = "gameID", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();

    public Game() {
    }

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public List<Player> getPlayers() {
        return players.stream().map(pla -> pla.getPlayerID()).collect(Collectors.toList());
    }

    public void addGamePlayers(GamePlayer gamePla) {
        players.add(gamePla);
    }

    public Set<Score> getScores() {
        return scores;
    }

    public long getId() {
        return id;
    }

    public Map<String, Object> toDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.id);
        dto.put("created", this.creationDate);
        List<Object> aux = players.stream().map(a -> a.toGPDTO()).collect(Collectors.toList());
        dto.put("gamePlayers", aux);
        List<Map<String, Object>> scores = players.stream()
                .map(gp -> gp.getScore()).filter(score -> score.isPresent()).map(score -> score.get().toScoreDTO())
                .collect(Collectors.toList());
        dto.put("scores", scores);
        return dto;
    }

    public List<Object> toSalvoDTO() {
        List<Object> aux = players.stream().flatMap(a -> a.getSalvos().stream().map(b -> b.toFinalSalvoDTO())).collect(Collectors.toList());
        return aux;
    }
}
