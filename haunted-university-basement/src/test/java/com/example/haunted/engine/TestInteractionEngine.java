package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Armor;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Key;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.QuestTracker;

/**
 * File:    TestInteractionEngine.java
 * Author:  Jack Belleville
 * Desc:    Mutation analysis tests for InteractionEngine. Covers every branch
 *          of pickUpItem, useItem, equipItem, and unlockRoom including
 *          full inventory handling, non-equippable/non-usable items, missing
 *          keys, already unlocked rooms, and rooms with no required key.
 */
public class TestInteractionEngine {

    private InteractionEngine engine;
    private Player player;
    private Room room;
    private Room east;
    private Quest quest;

    @BeforeEach
    void setUp() {
        engine = new InteractionEngine(new QuestTracker());
        player = new Player("P", 50, 5, 2, new Inventory(3));
        room = new Room("room", "Room", "d");
        east = new Room("east", "East", "d");
        room.connect(Direction.EAST, east);
        player.setCurrentRoom(room);
        quest = new Quest("Q", "d");
    }

    @Test
    void pickUpMissingItemFails() {
        InteractionResult r = engine.pickUpItem(player, quest, "nothing");
        assertFalse(r.isSuccess());
        assertEquals("Item not found in the room.", r.getMessage());
    }

    @Test
    void pickUpSuccessAddsToInventoryAndRemovesFromRoom() {
        room.addItem(new Key("K", "d"));
        InteractionResult r = engine.pickUpItem(player, quest, "k");
        assertTrue(r.isSuccess());
        assertEquals("Picked up K.", r.getMessage());
        assertTrue(player.getInventory().contains("K"));
        assertTrue(room.findItem("K").isEmpty());
    }

    @Test
    void pickUpWhenInventoryFullPutsItemBack() {
        player = new Player("P", 50, 5, 2, new Inventory(1));
        player.setCurrentRoom(room);
        player.getInventory().addItem(new Key("Existing", "d"));
        room.addItem(new Key("NewKey", "d"));
        InteractionResult r = engine.pickUpItem(player, quest, "NewKey");
        assertFalse(r.isSuccess());
        assertEquals("Inventory is full.", r.getMessage());
        // item went back to room
        assertTrue(room.findItem("NewKey").isPresent());
    }

    @Test
    void pickUpGradebookUpdatesQuest() {
        room.addItem(new QuestItem("Lost Gradebook", "d"));
        InteractionResult r = engine.pickUpItem(player, quest, "Lost Gradebook");
        assertTrue(r.isSuccess());
        assertTrue(quest.isGradebookRecovered());
    }

    @Test
    void useItemNotInInventoryFails() {
        InteractionResult r = engine.useItem(player, "potion");
        assertFalse(r.isSuccess());
        assertEquals("Item not found in inventory.", r.getMessage());
    }

    @Test
    void useNonPotionItemFails() {
        player.getInventory().addItem(new Key("K", "d"));
        InteractionResult r = engine.useItem(player, "K");
        assertFalse(r.isSuccess());
        assertEquals("That item cannot be used.", r.getMessage());
    }

    @Test
    void usePotionHealsAndConsumes() {
        player.getInventory().addItem(new Potion("P", "d", 10));
        player.takeDamage(20);
        InteractionResult r = engine.useItem(player, "p");
        assertTrue(r.isSuccess());
        assertEquals("Used P.", r.getMessage());
        assertEquals(40, player.getHealth());
        assertFalse(player.getInventory().contains("P"));
    }

    @Test
    void equipItemNotInInventoryFails() {
        InteractionResult r = engine.equipItem(player, "sword");
        assertFalse(r.isSuccess());
        assertEquals("Item not found in inventory.", r.getMessage());
    }

    @Test
    void equipWeaponSucceeds() {
        player.getInventory().addItem(new Weapon("Sword", "d", 3));
        InteractionResult r = engine.equipItem(player, "sword");
        assertTrue(r.isSuccess());
        assertEquals("Equipped weapon Sword.", r.getMessage());
        assertNotNull(player.getEquippedWeapon());
        assertEquals("Sword", player.getEquippedWeapon().getName());
    }

    @Test
    void equipArmorSucceeds() {
    	
        player.getInventory().addItem(new Armor("Shield", "d", 2));
        InteractionResult r = engine.equipItem(player, "Shield");
        assertTrue(r.isSuccess());
        assertEquals("Equipped armor Shield.", r.getMessage());
        assertNotNull(player.getEquippedArmor());
        assertEquals("Shield", player.getEquippedArmor().getName());
    }

    @Test
    void equipNonEquippableFails() {
        player.getInventory().addItem(new Potion("P", "d", 5));
        InteractionResult r = engine.equipItem(player, "p");
        assertFalse(r.isSuccess());
        assertEquals("That item cannot be equipped.", r.getMessage());
    }

    @Test
    void unlockRoomWithNoExitFails() {
        InteractionResult r = engine.unlockRoom(player, Direction.NORTH);
        assertFalse(r.isSuccess());
        assertEquals("There is no room in that direction.", r.getMessage());
    }

    @Test
    void unlockAlreadyUnlockedReturnsSuccessMessage() {
        InteractionResult r = engine.unlockRoom(player, Direction.EAST);
        assertTrue(r.isSuccess());
        assertEquals("The room is already unlocked.", r.getMessage());
    }

    @Test
    void unlockWithoutRequiredKeyFails() {
        east.setLocked(true, "Key A");
        InteractionResult r = engine.unlockRoom(player, Direction.EAST);
        assertFalse(r.isSuccess());
        assertEquals("You do not have the correct key.", r.getMessage());
    }

    @Test
    void unlockWithNonKeyItemSameNameFails() {
        east.setLocked(true, "Key A");
        // An item named "Key A" that is NOT a Key should fail.
        player.getInventory().addItem(new Potion("Key A", "d", 5));
        InteractionResult r = engine.unlockRoom(player, Direction.EAST);
        assertFalse(r.isSuccess());
        assertEquals("You do not have the correct key.", r.getMessage());
        assertTrue(east.isLocked());
    }

    @Test
    void unlockWithCorrectKeySucceeds() {
        east.setLocked(true, "Key A");
        player.getInventory().addItem(new Key("Key A", "d"));
        InteractionResult r = engine.unlockRoom(player, Direction.EAST);
        assertTrue(r.isSuccess());
        assertEquals("Unlocked East.", r.getMessage());
        assertFalse(east.isLocked());
    }

    @Test
    void unlockLockedRoomWithNullRequiredKeyFails() {
        east.setLocked(true, null);
        InteractionResult r = engine.unlockRoom(player, Direction.EAST);
        assertFalse(r.isSuccess());
        assertEquals("The room cannot be unlocked.", r.getMessage());
    }

    @Test
    void pickUpItemThatIsNotSpecialDoesNotTouchQuest() {
        room.addItem(new Key("Random", "d"));
        engine.pickUpItem(player, quest, "Random");
        assertFalse(quest.isGradebookRecovered());
        assertFalse(quest.isPhantomDefeated());
    }

    @Test
    void pickUpNullNameReturnsNotFound() {
        // Room.removeItemByName tolerates null (equalsIgnoreCase(null) is false)
        // so the engine reports "Item not found in the room." and nothing explodes.
    	
        room.addItem(new Key("X", "d"));
        InteractionResult r = engine.pickUpItem(player, quest, null);
        assertFalse(r.isSuccess());
        assertEquals("Item not found in the room.", r.getMessage());
    }

    @SuppressWarnings("unused")
    private static final List<String> _unused = List.of();
}
