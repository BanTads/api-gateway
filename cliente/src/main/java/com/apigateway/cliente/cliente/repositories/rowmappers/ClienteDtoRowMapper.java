package com.apigateway.cliente.cliente.repositories.rowmappers;

import com.apigateway.cliente.cliente.dto.ClienteDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDtoRowMapper implements RowMapper<ClienteDTO> {

    @Override
    public ClienteDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ClienteDTO clienteDto = new ClienteDTO();
        clienteDto.setId(rs.getLong("id"));
        clienteDto.setNome(rs.getString("nome"));
        clienteDto.setEmail(rs.getString("email"));
        clienteDto.setCpf(rs.getString("cpf"));
        clienteDto.setTelefone(rs.getString("telefone"));
        clienteDto.setSalario(rs.getFloat("salario"));
        return clienteDto;
    }
}
