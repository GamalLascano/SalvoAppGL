package com.codeoftheweb.salvo;
import com.codeoftheweb.salvo.game.Game;
import com.codeoftheweb.salvo.game.GameRepository;
import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import com.codeoftheweb.salvo.player.Player;
import com.codeoftheweb.salvo.player.PlayerRepository;
import com.codeoftheweb.salvo.salvo.Salvo;
import com.codeoftheweb.salvo.salvo.SalvoRepository;
import com.codeoftheweb.salvo.ship.Ship;
import com.codeoftheweb.salvo.ship.ShipRepository;
import com.codeoftheweb.salvo.ship.ShipType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository repository1, GameRepository repository2, GamePlayerRepository repository3, ShipRepository repository4, SalvoRepository repository5) {
		return (args) -> {
			// save a couple of customers
			Player p1 = new Player("j.bauer@ctu.gov");
			Player p2 = new Player("c.obrian@ctu.gov");
			Player p3 = new Player("t.almeida@ctu.gov");
			Player p4 = new Player("d.palmer@whitehouse.gov");
			repository1.save(p1);
			repository1.save(p2);
			repository1.save(p3);
			repository1.save(p4);
			// save a couple of games

			LocalDateTime actualDateTime=LocalDateTime.of(2018, 2, 17, 15, 20,15);
			Game[] gameList = new Game[6];
			for(int i=0;i<=5;i++){
				gameList[i] = new Game(actualDateTime.plusHours(i));
				repository2.save(gameList[i]);
			}
			//Partida 1
			GamePlayer gp1= new GamePlayer(actualDateTime,gameList[0],p1);
			repository3.save(gp1);
			GamePlayer gp2=new GamePlayer(actualDateTime,gameList[0],p2);
			repository3.save(gp2);
			//Partida 2
			repository3.save(new GamePlayer(actualDateTime,gameList[1],p1));
			repository3.save(new GamePlayer(actualDateTime,gameList[1],p2));
			//Partida 3
			repository3.save(new GamePlayer(actualDateTime,gameList[2],p2));
			repository3.save(new GamePlayer(actualDateTime,gameList[2],p3));
			//Partida 4
			repository3.save(new GamePlayer(actualDateTime,gameList[3],p1));
			repository3.save(new GamePlayer(actualDateTime,gameList[3],p2));
			//Partida 5
			repository3.save(new GamePlayer(actualDateTime,gameList[4],p3));
			repository3.save(new GamePlayer(actualDateTime,gameList[4],p1));
			//Partida 6
			repository3.save(new GamePlayer(actualDateTime,gameList[5],p4));
			Ship nave1 = repository4.save(new Ship(gp1, ShipType.DESTROYER,new HashSet(Arrays.asList("H2", "H3", "H4"))));
			Ship nave2 = repository4.save(new Ship(gp1,ShipType.SUBMARINE,new HashSet(Arrays.asList("F3", "G3", "H3"))));
			Ship nave3 = repository4.save(new Ship(gp1,ShipType.PATROL_BOAT,new HashSet(Arrays.asList("B4", "B5"))));
			Ship nave4 = repository4.save(new Ship(gp2,ShipType.SUBMARINE,new HashSet(Arrays.asList("B5", "C5", "D5"))));
			Ship nave5 = repository4.save(new Ship(gp2,ShipType.PATROL_BOAT,new HashSet(Arrays.asList("F1", "F2"))));
			repository5.save(new Salvo(gp1,1,new HashSet(Arrays.asList("H2", "H3", "H4"))));
			repository5.save(new Salvo(gp2,1,new HashSet(Arrays.asList("B4", "B5", "B6"))));
			repository5.save(new Salvo(gp1,2,new HashSet(Arrays.asList("F2", "D5"))));
			repository5.save(new Salvo(gp2,2,new HashSet(Arrays.asList("E1", "H3", "A2"))));
		};
	}

}
