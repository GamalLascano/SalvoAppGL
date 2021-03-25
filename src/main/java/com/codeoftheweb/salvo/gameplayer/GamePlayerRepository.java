package com.codeoftheweb.salvo.gameplayer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GamePlayerRepository  extends JpaRepository<GamePlayer, Long> {

}
