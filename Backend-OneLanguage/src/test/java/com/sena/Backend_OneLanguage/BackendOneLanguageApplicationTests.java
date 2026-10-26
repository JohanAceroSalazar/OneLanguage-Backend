package com.sena.Backend_OneLanguage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootTest
class BackendOneLanguageApplicationTests {

	private static final String TEST_JWT_SECRET = createTestJwtSecret();

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("jwt.secret", () -> TEST_JWT_SECRET);
	}

	@Test
	void contextLoads() {
	}

	private static String createTestJwtSecret() {
		byte[] secretBytes = new byte[48];
		new SecureRandom().nextBytes(secretBytes);
		return Base64.getEncoder().encodeToString(secretBytes);
	}

}
