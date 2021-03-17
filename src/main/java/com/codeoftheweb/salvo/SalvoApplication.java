package com.codeoftheweb.salvo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(new Player("jack@gmail.com"));
			repository.save(new Player("chloe@gmail.com"));
			repository.save(new Player("kim@gmail.com"));
			repository.save(new Player("david@gmail.com"));
			repository.save(new Player("michelle@gmail.com"));
		};
	}
	@Bean
	public CommandLineRunner initData2(GameRepository repository) {
		return (args) -> {
			// save a couple of games
			LocalDateTime actualDateTime = LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
			repository.save(new Game(actualDateTime));
			repository.save(new Game(actualDateTime.plusHours(1)));
			repository.save(new Game(actualDateTime.plusHours(2)));
		};
	}

}
