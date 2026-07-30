package com.sena.Backend_OneLanguage.passwordreset.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sena.Backend_OneLanguage.passwordreset.entity.PasswordResetToken;
import java.util.List;
import com.sena.Backend_OneLanguage.users.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByTokenIdentifier(UUID tokenIdentifier);

    List<PasswordResetToken> findAllByUser(User user);

    void deleteAllByUser(User user);

}