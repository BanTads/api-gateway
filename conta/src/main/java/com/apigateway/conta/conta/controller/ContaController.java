package com.apigateway.conta.conta.controller;

import com.apigateway.conta.conta.dto.ClienteDTO;
import com.apigateway.conta.conta.dto.ContaDTO;
import com.apigateway.conta.conta.model.Conta;
import com.apigateway.conta.conta.repositories.ContaRepository;
import com.apigateway.conta.conta.services.MessagingService;
import com.apigateway.conta.conta.utils.Response;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private MessagingService messagingService;

//    @GetMapping("/listar")
//    @Operation(summary = "Lista todas as contas")
//    public ResponseEntity<Object> listarTodos() {
//        try {
//            List<Conta> contas = repo.findAll();
//            List<ContaDTO> contasComClientes = contas.stream().map(conta -> {
//                String jsonCliente = (String) messagingService.sendAndReceiveMessage("client.get.info", conta.getIdCliente());
//
//                Gson gson = new Gson();
//                ClienteDTO cliente = gson.fromJson(jsonCliente, ClienteDTO.class);
//
//                ContaDTO contaDTO = new ContaDTO();
//                contaDTO.setNumero_conta(conta.getNumeroConta());
//                contaDTO.setAprovada(conta.getAprovada());
//                contaDTO.setDataCriacao(conta.getDataCriacao());
//                contaDTO.setLimite(conta.getLimite());
//                contaDTO.setIdGerente(conta.getIdGerente());
//                contaDTO.setCliente(cliente);
//                return contaDTO;
//            }).sorted(Comparator.comparing(c -> c.getCliente().getNome())).collect(Collectors.toList());
//
//            return new ResponseEntity<>(new Response(true, "Lista de contas recuperada com sucesso", contasComClientes), HttpStatus.OK);
//        } catch (Exception e) {
//            String mensagemErro = e.getMessage();
//            return new ResponseEntity<>(new Response(false, mensagemErro, null), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
