package com.example.haunted.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.haunted.config.DungeonFactory;
import com.example.haunted.engine.GameEngine;
import com.example.haunted.events.CombatResult;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;

/**
 * File:    TestGameIntegration.java
 * Author:  Jack Belleville
 * Desc:    Integration tests that chain the movement, interaction,
 *          and combat engines together through GameEngine. Covers the full
 *          happy path to game victory (pick up key, unlock room, defeat
 *          boss, recover gradebook) plus focused flows like locked room
 *          denial, trap triggering, and losing the game.
 */
public class TestGameIntegration {

    private GameEngine engine;

    @BeforeEach
    void setUp() {
        engine = DungeonFactory.createGame();
    }

    @Test
    void cannotMoveIntoLockedRoomWithoutKey() {
        engine.move(Direction.EAST); // stairwell -> lectureHall
        MoveResult result = engine.move(Direction.NORTH); // examArchive is locked
        assertFalse(result.isSuccess());
        assertEquals("The room is locked.", result.getMessage());
        assertEquals("lectureHall", engine.getCurrentRoom().getId());
    }

    @Test
    void cannotMoveInDirectionWithNoExit() {
        MoveResult result = engine.move(Direction.NORTH); // stairwell has no north exit
        assertFalse(result.isSuccess());
        assertEquals("There is no room in that direction.", result.getMessage());
        assertEquals("stairwell", engine.getCurrentRoom().getId());
    }

    @Test
    void unlockFailsWithoutRequiredKey() {
        engine.move(Direction.EAST);
        InteractionResult result = engine.unlockRoom(Direction.NORTH);
        assertFalse(result.isSuccess());
        assertEquals("You do not have the correct key.", result.getMessage());
    }

    @Test
    void unlockFailsForNonExistentExit() {
        InteractionResult result = engine.unlockRoom(Direction.SOUTH);
        assertFalse(result.isSuccess());
        assertEquals("There is no room in that direction.", result.getMessage());
    }

    @Test
    void unlockAlreadyUnlockedRoomSucceedsWithMessage() {
        InteractionResult result = engine.unlockRoom(Direction.EAST);
        assertTrue(result.isSuccess());
        assertEquals("The room is already unlocked.", result.getMessage());
    }

    @Test
    void pickUpKeyThenUnlockExamArchive() {
        engine.move(Direction.EAST);  // lectureHall
        engine.move(Direction.EAST);  // labStorage
        InteractionResult pick = engine.pickUpItem("Archive Key");
        assertTrue(pick.isSuccess());
        assertEquals("Picked up Archive Key.", pick.getMessage());

        engine.move(Direction.WEST);  // back to lectureHall
        InteractionResult unlock = engine.unlockRoom(Direction.NORTH);
        assertTrue(unlock.isSuccess());
        assertEquals("Unlocked Exam Archive.", unlock.getMessage());

        MoveResult move = engine.move(Direction.NORTH);
        assertTrue(move.isSuccess());
        assertEquals("examArchive", engine.getCurrentRoom().getId());
    }

    @Test
    void elevatorTrapTriggersOnFirstEntryAndDisarmsAfter() {
        engine.move(Direction.EAST); // lectureHall
        int hpBefore = engine.getPlayer().getHealth();

        MoveResult enter = engine.move(Direction.SOUTH); // brokenElevator
        assertTrue(enter.isSuccess());
        assertTrue(enter.isTrapTriggered());
        assertEquals(8, enter.getTrapDamage());
        assertEquals(hpBefore - 8, engine.getPlayer().getHealth());

        // Leave and re-enter: trap is one-time, must not trigger again.
        engine.move(Direction.NORTH);
        int hpBeforeSecond = engine.getPlayer().getHealth();
        MoveResult reenter = engine.move(Direction.SOUTH);
        assertTrue(reenter.isSuccess());
        assertFalse(reenter.isTrapTriggered());
        assertEquals(0, reenter.getTrapDamage());
        assertEquals(hpBeforeSecond, engine.getPlayer().getHealth());
    }

    @Test
    void cannotPickUpItemThatIsNotInRoom() {
        InteractionResult result = engine.pickUpItem("Nonexistent Item");
        assertFalse(result.isSuccess());
        assertEquals("Item not found in the room.", result.getMessage());
    }

    @Test
    void attackingNonexistentMonsterReturnsFailure() {
        CombatResult result = engine.attack("Ghost That Isn't Here");
        assertFalse(result.isSuccess());
        assertEquals("Monster not found.", result.getMessage());
    }

    @Test
    void usingItemNotInInventoryFails() {
        InteractionResult result = engine.useItem("Coffee Potion");
        assertFalse(result.isSuccess());
        assertEquals("Item not found in inventory.", result.getMessage());
    }

    @Test
    void equippingNonEquippableItemFails() {
        engine.move(Direction.EAST); // lectureHall
        engine.pickUpItem("Coffee Potion");
        InteractionResult result = engine.equipItem("Coffee Potion");
        assertFalse(result.isSuccess());
        assertEquals("That item cannot be equipped.", result.getMessage());
    }

    @Test
    void usingEquipmentItemFails() {
        engine.move(Direction.EAST);
        engine.move(Direction.EAST); // labStorage
        engine.pickUpItem("Calculator Shield");
        InteractionResult result = engine.useItem("Calculator Shield");
        assertFalse(result.isSuccess());
        assertEquals("That item cannot be used.", result.getMessage());
    }

