package com.sena.Backend_OneLanguage.config;

import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnectionVerifier implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        long userCount = userRepository.count();
        log.info("Conexion a la base de datos OK. Total de usuarios registrados: {}", userCount);
    }
}
