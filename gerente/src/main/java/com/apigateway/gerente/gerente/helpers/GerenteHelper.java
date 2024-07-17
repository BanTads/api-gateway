package com.apigateway.gerente.gerente.helpers;

import com.apigateway.gerente.gerente.dto.GerenteDTO;
import com.apigateway.gerente.gerente.model.Gerente;
import com.apigateway.gerente.gerente.repositories.GerenteRepository;
import com.apigateway.gerente.gerente.utils.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class GerenteHelper {
    @Autowired
    private GerenteRepository repo;
    @Autowired
    private ModelMapper mapper;
    public ResponseEntity<Object> getManagerMinAccount() {
        try {
            List<Gerente> gerentes = repo.findAll();
            if (gerentes.isEmpty()) {
                return new ResponseEntity<>(new Response(false, "Nenhum gerente encontrado", null,  HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }

            Gerente gerenteComMenosContas = gerentes.stream()
                    .min(Comparator.comparingInt(Gerente::getQuantidadeContas))
                    .orElse(null);

            return new ResponseEntity<>(new Response(true, "Gerente encontrado com sucesso", gerenteComMenosContas,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> addOneClienteToGerente(GerenteDTO gerenteDTO){
        try {
            Gerente gerente = repo.findById(gerenteDTO.getId()).orElse(null);
            if (gerente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null,  HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
            gerente.setQuantidadeContas(gerente.getQuantidadeContas() + 1);
            repo.save(gerente);
            return new ResponseEntity<>(new Response(true, "Atualização realizada com sucesso", gerente,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
