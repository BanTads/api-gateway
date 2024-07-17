package com.apigateway.conta.conta.helpers;

import com.apigateway.conta.conta.dto.ContaDTO;
import com.apigateway.conta.conta.model.Conta;
import com.apigateway.conta.conta.repositories.ContaRepository;
import com.apigateway.conta.conta.utils.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ContaHelper {
    @Autowired
    private ContaRepository repo;
    @Autowired
    private ModelMapper mapper;
    public ResponseEntity<Object> saveAccount(ContaDTO contaDTO) {
        try {
            contaDTO.setAprovada(null);
            contaDTO.setDataCriacao(new Date());
            Conta contaCriada = repo.saveAndFlush(mapper.map(contaDTO, Conta.class));
            return new ResponseEntity<>(new Response(true, "Conta criada com sucesso", contaCriada,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Registro duplicado";
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
