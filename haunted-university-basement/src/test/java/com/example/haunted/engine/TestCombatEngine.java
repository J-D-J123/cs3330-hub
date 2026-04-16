/**
* File: 		TestCombatEngine.java
* Author:	Joey Johnson
* Date:		04/15/2026
* Desc:		to test the CombatEngine.java file
*
* Functions to test:
*  		attack() with null monster, dead player, dead monster,
*  		attack that survives, and attack that defeats the monster
*
* 		In this example the Monster is the Ghost and I (Joey) am the player
*/

// package b/c why not
package com.example.haunted.engine;

// random ass packages for junit requiremtns for testing
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

// required classes in order to test CombatEngine
import com.example.haunted.events.CombatResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Item;
import com.example.haunted.model.Key;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;

public class TestCombatEngine {

    // makes the CombatEngine with the constusturtor as well as the new DamageCalculator and QuestTracker()
    private CombatEngine combatEngine = new CombatEngine(new DamageCalculator(), new QuestTracker());

    // since it is all custom objects i used a Stream
    // https://www.arhohuttunen.com/junit-5-parameterized-tests/
    static Stream<Arguments> combatScenarios() {

        // dead player takes damage to kill the player me :( i need sleep please make me go to bed
        Player deadPlayer = new Player("Dead Joey", 1, 5, 5, new Inventory(10));
        deadPlayer.takeDamage(100);

        // dead monster health to 0
        Monster deadMonster = new Monster("Dead Ghost", 1, 3, 1, List.of());
        deadMonster.takeDamage(100);

        return Stream.of(
            // null monster
            Arguments.of(new Player("Joey", 20, 5, 5, new Inventory(10)), null,
                false, "Monster not found.", 0, 0, false),

            // dead player oh no i died :(
            Arguments.of(deadPlayer, new Monster("Ghost", 100, 3, 1, List.of()),
                false, "Player is defeated.", 0, 0, false),

            // monster is already dead
            Arguments.of(new Player("Joey", 20, 5, 5, new Inventory(10)), deadMonster,
                false, "Monster is already defeated.", 0, 0, true),

            // Joey attacks monster and the monster survivies
            Arguments.of(new Player("Joey", 20, 5, 5, new Inventory(10)), new Monster("Ghost", 100, 3, 1, List.of()),
                true, "Attacked Ghost.", 4, 1, false),

            // Joey attacks and kills the monster
            Arguments.of(new Player("Joey", 20, 5, 5, new Inventory(10)), new Monster("Ghost", 1, 3, 1, List.of()),
                true, "Defeated Ghost.", 4, 0, true)
        );
    }

    /**
     * testAttack() uses parameterized test cases from the combatScenarios Stream
     * 	and uses them as inputs to the testAttack method to check
     * 	the results for each combat scenario
     *
     * @param player the Player object
     * @param monster the Monster object
     * @param expectedSuccess checks whether the attack call succeeds
     * @param expectedMessage the expected message after the Attack
     * @param expectedDmgToMonster the expected damage to the monster
     * @param expectedDmgToPlayer the expected damage amount to the player
     * @param expectedMonsterDefeated the boolean value if the monster should be defeated the monster should be defeated
     */
    @ParameterizedTest
    @MethodSource("combatScenarios")
    void testAttack(Player player, Monster monster, boolean expectedSuccess, String expectedMessage,
    		int expectedDmgToMonster, int expectedDmgToPlayer, boolean expectedMonsterDefeated) {

    		// Player player, Quest quest, Monster monster
        CombatResult result = combatEngine.attack(player, null, monster);

        // check the field variables, check success, message, damage to player and monster, and if the monster died
        assertEquals(expectedSuccess, result.isSuccess());
        assertEquals(expectedMessage, result.getMessage());
        assertEquals(expectedDmgToMonster, result.getDamageToMonster());
        assertEquals(expectedDmgToPlayer, result.getDamageToPlayer());
        assertEquals(expectedMonsterDefeated, result.isMonsterDefeated());
    }

    // test that loot from a defeated monster actually ends up in the dropped items list
    /**
     * the testGetDroppedItems() method makes a new Player named joey and a new Monster named ghost 
     * 	and checks to see after joey kills the ghost if joey gets the room1Key object that the ghost had
     */
    @Test
    void testGetDroppedItems() {
    	
    		// make new player (me)
    		Player joey = new Player("Joey", 20, 5, 5, new Inventory(10)); 
    		
    		// assign joey to a new room "Room1"
    		joey.setCurrentRoom(new Room("room1", "Hallway", "A Very Dark Hallway"));

    		// make a ghost Monster and make it hold a key
        Key room1Key = new Key("Key", "Room1  Key");
        Monster ghost = new Monster("Ghost", 1, 3, 1, List.of(room1Key));

        // joey attacks the ghost => ghost dies 
        CombatResult result = combatEngine.attack(joey, null, ghost);

        // ghost is dead and Player joey takes the ghost items which is the room1Key
        assertTrue(result.isMonsterDefeated());
        assertEquals(1, result.getDroppedItems().size());
        assertEquals("Key", result.getDroppedItems().get(0).getName());
    }
}
