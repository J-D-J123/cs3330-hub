package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;
import com.example.haunted.rules.TrapResolver;

/**
 * File:    TestMovementEngine.java
 * Author:  Jack Belleville
 * Desc:    Mutation analysis tests for MovementEngine. Targets the three main
 *          branches of move(): no exit, locked target, successful move; plus
 *          the trap trigger sub-branch and the disarming of one-time traps.
 */
public class TestMovementEngine {

    private MovementEngine engine;
    private Player player;
    private Room start;
    private Room east;

    @BeforeEach
    void setUp() {
        engine = new MovementEngine(new TrapResolver());
        player = new Player("P", 50, 5, 2, new Inventory(5));
        start = new Room("start", "Start", "d");
        east = new Room("east", "East Room", "d");
        start.connect(Direction.EAST, east);
        player.setCurrentRoom(start);
    }

    @Test
    void moveIntoDirectionWithNoExitFails() {
        MoveResult r = engine.move(player, Direction.NORTH);
        assertFalse(r.isSuccess());
        assertEquals("There is no room in that direction.", r.getMessage());
        assertFalse(r.isTrapTriggered());
        assertEquals(0, r.getTrapDamage());
        assertSame(start, player.getCurrentRoom());
    }

    @Test
    void moveIntoLockedRoomFails() {
        east.setLocked(true, "Some Key");
        MoveResult r = engine.move(player, Direction.EAST);
        assertFalse(r.isSuccess());
        assertEquals("The room is locked.", r.getMessage());
        assertSame(start, player.getCurrentRoom());
    }

    @Test
    void moveIntoUnlockedRoomSucceeds() {
        MoveResult r = engine.move(player, Direction.EAST);
        assertTrue(r.isSuccess());
        assertEquals("Moved to East Room.", r.getMessage());
        assertFalse(r.isTrapTriggered());
        assertEquals(0, r.getTrapDamage());
        assertSame(east, player.getCurrentRoom());
    }

    @Test
    void moveIntoRoomWithArmedTrapTakesDamageAndReports() {
        east.setTrap(new Trap("Spike Trap", TrapType.STEAM, 5, true, false));
        int hpBefore = player.getHealth();
        MoveResult r = engine.move(player, Direction.EAST);
        assertTrue(r.isSuccess());
        assertTrue(r.isTrapTriggered());
        assertEquals(5, r.getTrapDamage());
        assertEquals(hpBefore - 5, player.getHealth());
        assertTrue(r.getMessage().startsWith("Moved to East Room."));
        assertTrue(r.getMessage().contains("Spike Trap"));
    }

    @Test
    void unarmedTrapDoesNotTriggerOnMove() {
        east.setTrap(new Trap("Spike Trap", TrapType.STEAM, 5, false, false));
        int hpBefore = player.getHealth();
        MoveResult r = engine.move(player, Direction.EAST);
        assertTrue(r.isSuccess());
        assertFalse(r.isTrapTriggered());
        assertEquals(0, r.getTrapDamage());
        assertEquals(hpBefore, player.getHealth());
    }

    @Test
    void oneTimeTrapDisarmsAfterFiring() {
        east.setTrap(new Trap("Spike Trap", TrapType.ELECTRIC, 5, true, true));
        Room west = new Room("west", "West", "d");
        east.connect(Direction.WEST, start);
        start.connect(Direction.WEST, west);
        west.connect(Direction.EAST, start);

        engine.move(player, Direction.EAST);
        // go back
        engine.move(player, Direction.WEST);
        int hp = player.getHealth();
        // re-enter east; trap should be disarmed now
        MoveResult r = engine.move(player, Direction.EAST);
        assertTrue(r.isSuccess());
        assertFalse(r.isTrapTriggered());
        assertEquals(hp, player.getHealth());
    }

    @Test
    void repeatingTrapFiresEveryTime() {
        east.setTrap(new Trap("Steam Vent", TrapType.STEAM, 3, true, false));
        east.connect(Direction.WEST, start);
        engine.move(player, Direction.EAST);
        engine.move(player, Direction.WEST);
        int hp = player.getHealth();
        engine.move(player, Direction.EAST);
        assertEquals(hp - 3, player.getHealth());
    }
}
