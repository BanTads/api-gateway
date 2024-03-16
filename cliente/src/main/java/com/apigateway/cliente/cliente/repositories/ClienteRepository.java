package com.apigateway.cliente.cliente.repositories;

import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.cliente.cliente.repositories.rowmappers.ClienteDtoRowMapper;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Observed
public class ClienteRepository {

    @Autowired
    private DataSource dataSource;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<ClienteDTO> getAllClients(){
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = String.format("""
            SELECT * FROM clientes;
        """);

        try {
            return jdbcTemplate.query(sql, new ClienteDtoRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public long adicionarCliente(ClienteDTO clienteDTO) {
        String sql = "INSERT INTO clientes (nome, email, cpf, telefone, salario, id_endereco) VALUES (:nome, :email, :cpf, :telefone, :salario, :id_endereco)";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("nome", clienteDTO.getNome());
        parametros.put("email", clienteDTO.getEmail());
        parametros.put("cpf", clienteDTO.getCpf());
        parametros.put("telefone", clienteDTO.getTelefone());
        parametros.put("salario", clienteDTO.getSalario());
        parametros.put("id_endereco", clienteDTO.getEndereco().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(parametros), keyHolder, new String[]{"id"});
        return keyHolder.getKey().longValue();
    }

    public boolean existeClienteComCpf(String cpf, String email) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "SELECT COUNT(*) FROM clientes WHERE cpf = :cpf OR email = :email";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cpf", cpf);
        parametros.put("email", email);

        int count = jdbcTemplate.queryForObject(sql, parametros, Integer.class);

        return count > 0;
    }
}
