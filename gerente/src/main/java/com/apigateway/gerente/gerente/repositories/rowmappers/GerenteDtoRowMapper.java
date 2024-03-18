package com.apigateway.gerente.gerente.repositories.rowmappers;

import com.apigateway.gerente.gerente.dto.GerenteDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GerenteDtoRowMapper implements RowMapper<GerenteDTO> {

    @Override
    public GerenteDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ClienteDTO gerenteDTO = new GerenteDTO();
        gerenteDTO.setId(rs.getLong("id"));
        gerenteDTO.setNome(rs.getString("nome"));
        gerenteDTO.setEmail(rs.getString("email"));
        gerenteDTO.setCpf(rs.getString("cpf"));
        gerenteDTO.setTelefone(rs.getString("telefone"));

        return gerenteDTO;
    }
}
