package com.example.haunted.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.haunted.engine.GameEngine;
import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;

/**
 * File:    TestDungeonFactory.java
 * Author:  Jack Belleville
 * Desc:    Verifies DungeonFactory.createGame() wires the world correctly:
 *          all rooms exist, exits are bidirectional where expected, items are
 *          placed in the correct rooms, monsters are placed correctly, locked
 *          rooms require the right key, and traps are armed as configured.
 */
public class TestDungeonFactory {

    private GameEngine engine;
    private Player player;

    @BeforeEach
    void setUp() {
        engine = DungeonFactory.createGame();
        player = engine.getPlayer();
    }

    @Test
    void gameEngineIsNotNull() {
        assertNotNull(engine);
        assertNotNull(engine.getPlayer());
        assertNotNull(engine.getQuest());
        assertNotNull(engine.getCurrentRoom());
    }

    @Test
    void playerStartsInStairwellWithCorrectStats() {
        assertEquals("stairwell", player.getCurrentRoom().getId());
        assertEquals("Student Explorer", player.getName());
        assertEquals(50, player.getMaxHealth());
        assertEquals(50, player.getHealth());
        assertEquals(7, player.getBaseAttack());
        assertEquals(2, player.getBaseDefense());
        assertEquals(8, player.getInventory().getCapacity());
        assertNull(player.getEquippedWeapon());
        assertNull(player.getEquippedArmor());
    }

    @Test
    void questStartsNotCompleted() {
        assertEquals("Escape the Basement", engine.getQuest().getName());
        assertFalse(engine.getQuest().isComplete());
        assertFalse(engine.getQuest().isGradebookRecovered());
        assertFalse(engine.getQuest().isPhantomDefeated());
    }

    @ParameterizedTest
    @CsvSource({
        "stairwell, Maintenance Stairwell",
        "lectureHall, Abandoned Lecture Hall",
        "labStorage, Lab Storage",
        "brokenElevator, Broken Elevator",
        "serverCloset, Server Closet",
        "examArchive, Exam Archive",
        "deanVault, Dean Vault",
        "finalChamber, Final Chamber"
    })
    void allRoomsExistWithCorrectNames(String expectedId, String expectedName) {
        Room room = findRoomById(expectedId);
        assertNotNull(room, "Room not found: " + expectedId);
        assertEquals(expectedName, room.getName());
        assertNotNull(room.getDescription());
    }

    @Test
    void stairwellConnectsEastToLectureHall() {
        Room stairwell = findRoomById("stairwell");
        assertEquals("lectureHall", stairwell.getExit(Direction.EAST).getId());
        assertNull(stairwell.getExit(Direction.NORTH));
        assertNull(stairwell.getExit(Direction.SOUTH));
        assertNull(stairwell.getExit(Direction.WEST));
    }

    @Test
    void lectureHallHasFourExits() {
        Room lectureHall = findRoomById("lectureHall");
        Map<Direction, Room> exits = lectureHall.getExits();
        assertEquals(4, exits.size());
        assertEquals("stairwell", exits.get(Direction.WEST).getId());
        assertEquals("labStorage", exits.get(Direction.EAST).getId());
        assertEquals("brokenElevator", exits.get(Direction.SOUTH).getId());
        assertEquals("examArchive", exits.get(Direction.NORTH).getId());
    }

    @Test
    void bidirectionalExitsAreConsistent() {
        Room stairwell = findRoomById("stairwell");
        Room lectureHall = findRoomById("lectureHall");
        assertSame(lectureHall, stairwell.getExit(Direction.EAST));
        assertSame(stairwell, lectureHall.getExit(Direction.WEST));

        Room deanVault = findRoomById("deanVault");
        Room finalChamber = findRoomById("finalChamber");
        assertSame(finalChamber, deanVault.getExit(Direction.NORTH));
        assertSame(deanVault, finalChamber.getExit(Direction.SOUTH));
    }

    @Test
    void examArchiveIsLockedAndRequiresArchiveKey() {
        Room examArchive = findRoomById("examArchive");
        assertTrue(examArchive.isLocked());
        assertEquals("Archive Key", examArchive.getRequiredKeyName());
    }

    @Test
    void finalChamberIsLockedAndRequiresVaultKey() {
        Room finalChamber = findRoomById("finalChamber");
        assertTrue(finalChamber.isLocked());
        assertEquals("Vault Key", finalChamber.getRequiredKeyName());
    }

    @Test
    void brokenElevatorHasElectricTrap() {
        Room brokenElevator = findRoomById("brokenElevator");
        Trap trap = brokenElevator.getTrap();
        assertNotNull(trap);
        assertEquals("Loose Wires Trap", trap.getName());
        assertEquals(TrapType.ELECTRIC, trap.getType());
        assertEquals(8, trap.getDamage());
        assertTrue(trap.isArmed());
        assertTrue(trap.isOneTimeTrigger());
    }

    @ParameterizedTest
    @CsvSource({
        "lectureHall, Coffee Potion",
        "labStorage, Archive Key",
        "labStorage, Calculator Shield",
        "serverCloset, Stapler of Justice",
        "examArchive, Lost Gradebook",
        "deanVault, Vault Key"
    })
    void itemsArePlacedInCorrectRooms(String roomId, String itemName) {
        Room room = findRoomById(roomId);
        assertTrue(room.findItem(itemName).isPresent(),
                "Item '" + itemName + "' missing from room '" + roomId + "'");
    }

    @ParameterizedTest
    @CsvSource({
        "lectureHall, Sleep-Deprived TA, 18",
        "serverCloset, Spreadsheet Golem, 28",
        "examArchive, Plagiarism Ghost, 22",
        "deanVault, Registrar Wraith, 30",
        "finalChamber, Final Exam Phantom, 40"
    })
    void monstersArePlacedInCorrectRooms(String roomId, String monsterName, int expectedHealth) {
        Room room = findRoomById(roomId);
        Monster monster = room.findMonster(monsterName).orElse(null);
        assertNotNull(monster, "Monster '" + monsterName + "' missing from '" + roomId + "'");
        assertEquals(expectedHealth, monster.getHealth());
    }

    @Test
    void finalChamberBossIsBossMonsterInstance() {
        Room finalChamber = findRoomById("finalChamber");
        Monster phantom = finalChamber.findMonster("Final Exam Phantom").orElseThrow();
        assertTrue(phantom instanceof BossMonster);
    }

    @Test
    void unlockedRoomsAreNotLocked() {
        assertFalse(findRoomById("stairwell").isLocked());
        assertFalse(findRoomById("lectureHall").isLocked());
        assertFalse(findRoomById("labStorage").isLocked());
        assertFalse(findRoomById("serverCloset").isLocked());
        assertFalse(findRoomById("deanVault").isLocked());
        assertFalse(findRoomById("brokenElevator").isLocked());
    }

    @Test
    void gameIsNotWonOrOverAtStart() {
        assertFalse(engine.isGameOver());
        assertFalse(engine.isGameWon());
    }

// walk the room graph starting at the stairwell to locate any room by id.
    private Room findRoomById(String id) {
        java.util.Set<Room> visited = new java.util.HashSet<>();
        java.util.Deque<Room> stack = new java.util.ArrayDeque<>();
        stack.push(engine.getCurrentRoom());
        while (!stack.isEmpty()) {
            Room r = stack.pop();
            if (!visited.add(r)) continue;
            if (r.getId().equals(id)) return r;
            for (Room next : r.getExits().values()) {
                stack.push(next);
            }
        }
        return null;
    }
}
