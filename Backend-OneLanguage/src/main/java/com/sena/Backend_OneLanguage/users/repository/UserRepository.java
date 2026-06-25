//Acceso a la base de datos
package com.sena.Backend_OneLanguage.users.repository;

import com.sena.Backend_OneLanguage.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Optional<User> findByIdUserAndDeletedAtIsNull(UUID idUser);

    List<User> findAllByDeletedAtIsNull();

    boolean existsByEmailAndDeletedAtIsNull(String email);
}
