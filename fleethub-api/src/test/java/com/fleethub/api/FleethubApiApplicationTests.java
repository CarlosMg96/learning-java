package com.fleethub.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Deshabilitado en tests unitarios para no requerir conexión activa a PostgreSQL")
class FleethubApiApplicationTests {

	@Test
	void contextLoads() {
	}

}

