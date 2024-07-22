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

    public ResponseEntity<Object> saveManager(GerenteDTO gerenteDTO) {
        try {
            Gerente gerenteObj = repo.saveAndFlush(mapper.map(gerenteDTO, Gerente.class));
            return new ResponseEntity<>(new Response(true, "Gerente criado com sucesso", gerenteObj,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateManager(GerenteDTO gerenteDTO) {
        try {
            Gerente gerenteExistente = repo.findById(gerenteDTO.getId()).orElse(null);
            if (gerenteExistente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
            gerenteExistente.setNome(gerenteDTO.getNome());
            gerenteExistente.setEmail(gerenteDTO.getEmail());
            gerenteExistente.setTelefone(gerenteDTO.getTelefone());
            if (Integer.compare(gerenteDTO.getQuantidadeContas(), 0) != 0) {
                gerenteExistente.setQuantidadeContas(gerenteDTO.getQuantidadeContas());
            }
            repo.saveAndFlush(gerenteExistente);
            return new ResponseEntity<>(new Response(true, "Gerente atualizado com sucesso", gerenteExistente, HttpStatus.OK.value()), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> verifyAndFindNewManager(GerenteDTO gerenteDTO) {
        try {
            if (!repo.existsById(gerenteDTO.getId())) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }

            Gerente gerenteToRemove = repo.findById(gerenteDTO.getId()).orElse(null);
            if (gerenteToRemove == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }

            // Verificar se há mais de um gerente
            if (repo.count() <= 1) {
                return new ResponseEntity<>(new Response(false, "Não é permitido remover o último gerente do banco.", null, HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
            }

            List<Gerente> gerentes = repo.findByIdNot(gerenteDTO.getId());
            Gerente gerenteComMenosContas = gerentes.stream()
                    .min(Comparator.comparingInt(Gerente::getQuantidadeContas))
                    .orElse(null);

            if (gerenteComMenosContas == null) {
                return new ResponseEntity<>(new Response(false, "Não foi possível encontrar um substituto para as contas.", null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            gerenteComMenosContas.setQuantidadeContas(gerenteComMenosContas.getQuantidadeContas() + gerenteToRemove.getQuantidadeContas());
            return new ResponseEntity<>(new Response(true, "Novo gerente retornado com sucesso", gerenteComMenosContas, HttpStatus.OK.value()), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String mensagemErro = "Um registro com o mesmo CPF ou e-mail já existe.";
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> removeManager(GerenteDTO gerenteDTO){
        try {
            Gerente managerToRemove = repo.findById(gerenteDTO.getId()).orElse(null);
            if (managerToRemove == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
            repo.deleteById(gerenteDTO.getId());
            return new ResponseEntity<>(new Response(true, "Gerente removido com sucesso", managerToRemove,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getManagerMaxAccount() {
        try {
            List<Gerente> gerentes = repo.findAll();
            if (gerentes.isEmpty()) {
                return new ResponseEntity<>(new Response(false, "Nenhum gerente encontrado", null,  HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
            Gerente gerenteComMaisContas = gerentes.stream()
                    .filter(g -> g.getQuantidadeContas() > 1)
                    .max(Comparator.comparingInt(Gerente::getQuantidadeContas))
                    .orElse(null);
            return new ResponseEntity<>(new Response(true, "Gerente encontrado com sucesso", gerenteComMaisContas,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> removeOneFromManager(GerenteDTO gerenteDTO){
        try {
            Gerente gerente = repo.findById(gerenteDTO.getId()).orElse(null);
            if (gerente == null) {
                return new ResponseEntity<>(new Response(false, "Gerente não encontrado", null,  HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
            }
            gerente.setQuantidadeContas(gerente.getQuantidadeContas() - 1);
            repo.save(gerente);
            return new ResponseEntity<>(new Response(true, "Atualização realizada com sucesso", gerente,  HttpStatus.OK.value()), HttpStatus.OK);
        } catch (Exception e) {
            String mensagemErro = e.getMessage();
            return new ResponseEntity<>(new Response(false, mensagemErro, null, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
