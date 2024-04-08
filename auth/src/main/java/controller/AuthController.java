package controller;

import dto.LoginDTO;
import dto.UsuarioDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AuthService;
import lombok.extern.log4j.Log4j2;


@RestController
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {

    private AuthService authService;


    @PostMapping("/login")
    public UsuarioDTO login(@RequestBody LoginDTO login) {
        System.out.println("entrou no controller");
        return authService.login(login);
    }



}
