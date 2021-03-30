package com.codeoftheweb.salvo.player;

import com.codeoftheweb.salvo.game.Game;
import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.score.Score;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    @OneToMany(mappedBy="playerID", fetch=FetchType.EAGER)
    Set<GamePlayer> games = new HashSet<>();
    @OneToMany(mappedBy="playerID", fetch=FetchType.EAGER)
    Set<Score> scores= new HashSet<>();
    public Player() {
    }

    public Player(String user) {
        this.userName = user;
    }

    public String getUserName() {
        return userName;
    }

    public long getId() {
        return id;
    }

    public Optional<Score> getScore(Game gamePar){
        return scores.stream().filter(a -> a.getGame().equals(gamePar)).findFirst();
    }
    public Map<String,Object> makeScores(){
        Map<String,Object> res = new LinkedHashMap<String, Object>();
        int win=(int)scores.stream().filter(a->a.getScore()==1.0).count();
        int loss=(int)scores.stream().filter(a->a.getScore()==0).count();
        int draw=(int)scores.stream().filter(a->a.getScore()==0.5).count();
        double count = scores.stream().mapToDouble(Score::getScore).sum();
        res.put("wins",win);
        res.put("losses",loss);
        res.put("draws",draw);
        res.put("total",count);
        return res;
    }
    public void addGamePlayers(GamePlayer gamePla) {
        games.add(gamePla);
    }
    @JsonIgnore
    public List<Game> getGames(){
        return games.stream().map(pla -> pla.getGameID()).collect(Collectors.toList());
    }
    public Map<String,Object> toPlayerDTO(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id",this.id);
        dto.put("email",this.userName);
        dto.put("score",this.makeScores());
        return dto;
    }

}
