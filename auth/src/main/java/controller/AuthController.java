package controller;

import dto.UsuarioDTO;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import service.AuthService;

public class AuthController {

    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody UsuarioDTO usuario) {
        return ResponseEntity.ok(authService.login(usuario));
    }



}
