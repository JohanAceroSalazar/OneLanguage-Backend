package com.sena.Backend_OneLanguage.auth.service.imple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sena.Backend_OneLanguage.auth.dto.LoginRequestDto;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@EnabledIfSystemProperty(named = "concurrency.integration", matches = "true")
class AuthServicePostgresConcurrencyTest {

    private static final String EMAIL = "concurrency@example.com";
    private static final String WRONG_PASSWORD = "wrong-password";
    private static final String CORRECT_PASSWORD = "correct-password";

    @Autowired
    private AuthServiceImple authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void resetUser() {
        userRepository.deleteAll();
        userRepository.save(User.builder()
                .idUser(UUID.randomUUID())
                .email(EMAIL)
                .fullName("Concurrency User")
                .passwordHash(passwordEncoder.encode(CORRECT_PASSWORD))
                .failedAttempts(0)
                .userStatus(true)
                .build());
    }

    @Test
    void twoConcurrentFailuresStartingAtThreeLockAtFive() throws Exception {
        User user = currentUser();
        user.setFailedAttempts(3);
        userRepository.save(user);

        runConcurrent(2, WRONG_PASSWORD);

        User result = currentUser();
        assertEquals(5, result.getFailedAttempts());
        assertNotNull(result.getLockedUntil());
    }

    @Test
    void fiveConcurrentFailuresStartingAtZeroLockAtFive() throws Exception {
        runConcurrent(5, WRONG_PASSWORD);

        User result = currentUser();
        assertEquals(5, result.getFailedAttempts());
        assertNotNull(result.getLockedUntil());
    }

    @Test
    void concurrentFailuresDuringLockDoNotChangeState() throws Exception {
        User user = currentUser();
        OffsetDateTime originalLock = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(2);
        user.setFailedAttempts(5);
        user.setLockedUntil(originalLock);
        userRepository.save(user);

        runConcurrent(5, WRONG_PASSWORD);

        User result = currentUser();
        assertEquals(5, result.getFailedAttempts());
        assertTrue(Math.abs(Duration.between(originalLock, result.getLockedUntil()).toNanos()) <= 2_000);
    }

    @Test
    void concurrentFailuresAfterExpiredLockStartANewCycle() throws Exception {
        User user = currentUser();
        user.setFailedAttempts(5);
        user.setLockedUntil(OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(1));
        userRepository.save(user);

        runConcurrent(2, WRONG_PASSWORD);

        User result = currentUser();
        assertEquals(2, result.getFailedAttempts());
        assertEquals(null, result.getLockedUntil());
    }

    @Test
    void correctAndIncorrectConcurrentLoginRemainTransactional() throws Exception {
        List<Throwable> failures = runConcurrentWithDifferentPasswords(CORRECT_PASSWORD, WRONG_PASSWORD);

        User result = currentUser();
        assertTrue(result.getFailedAttempts() == 0 || result.getFailedAttempts() == 1);
        assertEquals(null, result.getLockedUntil());
        assertEquals(1, failures.size());
    }

    private User currentUser() {
        return userRepository.findByEmailAndDeletedAtIsNull(EMAIL).orElseThrow();
    }

    private List<Throwable> runConcurrent(int count, String password) throws Exception {
        List<String> passwords = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            passwords.add(password);
        }
        return runConcurrentWithPasswords(passwords);
    }

    private List<Throwable> runConcurrentWithDifferentPasswords(String first, String second) throws Exception {
        return runConcurrentWithPasswords(List.of(first, second));
    }

    private List<Throwable> runConcurrentWithPasswords(List<String> passwords) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(passwords.size());
        CountDownLatch ready = new CountDownLatch(passwords.size());
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Throwable>> futures = new ArrayList<>();

        try {
            for (String password : passwords) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    assertTrue(start.await(10, TimeUnit.SECONDS));
                    try {
                        LoginRequestDto request = new LoginRequestDto();
                        request.setEmail(EMAIL);
                        request.setPassword(password);
                        authService.login(request);
                        return null;
                    } catch (Throwable throwable) {
                        return throwable;
                    }
                }));
            }

            assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();

            List<Throwable> failures = new ArrayList<>();
            for (Future<Throwable> future : futures) {
                Throwable failure = future.get(30, TimeUnit.SECONDS);
                if (failure != null) {
                    failures.add(failure);
                }
            }
            return failures;
        } finally {
            executor.shutdownNow();
        }
    }
}
