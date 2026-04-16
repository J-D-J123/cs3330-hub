package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
/*  File: TestMonster.java
*	Name: Tucker Potts
*	Desc: This test class tests the getters and takeDamage methods to see if it works properly
*	and if the monster dies.
*
*/
public class TestMonster {
	
	@ParameterizedTest
	@CsvSource({//who is a more iconic monster?
		"Frankenstein, 500, 70, 25"
	})
	void isGettersWorkingForMonster(String name, int health, int damage, int defense) {
		Monster monster = new Monster(name, health, damage, defense, new ArrayList<>());
		
		assertAll(//just checks if getters are working properly
				() -> assertEquals(name, monster.getName()),
				() -> assertEquals(health, monster.getHealth()),
				() -> assertEquals(health, monster.getMaxHealth()),
				() -> assertEquals(damage, monster.getAttack()),
				() -> assertEquals(defense, monster.getDefense())
				);
	}
	
	@Test
	void healthIsReducedForTakeDamage() {//Test to make sure health is properly reduced
		Monster monster = new Monster("Frankenstein", 500, 70, 25, new ArrayList<>());
		
		monster.takeDamage(100);
		assertEquals(400, monster.getHealth());
		
	}
	
	@Test
	void noNegativeDamage() {//test to see if takeDamage might add health or do something wrong
		Monster monster = new Monster("Frankenstein", 500, 70, 25, new ArrayList<>());
		
		monster.takeDamage(-50);
		
		assertEquals(500, monster.getHealth());
	}
	
	@Test
	void makeSureIsAliveWorks() {//tests to see if monsters die when they take the right amount of damage
		Monster monster = new Monster("Frankenstein", 500, 70, 25, new ArrayList<>());
		
		monster.takeDamage(501);
		
		assertFalse(monster.isAlive());
	}
	
	
	
}
