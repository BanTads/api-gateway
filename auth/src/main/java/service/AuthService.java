package service;

import dto.LoginDTO;
import dto.UsuarioDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import util.TipoEnum;

import java.util.Optional;

public class AuthService {

        public UsuarioDTO login(LoginDTO login){
            System.out.println("Login: " + login.getLogin());
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setLogin("teste");
            usuario.setTipo(TipoEnum.ADMIN);
            return usuario;
        }





}
