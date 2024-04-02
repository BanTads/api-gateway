package com.apigateway.gerente.gerente.repositories.rowmappers;

import com.apigateway.gerente.gerente.dto.GerenteDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GerenteDtoRowMapper implements RowMapper<GerenteDTO> {

    @Override
    public GerenteDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        GerenteDTO gerenteDTO = new GerenteDTO();
        GerenteDTO.setId(rs.getLong("id"));
        GerenteDTO.setNome(rs.getString("nome"));
        GerenteDTO.setEmail(rs.getString("email"));
        GerenteDTO.setCpf(rs.getString("cpf"));
        GerenteDTO.setTelefone(rs.getString("telefone"));

        return gerenteDTO;
    }
}
