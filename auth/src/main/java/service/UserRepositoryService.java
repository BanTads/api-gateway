package service;

import dto.UsuarioDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepositoryService extends MongoRepository<UsuarioDTO, String> {
    Optional<UsuarioDTO> findByLogin(String login);
    Boolean existsByLogin(String login);
}
