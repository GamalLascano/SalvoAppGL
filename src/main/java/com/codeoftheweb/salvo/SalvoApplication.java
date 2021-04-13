package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.game.Game;
import com.codeoftheweb.salvo.game.GameRepository;
import com.codeoftheweb.salvo.gameplayer.GamePlayer;
import com.codeoftheweb.salvo.gameplayer.GamePlayerRepository;
import com.codeoftheweb.salvo.player.Player;
import com.codeoftheweb.salvo.player.PlayerRepository;
import com.codeoftheweb.salvo.salvo.Salvo;
import com.codeoftheweb.salvo.salvo.SalvoRepository;
import com.codeoftheweb.salvo.score.Score;
import com.codeoftheweb.salvo.score.ScoreRepository;
import com.codeoftheweb.salvo.ship.Ship;
import com.codeoftheweb.salvo.ship.ShipRepository;
import com.codeoftheweb.salvo.ship.ShipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {
    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository repository1, GameRepository repository2, GamePlayerRepository repository3, ShipRepository repository4, SalvoRepository repository5, ScoreRepository repository6) {
        return (args) -> {
            // save a couple of customers
            Player p1 = new Player("j.bauer@ctu.gov");
            Player p2 = new Player("c.obrian@ctu.gov");
            Player p3 = new Player("t.almeida@ctu.gov");
            Player p4 = new Player("d.palmer@whitehouse.gov");
            p1.setPassword(passwordEncoder().encode("1234"));
            p2.setPassword(passwordEncoder().encode("1234"));
            p3.setPassword(passwordEncoder().encode("1234"));
            p4.setPassword(passwordEncoder().encode("1234"));
            repository1.save(p1);
            repository1.save(p2);
            repository1.save(p3);
            repository1.save(p4);
            // save a couple of games

            LocalDateTime actualDateTime = LocalDateTime.of(2018, 2, 17, 15, 20, 15);
            Game[] gameList = new Game[6];
            for (int i = 0; i <= 5; i++) {
                gameList[i] = new Game(actualDateTime.plusHours(i));
                repository2.save(gameList[i]);
            }
            //Partida 1
            GamePlayer gp1 = new GamePlayer(actualDateTime, gameList[0], p1);
            repository3.save(gp1);
            GamePlayer gp2 = new GamePlayer(actualDateTime, gameList[0], p2);
            repository3.save(gp2);
            //Partida 2
            GamePlayer gp3 = new GamePlayer(actualDateTime, gameList[1], p1);
            repository3.save(gp3);
            GamePlayer gp4 = new GamePlayer(actualDateTime, gameList[1], p2);
            repository3.save(gp4);
            //Partida 3
            repository3.save(new GamePlayer(actualDateTime, gameList[2], p2));
            repository3.save(new GamePlayer(actualDateTime, gameList[2], p3));
            //Partida 4
            repository3.save(new GamePlayer(actualDateTime, gameList[3], p1));
            repository3.save(new GamePlayer(actualDateTime, gameList[3], p2));
            //Partida 5
            repository3.save(new GamePlayer(actualDateTime, gameList[4], p3));
            repository3.save(new GamePlayer(actualDateTime, gameList[4], p1));
            //Partida 6
            repository3.save(new GamePlayer(actualDateTime, gameList[5], p4));
/*            Ship nave1 = repository4.save(new Ship(gp1, ShipType.DESTROYER, new HashSet(Arrays.asList("H2", "H3", "H4"))));
            Ship nave2 = repository4.save(new Ship(gp1, ShipType.SUBMARINE, new HashSet(Arrays.asList("E1", "F1", "G1"))));
            Ship nave3 = repository4.save(new Ship(gp1, ShipType.PATROL_BOAT, new HashSet(Arrays.asList("B4", "B5"))));
            Ship nave4 = repository4.save(new Ship(gp2, ShipType.SUBMARINE, new HashSet(Arrays.asList("B5", "C5", "D5"))));
            Ship nave5 = repository4.save(new Ship(gp2, ShipType.PATROL_BOAT, new HashSet(Arrays.asList("F1", "F2"))));
            Ship nave6 = repository4.save(new Ship(gp3, ShipType.DESTROYER, new HashSet(Arrays.asList("B5", "C5", "D5"))));
            Ship nave7 = repository4.save(new Ship(gp3, ShipType.PATROL_BOAT, new HashSet(Arrays.asList("C6", "C7"))));
            Ship nave8 = repository4.save(new Ship(gp4, ShipType.SUBMARINE, new HashSet(Arrays.asList("A2", "A3", "A4"))));
            Ship nave9 = repository4.save(new Ship(gp4, ShipType.PATROL_BOAT, new HashSet(Arrays.asList("G6", "H6"))));
            repository5.save(new Salvo(gp1, 1, new HashSet(Arrays.asList("H2", "H3", "H4"))));
            repository5.save(new Salvo(gp2, 1, new HashSet(Arrays.asList("B4", "B5", "B6"))));
            repository5.save(new Salvo(gp1, 2, new HashSet(Arrays.asList("F2", "D5"))));
            repository5.save(new Salvo(gp2, 2, new HashSet(Arrays.asList("E1", "H3", "A2"))));
            repository5.save(new Salvo(gp3, 1, new HashSet(Arrays.asList("A2", "A4", "G6"))));
            repository5.save(new Salvo(gp3, 2, new HashSet(Arrays.asList("A3", "H6"))));
            repository5.save(new Salvo(gp4, 1, new HashSet(Arrays.asList("B5", "D5", "C7"))));
            repository5.save(new Salvo(gp4, 2, new HashSet(Arrays.asList("C5", "C6"))));*/
            repository6.save(new Score(gp1.getGameID(), gp1.getPlayerID(), 1));
            repository6.save(new Score(gp2.getGameID(), gp2.getPlayerID(), 0));
            repository6.save(new Score(gp3.getGameID(), gp3.getPlayerID(), 0.5));
            repository6.save(new Score(gp4.getGameID(), gp4.getPlayerID(), 0.5));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName -> {
            Player player = playerRepository.findByUserName(inputName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/games", "/api/players", "/api/login")
                .permitAll()
                .antMatchers("/web/game.html")
                .hasAuthority("USER").antMatchers("/web/**").permitAll()
                .antMatchers("/h2-console/**").permitAll().anyRequest().authenticated()
                .and().csrf().ignoringAntMatchers("/h2-console/**")
                .and().headers().frameOptions().sameOrigin()
        ;
        http.formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();
        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
