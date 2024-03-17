package com.apigateway.cliente.cliente.repositories.rowmappers;

import com.apigateway.cliente.cliente.dto.ClienteDTO;
import com.apigateway.cliente.cliente.dto.EnderecoDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDtoRowMapper implements RowMapper<ClienteDTO> {

    @Override
    public ClienteDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(rs.getLong("id"));
        clienteDTO.setNome(rs.getString("nome"));
        clienteDTO.setEmail(rs.getString("email"));
        clienteDTO.setCpf(rs.getString("cpf"));
        clienteDTO.setTelefone(rs.getString("telefone"));
        clienteDTO.setSalario(rs.getFloat("salario"));

        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setId(rs.getLong("id_endereco"));
        enderecoDTO.setLogradouro(rs.getString("logradouro"));
        enderecoDTO.setNumero(rs.getString("numero"));
        enderecoDTO.setCidade(rs.getString("cidade"));
        enderecoDTO.setUf(rs.getString("uf"));
        enderecoDTO.setCep(rs.getString("cep"));

        clienteDTO.setEndereco(enderecoDTO);

        return clienteDTO;
    }
}
