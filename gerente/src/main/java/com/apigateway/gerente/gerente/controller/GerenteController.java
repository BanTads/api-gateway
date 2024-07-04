package com.apigateway.gerente.gerente.controller;
import com.apigateway.gerente.gerente.dto.GerenteDTO;
import com.apigateway.gerente.gerente.model.Gerente;
import com.apigateway.gerente.gerente.repositories.GerenteRepository;
import com.apigateway.gerente.gerente.services.MessagingService;
import com.apigateway.gerente.gerente.utils.Response;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

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

    @PostMapping("adicionar")
    @Operation(
            summary = "Endpoint para adicionar gerente",
            description = "Retorna o último gerente adicionado"
    )

    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    public ResponseEntity<Object> inserir(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request ilustrativa") @RequestBody GerenteDTO gerenteDTO)
    {
        System.out.println(gerenteDTO.getNome());
        try {
            if (gerenteDTO.getNome() == null || gerenteDTO.getEmail() == null || gerenteDTO.getCpf() == null || gerenteDTO.getTelefone() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do gerente inválidos", null), HttpStatus.BAD_REQUEST);
            }
            Gerente gerenteObj = repo.saveAndFlush(mapper.map(gerenteDTO, Gerente.class));
            messagingService.sendMessage("manager.created", gerenteObj);
            return new ResponseEntity<>(new Response(true, "Gerente criado com sucesso", gerenteObj), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listar")
    @Operation(summary = "Lista todos os gerentes")
    public ResponseEntity<Object> listarTodos() {
        try {
            List<Gerente> gerentes = repo.findAll();
            return new ResponseEntity<>(new Response(true, "Lista de gerentes recuperada com sucesso", gerentes), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listar/{id}")
    @Operation(summary = "Busca um gerente pelo ID")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        try {
            Gerente gerente = repo.findById(id).orElse(null);
            if (gerente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new Response(true, "Gerente encontrado", gerente), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/atualizar/{id}")
    @ApiResponse(responseCode = "403", description = "CPF ou E-mail duplicado", content = @Content(schema = @Schema(implementation = Response.class)))
    @Operation(summary = "Atualiza um gerente existente pelo ID")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody GerenteDTO gerenteDTO) {
        try {
            Gerente gerenteExistente = repo.findById(id).orElse(null);
            if (gerenteExistente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }

            if (gerenteExistente.getId() != id) {
                return new ResponseEntity<>(new Response(false, "ID do gerente não pode ser alterado", null), HttpStatus.BAD_REQUEST);
            }

            gerenteExistente.setNome(gerenteDTO.getNome());
            gerenteExistente.setCpf(gerenteDTO.getCpf());
            gerenteExistente.setEmail(gerenteDTO.getEmail());
            repo.saveAndFlush(gerenteExistente);
            messagingService.sendMessage("manager.edited", gerenteExistente);
            return new ResponseEntity<>(new Response(true, "Gerente atualizado com sucesso", gerenteExistente), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remover/{id}")
    @Operation(summary = "Remove um gerente pelo ID")
    public ResponseEntity<Object> remover(@PathVariable Long id) {
        try {
            if (!repo.existsById(id)) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }

            Gerente gerenteToRemove = repo.findById(id).orElse(null);
            repo.deleteById(id);
            messagingService.sendMessage("manager.removed", gerenteToRemove.getEmail());
            return new ResponseEntity<>(new Response(true, "Gerente removido com sucesso", null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listar/email/{email}")
    @Operation(summary = "Busca um gerente pelo e-mail")
    public ResponseEntity<Object> buscarPorEmail(@PathVariable String email) {
        try {
            Gerente gerente = repo.findByEmail(email);
            if (gerente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new Response(true, "Gerente encontrado", gerente), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}