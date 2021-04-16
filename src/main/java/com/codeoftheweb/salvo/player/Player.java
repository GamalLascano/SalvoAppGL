package com.codeoftheweb.salvo.player;

import com.codeoftheweb.salvo.game.Game;
import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.score.Score;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class stores the info for the player, like the user name, the games that it's in, the scores and the password
 */
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    @OneToMany(mappedBy = "playerID", fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    Set<GamePlayer> games = new HashSet<>();
    @OneToMany(mappedBy = "playerID", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Score> scores = new HashSet<>();
    private String password;

    public Player() {
    }

    public Player(String user) {
        this.userName = user;
    }

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This function will retrieve the score linked to this player and the game passed by parameter
     * @param gamePar The game where that score is saved
     * @return An optional score
     */
    public Optional<Score> getScore(Game gamePar) {
        return scores.stream().filter(a -> a.getGame().equals(gamePar)).findFirst();
    }

    /**
     * This function will add a game player to this player
     * @param gamePla The required game player
     */
    public void addGamePlayers(GamePlayer gamePla) {
        games.add(gamePla);
    }

    @JsonIgnore
    public List<Game> getGames() {
        return games.stream().map(pla -> pla.getGameID()).collect(Collectors.toList());
    }

    /**
     * Returns the relevant info of a player in a map, requiered to send as a json object to the front end
     * @return A map containing the id and the email(username) of this player
     */
    public Map<String, Object> toPlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.id);
        dto.put("email", this.userName);
        return dto;
    }

}
