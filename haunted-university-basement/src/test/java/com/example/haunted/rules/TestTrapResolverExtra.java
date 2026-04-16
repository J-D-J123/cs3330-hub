package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;

/**
 * File:    TestTrapResolverExtra.java
 * Author:  Jack Belleville
 * Desc:    Supplemental mutation analysis tests for TrapResolver. Targets the
 *          one-time-trigger disarm branch and the non-one-time stays-armed
 *          branch - mutation operators on boolean fields will flip these.
 */
public class TestTrapResolverExtra {

    private final TrapResolver resolver = new TrapResolver();

    @Test
    void oneTimeTrapDisarmsItselfAfterTriggering() {
        Player p = new Player("P", 20, 1, 0, new Inventory(1));
        Trap trap = new Trap("Spike", TrapType.ELECTRIC, 5, true, true);
        InteractionResult r = resolver.resolveTrap(p, trap);
        assertTrue(r.isSuccess());
        assertFalse(trap.isArmed(), "one-time trap must be disarmed after firing");
    }

    @Test
    void repeatingTrapStaysArmedAfterTriggering() {
        Player p = new Player("P", 20, 1, 0, new Inventory(1));
        Trap trap = new Trap("Steam", TrapType.STEAM, 5, true, false);
        resolver.resolveTrap(p, trap);
        assertTrue(trap.isArmed(), "repeating trap must stay armed");
    }

    @Test
    void triggerMessageContainsNameAndDamage() {
        Player p = new Player("P", 20, 1, 0, new Inventory(1));
        Trap trap = new Trap("Zap", TrapType.ELECTRIC, 7, true, false);
        InteractionResult r = resolver.resolveTrap(p, trap);
        assertEquals("Trap 'Zap' triggered for 7 damage.", r.getMessage());
    }

    @Test
    void nullTrapReturnsFailureWithoutTouchingPlayer() {
        Player p = new Player("P", 20, 1, 0, new Inventory(1));
        InteractionResult r = resolver.resolveTrap(p, null);
        assertFalse(r.isSuccess());
        assertEquals("No trap was triggered.", r.getMessage());
        assertEquals(20, p.getHealth());
    }

    @Test
    void unarmedTrapReturnsFailureWithoutTouchingPlayer() {
        Player p = new Player("P", 20, 1, 0, new Inventory(1));
        Trap trap = new Trap("Dud", TrapType.STEAM, 5, false, false);
        InteractionResult r = resolver.resolveTrap(p, trap);
        assertFalse(r.isSuccess());
        assertEquals("No trap was triggered.", r.getMessage());
        assertEquals(20, p.getHealth());
    }
}
