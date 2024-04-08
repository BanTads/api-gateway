package com.apigateway.gerente.gerente.controller;

import com.apigateway.gerente.gerente.dto.GerenteDTO;
import com.apigateway.gerente.gerente.repositories.GerenteRepository;
import com.apigateway.gerente.gerente.services.MessagingService;
import com.apigateway.gerente.gerente.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import com.apigateway.gerente.gerente.model.Gerente;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/gerente")
@Log4j2
@Tag(
        name = "API: Gerente",
        description = "Contém todos os endpoints relacionados ao gerente"
)
public class GerenteController {
    @Autowired
    private GerenteRepository repo;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private MessagingService messagingService;

    @GetMapping("listar")
    @Operation(
            summary = "Endpoint para listagem de gerentes",
            description = "Retorna uma lista com todos os gerentes"
    )
    public ResponseEntity<Object> listAll() {
        try{
            List<Gerente> gerentes = repo.findAll();
            return new ResponseEntity<>(new Response(true, "Lista de gerentes retornada com sucesso", gerentes), HttpStatus.OK);
        }catch (Exception e){
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("adicionar")
    @Operation(
            summary = "Endpoint para adicionar gerente",
            description = "Retorna o último gerente adicionado"
    )
    public ResponseEntity<Object> inserir(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody GerenteDTO gerenteDTO)
    {
        try {
            if (gerenteDTO.getNome() == null || gerenteDTO.getCpf() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do gerente inválidos", null), HttpStatus.BAD_REQUEST);
            }

            if (gerenteDTO.getCpf() == null) {
                return new ResponseEntity<>(new Response(false, "Já existe um gerente cadastrado com o CPF informado.", null), HttpStatus.UNAUTHORIZED);
            }

            //GerenteDTO gerenteObj = gerenteDTO.adicionarCliente(gerenteDTO);
            Gerente gerenteObj = repo.saveAndFlush(mapper.map(gerenteDTO, Gerente.class));

            return new ResponseEntity<>(new Response(true, "Gerente criado com sucesso", gerenteObj), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("alterar")
    @Operation(
            summary = "Endpoint para alterar gerente",
            description = "Retorna dados do gerente alterado"
    )
    public ResponseEntity<Object> alterar(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody GerenteDTO gerenteDTO)
    {
        try {
            if (gerenteDTO.getNome() == null || gerenteDTO.getCpf() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do gerente inválidos", null), HttpStatus.BAD_REQUEST);
            }

            if (gerenteDTO.getCpf() == null) {
                return new ResponseEntity<>(new Response(false, "Já existe um gerente cadastrado com o CPF informado.", null), HttpStatus.UNAUTHORIZED);
            }

            //GerenteDTO gerenteObj = gerenteDTO.alterarGerente(gerenteDTO);
            Gerente gerenteObj = repo.saveAndFlush(mapper.map(gerenteDTO, Gerente.class));

            return new ResponseEntity<>(new Response(true, "Gerente alterado com sucesso", gerenteObj), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("deletar")
    @Operation(
            summary = "Endpoint para deletar gerente",
            description = "Retorna dado nenhum"
    )
    public ResponseEntity<Object> deletar(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody GerenteDTO gerenteDTO)
    {
        try {
            if (gerenteDTO.getNome() == null || gerenteDTO.getCpf() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do gerente inválidos", null), HttpStatus.BAD_REQUEST);
            }

            //gerenteDTO.deletarGerente(gerenteDTO);
            Gerente gerenteObj = repo.saveAndFlush(mapper.map(gerenteDTO, Gerente.class));

            return new ResponseEntity<>(new Response(true, "Gerente deletado com sucesso", null), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.BAD_REQUEST);
        }
    }
}