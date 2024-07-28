package com.apigateway.auth.auth.controller;

import com.apigateway.auth.auth.dto.ClienteDTO;
import com.apigateway.auth.auth.dto.GerenteDTO;
import com.apigateway.auth.auth.dto.LoginDTO;
import com.apigateway.auth.auth.dto.UserDTO;
import com.apigateway.auth.auth.helper.UserHelper;
import com.apigateway.auth.auth.model.User;
import com.apigateway.auth.auth.repositories.UserRepository;
import com.apigateway.auth.auth.services.MessagingService;
import com.apigateway.auth.auth.utils.Response;
import com.apigateway.auth.auth.utils.SecurityUtil;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

import static com.apigateway.auth.auth.utils.SecurityUtil.getSalt;
import static com.apigateway.auth.auth.utils.SecurityUtil.getSecurePassword;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "API: Auth",
        description = "Contém todos os endpoints relacionados a autenticação"
)
public class UserController {
    @Autowired
    private UserRepository repo;
    @Autowired
    private UserHelper helper;
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private Gson gson;

    @PostMapping("login")
    @Operation(
            summary = "Endpoint para autenticação",
            description = "verifica se existe um usuário cadastrado como login e senha informado"
    )
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            if (loginDTO.getEmail() == null || loginDTO.getSenha() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do login inválidos", null), HttpStatus.BAD_REQUEST);
            }

            Optional<User> userOptional = repo.findByEmail(loginDTO.getEmail());
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>(new Response(false, "Usuário não encontrado", null), HttpStatus.NOT_FOUND);
            }
            User user = userOptional.get();
            byte[] salt = Base64.getDecoder().decode(user.getSalt());
            String securePassword = SecurityUtil.getSecurePassword(loginDTO.getSenha(), salt);

            System.out.println(securePassword);

            if (securePassword.equals(user.getSenha())) {
                User safeUser = new User();
                safeUser.setId(user.getId());
                safeUser.setNome(user.getNome());
                safeUser.setEmail(user.getEmail());
                safeUser.setCargo(user.getCargo());

                if(user.getCargo().equals("GERENTE")){
                    String jsonManager = (String) messagingService.sendAndReceiveMessage("manager.check.email", user.getEmail());
                    GerenteDTO manager = gson.fromJson(jsonManager, GerenteDTO.class);
                    safeUser.setGerente(manager);
                }else if(user.getCargo().equals("CLIENTE")){
                    String jsonCliente = (String) messagingService.sendAndReceiveMessage("client.check.email", user.getEmail());
                    ClienteDTO cliente = gson.fromJson(jsonCliente, ClienteDTO.class);
                    safeUser.setCliente(cliente);
                }
                return new ResponseEntity<>(new Response(true, "Login bem-sucedido", safeUser), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new Response(false, "Senha incorreta", null), HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("adicionar")
    @Operation(
            summary = "Endpoint para adicionar usuário",
            description = "apenas caso precise adicionar usuário tipo administrador"
    )
    public ResponseEntity<Object> adicionar(@RequestBody @Valid UserDTO userDTO) {
        try {
            return helper.removeUser(userDTO.getEmail());
        }catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.BAD_REQUEST);
        }
    }
}
