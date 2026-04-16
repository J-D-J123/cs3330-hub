package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * File:    TestRoom.java
 * Author:  Jack Belleville
 * Desc:    Mutation-analysis tests for Room. Targets locked/unlocked branches,
 *          key-matching logic, case-insensitive item/monster lookup, and
 *          hasLivingMonsters.
 */
public class TestRoom {

    @Test
    void basicGettersReturnConstructorValues() {
        Room r = new Room("id", "Name", "Desc");
        assertEquals("id", r.getId());
        assertEquals("Name", r.getName());
        assertEquals("Desc", r.getDescription());
    }

    @Test
    void nullIdIsRejected() {
        assertThrows(NullPointerException.class, () -> new Room(null, "n", "d"));
    }

    @Test
    void newRoomHasNoTrapAndIsUnlocked() {
        Room r = new Room("id", "n", "d");
        assertNull(r.getTrap());
        assertFalse(r.isLocked());
        assertNull(r.getRequiredKeyName());
        assertFalse(r.hasLivingMonsters());
    }

    @Test
    void connectStoresExitAndGetExitRetrieves() {
        Room a = new Room("a", "A", "d");
        Room b = new Room("b", "B", "d");
        a.connect(Direction.EAST, b);
        assertSame(b, a.getExit(Direction.EAST));
        assertNull(a.getExit(Direction.WEST));
    }

    @Test
    void nullDirectionOrRoomRejectedByConnect() {
        Room a = new Room("a", "A", "d");
        Room b = new Room("b", "B", "d");
        assertThrows(NullPointerException.class, () -> a.connect(null, b));
        assertThrows(NullPointerException.class, () -> a.connect(Direction.EAST, null));
    }

    @Test
    void exitsMapIsUnmodifiable() {
        Room a = new Room("a", "A", "d");
        Room b = new Room("b", "B", "d");
        a.connect(Direction.EAST, b);
        assertThrows(UnsupportedOperationException.class,
                () -> a.getExits().put(Direction.WEST, b));
    }

    @Test
    void findItemAndRemoveItemAreCaseInsensitive() {
        Room r = new Room("id", "n", "d");
        Key key = new Key("Archive Key", "d");
        r.addItem(key);
        assertTrue(r.findItem("archive key").isPresent());
        Item removed = r.removeItemByName("ARCHIVE KEY");
        assertSame(key, removed);
        assertTrue(r.findItem("Archive Key").isEmpty());
    }

    @Test
    void removeItemByNameReturnsNullWhenNotFound() {
        Room r = new Room("id", "n", "d");
        assertNull(r.removeItemByName("nope"));
    }

    @Test
    void itemsListIsUnmodifiable() {
        Room r = new Room("id", "n", "d");
        r.addItem(new Key("A", "d"));
        assertThrows(UnsupportedOperationException.class,
                () -> r.getItems().remove(0));
    }

    @Test
    void monstersListIsUnmodifiable() {
        Room r = new Room("id", "n", "d");
        r.addMonster(new Monster("M", 5, 1, 0, List.of()));
        assertThrows(UnsupportedOperationException.class,
                () -> r.getMonsters().remove(0));
    }

    @Test
    void findMonsterIsCaseInsensitive() {
        Room r = new Room("id", "n", "d");
        r.addMonster(new Monster("Ghost", 5, 1, 0, List.of()));
        assertTrue(r.findMonster("ghost").isPresent());
        assertTrue(r.findMonster("GHOST").isPresent());
        assertTrue(r.findMonster("nope").isEmpty());
    }

    @Test
    void hasLivingMonstersTransitionsWhenAllDie() {
        Room r = new Room("id", "n", "d");
        Monster m1 = new Monster("a", 5, 1, 0, List.of());
        Monster m2 = new Monster("b", 5, 1, 0, List.of());
        r.addMonster(m1);
        r.addMonster(m2);
        assertTrue(r.hasLivingMonsters());
        m1.takeDamage(99);
        assertTrue(r.hasLivingMonsters());
        m2.takeDamage(99);
        assertFalse(r.hasLivingMonsters());
    }

    @Test
    void unlockWithCorrectKeyOpensRoom() {
        Room r = new Room("id", "n", "d");
        r.setLocked(true, "Key A");
        assertTrue(r.isLocked());
        assertTrue(r.unlock("Key A"));
        assertFalse(r.isLocked());
    }

    @Test
    void unlockIsCaseInsensitive() {
        Room r = new Room("id", "n", "d");
        r.setLocked(true, "Key A");
        assertTrue(r.unlock("key a"));
        assertFalse(r.isLocked());
    }

    @Test
    void unlockWithWrongKeyFails() {
        Room r = new Room("id", "n", "d");
        r.setLocked(true, "Key A");
        assertFalse(r.unlock("Key B"));
        assertTrue(r.isLocked());
    }

    @Test
    void unlockWithNullKeyFailsWhenKeyRequired() {
        Room r = new Room("id", "n", "d");
        r.setLocked(true, "Key A");
        assertFalse(r.unlock(null));
        assertTrue(r.isLocked());
    }

    @Test
    void unlockOnUnlockedRoomReturnsTrueAndStaysUnlocked() {
        Room r = new Room("id", "n", "d");
        // default is unlocked
        assertTrue(r.unlock("anything"));
        assertFalse(r.isLocked());
    }

    @Test
    void unlockOnLockedRoomWithNullRequiredKeyFails() {
        Room r = new Room("id", "n", "d");
        r.setLocked(true, null);
        assertFalse(r.unlock("any"));
        assertTrue(r.isLocked());
    }

    @Test
    void setTrapAndGetTrapWorkTogether() {
        Room r = new Room("id", "n", "d");
        Trap t = new Trap("T", TrapType.STEAM, 5, true, false);
        r.setTrap(t);
        assertSame(t, r.getTrap());
    }
}
