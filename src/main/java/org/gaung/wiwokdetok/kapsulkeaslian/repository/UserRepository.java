package org.gaung.wiwokdetok.kapsulkeaslian.repository;

import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String username);
    Page<User> findAll(Pageable pageable);
}
