package com.apigateway.gerente.gerente.repositories;

import com.apigateway.gerente.gerente.dto.GerenteDTO;
import com.apigateway.gerente.gerente.repositories.rowmappers.GerenteDtoRowMapper;
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
public class GerenteRepository {

    @Autowired
    private DataSource dataSource;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<GerenteDTO> getGerentes(){
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "SELECT * FROM gerente)";

        try 
        {
            return jdbcTemplate.query(sql, new GerenteDtoRowMapper());
        } 
        catch (EmptyResultDataAccessException e) 
        {
            return null;
        }
    }

    public GerenteDTO getGerenteByCPF(String cpf)
    {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "SELECT * FROM gerente where cpf = :cpf";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cpf", cpf);

        try 
        {
            return jdbcTemplate.queryForObject(sql, parametros, new GerenteDtoRowMapper());
        } 
        catch (EmptyResultDataAccessException e) 
        {
            return null;
        }
    }

    public long adicionarGerente(GerenteDTO gerenteDTO) {

        if (existeGerenteComCpf(gerenteDTO.getCpf(), gerenteDTO.getEmail()))
        {
            return null;
        }
        else
        {
            String sql = "INSERT INTO gerente (nome, email, cpf, telefone) VALUES (:nome, :email, :cpf, :telefone)";

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("nome", gerenteDTO.getNome());
            parametros.put("email", gerenteDTO.getEmail());
            parametros.put("cpf", gerenteDTO.getCpf());
            parametros.put("telefone", gerenteDTO.getTelefone());
    
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(sql, new MapSqlParameterSource(parametros), keyHolder, new String[]{"id"});
            return keyHolder.getKey().longValue();
        }
    }

    public GerenteDTO alterarGerente(GerenteDTO gerenteDTO)
    {
        getGerenteByCPF(gerenteDTO.getCpf());
        String sql = "UPDATE gerente SET nome = :nome, email = :email, telefone = :telefone WHERE cpf = :cpf; ";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("nome", gerenteDTO.getNome());
        parametros.put("email", gerenteDTO.getEmail());
        parametros.put("telefone", gerenteDTO.getTelefone());
        parametros.put("cpf", gerenteDTO.getCpf());

        jdbcTemplate.queryForObject(sql, parametros, new GerenteDtoRowMapper());

        return getGerenteByCPF(gerenteDTO.getCpf());
    }

    public void deletarGerente(GerenteDTO gerenteDTO)
    {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "DELETE FROM gerente WHERE cpf = :cpf";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cpf", gerenteDTO.getCpf());

        jdbcTemplate.queryForObject(sql, parametros, new GerenteDtoRowMapper());

        return;
    }

    public boolean existeGerenteComCpf(String cpf) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "SELECT COUNT(*) FROM gerente WHERE cpf = :cpf";

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("cpf", cpf);

        int count = jdbcTemplate.queryForObject(sql, parametros, Integer.class);

        return count > 0;
    }
}
