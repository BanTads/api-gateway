package com.apigateway.cliente.cliente.helpers;


import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.cliente.cliente.dto.EnderecoDTO;
import com.apigateway.cliente.cliente.model.Cliente;
import com.apigateway.cliente.cliente.model.Endereco;
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
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateClient(ClienteDTO clienteDTO) {
        try {
            System.out.println(clienteDTO.getId());
            // Verifica se o cliente existe
            Cliente clienteExistente = repo.findById(clienteDTO.getId()).orElse(null);

            if (clienteExistente == null) {
                return new ResponseEntity<>(new Response(false, "Cliente não encontrado", null, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }

            // Atualiza os campos permitidos
            if (clienteDTO.getNome() != null) {
                clienteExistente.setNome(clienteDTO.getNome());
            }

            if (clienteDTO.getEmail() != null) {
                clienteExistente.setEmail(clienteDTO.getEmail());
            }

            if (clienteDTO.getTelefone() != null) {
                clienteExistente.setTelefone(clienteDTO.getTelefone());
            }

            if (Float.compare(clienteDTO.getSalario(), 0) != 0) {
                clienteExistente.setSalario(clienteDTO.getSalario());
            }

            if (clienteDTO.getEndereco() != null) {
                EnderecoDTO enderecoDTO = clienteDTO.getEndereco();
                Endereco enderecoExistente = clienteExistente.getEndereco();

                if (enderecoDTO.getLogradouro() != null) {
                    enderecoExistente.setLogradouro(enderecoDTO.getLogradouro());
                }
                if (enderecoDTO.getNumero() != null) {
                    enderecoExistente.setNumero(enderecoDTO.getNumero());
                }
                if (enderecoDTO.getComplemento() != null) {
                    enderecoExistente.setComplemento(enderecoDTO.getComplemento());
                }
                if (enderecoDTO.getCep() != null) {
                    enderecoExistente.setCep(enderecoDTO.getCep());
                }
                if (enderecoDTO.getCidade() != null) {
                    enderecoExistente.setCidade(enderecoDTO.getCidade());
                }
                if (enderecoDTO.getUf() != null) {
                    enderecoExistente.setUf(enderecoDTO.getUf());
                }
                clienteExistente.setEndereco(enderecoExistente);
            }

            Cliente clienteAtualizado = repo.saveAndFlush(clienteExistente);
            return new ResponseEntity<>(new Response(true, "Cliente atualizado com sucesso", clienteAtualizado, HttpStatus.OK.value()), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> deleteCliente(ClienteDTO clienteDTO){
        try {
            Cliente clienteToRemove = repo.findById(clienteDTO.getId()).orElse(null);
            if (clienteToRemove == null) {
                return new ResponseEntity<>(new Response(false, "Cliente não encontrado", null, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
            repo.deleteById(clienteDTO.getId());
            return new ResponseEntity<>(new Response(true, "Cliente removido com sucesso", null,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
