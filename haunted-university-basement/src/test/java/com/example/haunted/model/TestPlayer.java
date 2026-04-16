package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/*  File: TestPlayer.java
*	Name: Tucker Potts
*	Desc: This test class also tests the getters as well as the isAlive method.
*
*/
public class TestPlayer {
	@ParameterizedTest
	@CsvSource({
		"Tucker, 100, 20, 10"
	})
	void isBasicGettersWorkingForPlayer(String name, int health, int damage, int defense) {
		Inventory inventory = new Inventory(50);
		Player player = new Player(name, health, damage, defense, inventory);
		
		assertAll(
				() -> assertEquals(name, player.getName()),
				() -> assertEquals(health, player.getHealth()),
				() -> assertEquals(health, player.getMaxHealth()),
				() -> assertEquals(damage, player.getBaseAttack()),
				() -> assertEquals(defense, player.getBaseDefense()),
				() -> assertEquals(inventory, player.getInventory())
				);
	}
	
	@ParameterizedTest
	@CsvSource({
		"Tucker, 100, 20, 10"
	})
	void makeSureIsAliveWorks(String name, int health, int damage, int defense) {
		Inventory inventory = new Inventory(50);
		Player player = new Player(name, health, damage, defense, inventory);
		
		player.takeDamage(101);
		
		assertFalse(player.isAlive());
	}
	
}
