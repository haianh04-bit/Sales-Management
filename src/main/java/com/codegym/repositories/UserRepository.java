
package com.codegym.repositories;

import com.codegym.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
            String username,
            String email,
            String phone,
            Pageable pageable
    );

    Object findAll(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_USER'")
    Long countCustomers();
}

