package com.apigateway.cliente.cliente.repositories;

import com.apigateway.cliente.cliente.dto.EnderecoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class EnderecoRepository {

    @Autowired
    private DataSource dataSource;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public EnderecoRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long adicionarEndereco(EnderecoDTO enderecoDTO) {
        String sql = "INSERT INTO enderecos (tipo, logradouro, numero, complemento, cep, cidade, uf) " +
                "VALUES (:tipo, :logradouro, :numero, :complemento, :cep, :cidade, :uf)";


        SqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("tipo", enderecoDTO.getTipo())
                .addValue("logradouro", enderecoDTO.getLogradouro())
                .addValue("numero", enderecoDTO.getNumero())
                .addValue("complemento", enderecoDTO.getComplemento())
                .addValue("cep", enderecoDTO.getCep())
                .addValue("cidade", enderecoDTO.getCidade())
                .addValue("uf", enderecoDTO.getUf());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, parametros, keyHolder);
        return keyHolder.getKey().longValue();
    }

}
