
package com.codegym.repositories;

import com.codegym.models.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
