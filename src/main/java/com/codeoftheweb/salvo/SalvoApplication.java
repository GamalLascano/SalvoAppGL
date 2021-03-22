package com.codeoftheweb.salvo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository repository1,GameRepository repository2,GamePlayerRepository repository3) {
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
			repository3.save(new GamePlayer(actualDateTime,gameList[0],p1));
			repository3.save(new GamePlayer(actualDateTime,gameList[0],p2));
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
		};
	}

}
