package com.apigateway.auth.auth.helper;

import com.apigateway.auth.auth.dto.UserChangeDTO;
import com.apigateway.auth.auth.dto.UserDTO;
import com.apigateway.auth.auth.model.User;
import com.apigateway.auth.auth.repositories.UserRepository;
import com.apigateway.auth.auth.services.EmailService;
import com.apigateway.auth.auth.utils.Response;
import com.apigateway.auth.auth.utils.SecurityUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserHelper {
    @Autowired
    private UserRepository repo;
    @Autowired
    private EmailService emailService;
    public ResponseEntity<Object> saveUser(UserDTO userDTO) {
        try {
            if (userDTO.getNome() == null || userDTO.getEmail() == null || userDTO.getCargo() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do usuário inválidos", null, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
            }
            Optional<User> userOptional = repo.findByEmail(userDTO.getEmail());
            if (!userOptional.isEmpty()) {
                return new ResponseEntity<>(new Response(false, "O e-mail já está sendo utilizado", null, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
            }

            User user = new User();
            user.setNome(userDTO.getNome());
            user.setEmail(userDTO.getEmail());
            user.setCargo(userDTO.getCargo());

            String randomPassword = SecurityUtil.generateRandomPassword(12);
            byte[] salt = SecurityUtil.getSalt(); //gerando o salt
            String securePassword = SecurityUtil.getSecurePassword(randomPassword, salt); //gerando a senha em SHA-256 + salt

            user.setSenha(securePassword); //setando a senha
            user.setSalt(Base64.encodeBase64String(salt)); //setando o salt em base64
            User userObj = repo.save(user);

            User safeUser = new User();
            safeUser.setId(userObj.getId());
            safeUser.setNome(userObj.getNome());
            safeUser.setEmail(userObj.getEmail());
            safeUser.setCargo(userObj.getCargo());

            emailService.sendEmail(userObj.getEmail(), "Conta criada", randomPassword, userObj.getCargo(), userObj.getNome());
            return new ResponseEntity<>(new Response(true, "Usuário criado com sucesso", safeUser, HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateUser(UserChangeDTO userChangeDTO) {
        try {
            // Verificar se o usuário com oldEmail existe
            Optional<User> userOptional = repo.findByEmail(userChangeDTO.getOldEmail());

            System.out.print(userChangeDTO.getOldEmail());
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>(new Response(false, "Usuário não encontrado para alteração", null, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }

            // Verificar se o newEmail já está sendo utilizado por outro usuário
            Optional<User> newEmailUserOptional = repo.findByEmail(userChangeDTO.getNewEmail());
            if (newEmailUserOptional.isPresent()) {
                return new ResponseEntity<>(new Response(false, "O novo email já está em uso por outro usuário", null, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
            }

            // Atualizar o email do usuário
            User user = userOptional.get();
            user.setEmail(userChangeDTO.getNewEmail());
            repo.save(user);

            return new ResponseEntity<>(new Response(true, "Email do usuário atualizado com sucesso", null, HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> removeUser(String email) {
        try {
            Optional<User> userOptional = repo.findByEmail(email);
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>(new Response(false, "usuário não encontrado", null, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
            repo.delete(userOptional.get());
            return new ResponseEntity<>(new Response(true, "Usuário removido com sucesso", null, HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