    @Test
    void potionHealsPlayer() {
        engine.move(Direction.EAST); // lectureHall
        engine.pickUpItem("Coffee Potion");
        // Damage the player enough that the +15 heal doesn't hit the maxHealth cap.
        engine.getPlayer().takeDamage(30); // 50 -> 20
        int hpBefore = engine.getPlayer().getHealth();
        assertEquals(20, hpBefore);

        InteractionResult use = engine.useItem("Coffee Potion");
        assertTrue(use.isSuccess());
        assertEquals("Used Coffee Potion.", use.getMessage());
        assertEquals(hpBefore + 15, engine.getPlayer().getHealth());
        // Potion consumed.
        assertFalse(engine.getPlayer().getInventory().contains("Coffee Potion"));
    }

    @Test
    void combatDropsLootIntoRoomAndReturnsIt() {
        engine.move(Direction.EAST); // lectureHall, has Sleep-Deprived TA
        attackUntilDefeated("Sleep-Deprived TA");
        // TA's loot is a Coffee Potion (two Coffee Potions should now be in the room.)
        long count = engine.getCurrentRoom().getItems().stream()
                .filter(i -> i.getName().equals("Coffee Potion"))
                .count();
        assertEquals(2, count);
    }

    @Test
    void fullHappyPathWinsGame() {
        // stairwell -> lectureHall
        assertTrue(engine.move(Direction.EAST).isSuccess());
        assertTrue(engine.pickUpItem("Coffee Potion").isSuccess());
        attackUntilDefeated("Sleep-Deprived TA");

        // lectureHall -> labStorage
        assertTrue(engine.move(Direction.EAST).isSuccess());
        assertTrue(engine.pickUpItem("Archive Key").isSuccess());

        // labStorage -> serverCloset
        assertTrue(engine.move(Direction.NORTH).isSuccess());
        assertTrue(engine.pickUpItem("Stapler of Justice").isSuccess());
        assertTrue(engine.equipItem("Stapler of Justice").isSuccess());
        attackUntilDefeated("Spreadsheet Golem");
        assertTrue(engine.pickUpItem("Dry Erase Sword").isSuccess());
        assertTrue(engine.equipItem("Dry Erase Sword").isSuccess());

        // serverCloset -> labStorage -> lectureHall
        assertTrue(engine.move(Direction.SOUTH).isSuccess());
        assertTrue(engine.move(Direction.WEST).isSuccess());

        // Unlock and enter examArchive
        assertTrue(engine.unlockRoom(Direction.NORTH).isSuccess());
        assertTrue(engine.move(Direction.NORTH).isSuccess());
        attackUntilDefeated("Plagiarism Ghost");
        assertTrue(engine.pickUpItem("Lost Gradebook").isSuccess());
        assertTrue(engine.getQuest().isGradebookRecovered());
        assertFalse(engine.getQuest().isComplete());

        // examArchive -> deanVault
        assertTrue(engine.move(Direction.EAST).isSuccess());
        attackUntilDefeated("Registrar Wraith");
        assertTrue(engine.pickUpItem("Graduation Gown Armor").isSuccess());
        assertTrue(engine.equipItem("Graduation Gown Armor").isSuccess());
        assertTrue(engine.pickUpItem("Vault Key").isSuccess());

        // Heal up before the boss.
        if (engine.getPlayer().getHealth() < engine.getPlayer().getMaxHealth()) {
            engine.useItem("Coffee Potion");
        }

        // Unlock and enter finalChamber; defeat boss.
        assertTrue(engine.unlockRoom(Direction.NORTH).isSuccess());
        assertTrue(engine.move(Direction.NORTH).isSuccess());
        attackUntilDefeated("Final Exam Phantom");

        assertTrue(engine.getQuest().isPhantomDefeated());
        assertTrue(engine.getQuest().isComplete());
        assertTrue(engine.isGameWon());
        assertFalse(engine.isGameOver());
    }

    @Test
    void playerLosesWhenHealthReachesZero() {
        // Drive the player to 0 HP directly so the test doesn't depend on combat
        // balance. Then verify isGameOver reflects it and that attacking
        // returns "Player is defeated.".
        engine.move(Direction.EAST); // lectureHall (has a living monster)
        engine.getPlayer().takeDamage(9999);
        assertTrue(engine.isGameOver());
        assertFalse(engine.isGameWon());
        assertEquals(0, engine.getPlayer().getHealth());

        CombatResult result = engine.attack("Sleep-Deprived TA");
        assertFalse(result.isSuccess());
        assertEquals("Player is defeated.", result.getMessage());
    }

    @Test
    void attackingAlreadyDefeatedMonsterReturnsAppropriateMessage() {
        engine.move(Direction.EAST);
        attackUntilDefeated("Sleep-Deprived TA");
        CombatResult result = engine.attack("Sleep-Deprived TA");
        assertFalse(result.isSuccess());
        assertEquals("Monster is already defeated.", result.getMessage());
        assertTrue(result.isMonsterDefeated());
    }

    @Test
    void itemLookupAndUnlockAreCaseInsensitive() {
        engine.move(Direction.EAST);
        engine.move(Direction.EAST);
        assertTrue(engine.pickUpItem("archive key").isSuccess());
        engine.move(Direction.WEST);
        // Room.unlock matches key name case-insensitively.
        assertTrue(engine.unlockRoom(Direction.NORTH).isSuccess());
    }

    private void attackUntilDefeated(String monsterName) {
        int safety = 50;
        while (engine.getCurrentRoom().findMonster(monsterName).orElseThrow().isAlive()
                && !engine.isGameOver()
                && safety-- > 0) {
            engine.attack(monsterName);
        }
        assertFalse(engine.getCurrentRoom().findMonster(monsterName).orElseThrow().isAlive(),
                "Failed to defeat " + monsterName);
    }
}
