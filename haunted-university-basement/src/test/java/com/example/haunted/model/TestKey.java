package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestKey {
	
	@ParameterizedTest
	@CsvSource({
		"Archive Key, Opens the Exam Archive",
		"Vault Key, Opens the Final Chamber"
		})
	void checkIfValuesNotNull(String name, String description) {
		Key key = new Key(name, description);
		assertAll(
				() -> assertNotNull(key.getName()),
				() -> assertNotNull(key.getDescription())
			);
	}
}
