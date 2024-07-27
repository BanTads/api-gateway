package com.apigateway.auth.auth.repositories;

import com.apigateway.auth.auth.dto.UserDTO;
import com.apigateway.auth.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String login);
}
