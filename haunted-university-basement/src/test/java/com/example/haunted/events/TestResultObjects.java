package com.example.haunted.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.haunted.model.Item;
import com.example.haunted.model.Key;

/**
 * File:    TestResultObjects.java
 * Author:  Jack Belleville
 * Desc:    Mutation-analysis tests for the three event/result DTOs. These
 *          tests lock down the exact field values returned by the getters and
 *          the defensive-copy behavior of CombatResult.getDroppedItems().
 */
public class TestResultObjects {

    @Test
    void combatResultGettersReturnConstructorValues() {
        List<Item> drops = List.of(new Key("K", "d"));
        CombatResult r = new CombatResult(true, "Msg", 5, 3, true, drops);
        assertTrue(r.isSuccess());
        assertEquals("Msg", r.getMessage());
        assertEquals(5, r.getDamageToMonster());
        assertEquals(3, r.getDamageToPlayer());
        assertTrue(r.isMonsterDefeated());
        assertEquals(1, r.getDroppedItems().size());
        assertEquals("K", r.getDroppedItems().get(0).getName());
    }

    @Test
    void combatResultDroppedItemsIsDefensiveCopy() {
        List<Item> drops = new ArrayList<>();
        drops.add(new Key("K", "d"));
        CombatResult r = new CombatResult(true, "m", 0, 0, true, drops);
        // mutating the source list must not affect the result
        drops.add(new Key("X", "d"));
        assertEquals(1, r.getDroppedItems().size());
        // and the returned view is unmodifiable
        assertThrows(UnsupportedOperationException.class,
                () -> r.getDroppedItems().add(new Key("Y", "d")));
    }

    @Test
    void combatResultFalseStateIsCorrectlyReported() {
        CombatResult r = new CombatResult(false, "none", 0, 0, false, List.of());
        assertFalse(r.isSuccess());
        assertFalse(r.isMonsterDefeated());
        assertTrue(r.getDroppedItems().isEmpty());
    }

    @Test
    void moveResultGettersReturnConstructorValues() {
        MoveResult r = new MoveResult(true, "moved", true, 7);
        assertTrue(r.isSuccess());
        assertEquals("moved", r.getMessage());
        assertTrue(r.isTrapTriggered());
        assertEquals(7, r.getTrapDamage());
    }

    @Test
    void moveResultFailedDoesNotReportTrap() {
        MoveResult r = new MoveResult(false, "blocked", false, 0);
        assertFalse(r.isSuccess());
        assertFalse(r.isTrapTriggered());
        assertEquals(0, r.getTrapDamage());
    }

    @Test
    void interactionResultGettersReturnConstructorValues() {
        InteractionResult r = new InteractionResult(true, "done");
        assertTrue(r.isSuccess());
        assertEquals("done", r.getMessage());
    }

    @Test
    void interactionResultFailureReportsFalse() {
        InteractionResult r = new InteractionResult(false, "nope");
        assertFalse(r.isSuccess());
        assertEquals("nope", r.getMessage());
    }
}
