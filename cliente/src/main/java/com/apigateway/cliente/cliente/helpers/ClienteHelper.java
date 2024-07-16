package com.apigateway.cliente.cliente.helpers;


import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.cliente.cliente.dto.EnderecoDTO;
import com.apigateway.cliente.cliente.model.Cliente;
import com.apigateway.cliente.cliente.repositories.ClienteRepository;
import com.apigateway.cliente.cliente.utils.Response;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteHelper {
    @Autowired
    private ClienteRepository repo;
    @Autowired
    private ModelMapper mapper;
    public ResponseEntity<Object> saveClient(ClienteDTO clienteDTO) {
        try {
            if (clienteDTO.getNome() == null || clienteDTO.getCpf() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do cliente inválidos", null, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
            }

            EnderecoDTO endereco = clienteDTO.getEndereco();
            if (endereco.getLogradouro() == null || endereco.getNumero() == null || endereco.getCidade() == null || endereco.getUf() == null || endereco.getCep() == null) {
                return new ResponseEntity<>(new Response(false, "Dados do endereço inválidos", null, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
            }
            Cliente clienteObj = repo.saveAndFlush(mapper.map(clienteDTO, Cliente.class));
            return new ResponseEntity<>(new Response(true, "Cliente criado com sucesso", clienteObj,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
