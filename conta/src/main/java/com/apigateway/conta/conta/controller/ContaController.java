package com.apigateway.conta.conta.controller;

import com.apigateway.conta.conta.dto.ClienteDTO;
import com.apigateway.conta.conta.dto.ContaDTO;
import com.apigateway.conta.conta.model.Conta;
import com.apigateway.conta.conta.repositories.ContaRepository;
import com.apigateway.conta.conta.repositories.MovimentacaoRepository;
import com.apigateway.conta.conta.services.MessagingService;
import com.apigateway.conta.conta.utils.Response;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conta")
@Log4j2
@Tag(
        name = "API: Conta",
        description = "Contém todos os endpoints relacionados a conta"
)
public class ContaController {
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ContaRepository repo;
    @Autowired
    private MovimentacaoRepository repoMovimentacao;
    @Autowired
    private MessagingService messagingService;

    @PutMapping("/atualizar/{numeroConta}")
    @Operation(summary = "Atualiza uma conta pelo id")
    public ResponseEntity<Object> atualizar(@PathVariable Long numeroConta, @RequestBody ContaDTO contaDTO) {
        try {
            Conta contaExistente = repo.findById(numeroConta).orElse(null);
            if (contaExistente == null) {
                return new ResponseEntity<>(new Response(false, "Conta não encontrada", null), HttpStatus.NOT_FOUND);
            }

            if (contaExistente.getNumeroConta() != numeroConta) {
                return new ResponseEntity<>(new Response(false, "ID da conta não pode ser alterado", null), HttpStatus.BAD_REQUEST);
            }

            contaExistente.setLimite(contaDTO.getLimite());
            contaExistente.setAprovada(contaDTO.getAprovada());
            repo.saveAndFlush(contaExistente);
            return new ResponseEntity<>(new Response(true, "Conta atualizada com sucesso", contaExistente), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/saldo/{numeroConta}")
    @Operation(summary = "Saldo de uma conta")
    public ResponseEntity<Object> saldo(@PathVariable Long numeroConta) {
        try {
            Double saldo = repoMovimentacao.calcularSaldo(numeroConta);
            return new ResponseEntity<>(new Response(true, "Saldo listado com sucesso", saldo), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
