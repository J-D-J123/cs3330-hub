package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestMonster {
	
	@ParameterizedTest
	@CsvSource({
		"Frankenstein, 500, 70, 25"
	})
	void isGettersWorkingForMonster(String name, int health, int damage, int defense) {
		Monster monster = new Monster(name, health, damage, defense, new ArrayList<>());
		
		assertAll(
				() -> assertEquals(name, monster.getName()),
				() -> assertEquals(health, monster.getHealth()),
				() -> assertEquals(health, monster.getMaxHealth()),
				() -> assertEquals(damage, monster.getAttack()),
				() -> assertEquals(defense, monster.getDefense())
				);
	}
	
	@Test
	void healthIsReducedForTakeDamage() {
		Monster monster = new Monster("Frankenstein", 500, 70, 25, new ArrayList<>());
		
		monster.takeDamage(100);
		assertEquals(400, monster.getHealth());
		
	}
	
	@Test
	void noNegativeDamage() {
		Monster monster = new Monster("Frankenstein", 500, 70, 25, new ArrayList<>());
		
		monster.takeDamage(-50);
		
		assertEquals(500, monster.getHealth());
	}
	
	@Test
	void makeSureIsAliveWorks() {
		Monster monster = new Monster("Frankenstein", 500, 70, 25, new ArrayList<>());
		
		monster.takeDamage(501);
		
		assertFalse(monster.isAlive());
	}
	
	
	
}
