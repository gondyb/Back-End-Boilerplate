package fr.gondyb.backendtemplate.user.query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserViewRepository extends JpaRepository<UserView, Long> {
    Optional<UserView> findByEmail(String email);

    Optional<UserView> findById(UUID uuid);

    boolean existsByEmail(String email);
}