package com.codeoftheweb.salvo.player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class PlayerController {
    @Autowired
    private PlayerRepository playerRepository;
}
