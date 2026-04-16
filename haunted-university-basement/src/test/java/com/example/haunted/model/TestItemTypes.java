package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * File:    TestItemTypes.java
 * Author:  Jack Belleville
 * Desc:    Mutation-analysis tests for Item subclasses - Weapon, Armor,
 *          Potion, Key, QuestItem - and the Item base class behavior
 *          (name, description, toString, null-guard).
 */
public class TestItemTypes {

    @Test
    void weaponStoresAttackBonus() {
        Weapon w = new Weapon("Sword", "desc", 4);
        assertEquals("Sword", w.getName());
        assertEquals("desc", w.getDescription());
        assertEquals(4, w.getAttackBonus());
    }

    @Test
    void armorStoresDefenseBonus() {
        Armor a = new Armor("Shield", "desc", 3);
        assertEquals("Shield", a.getName());
        assertEquals("desc", a.getDescription());
        assertEquals(3, a.getDefenseBonus());
    }

    @Test
    void potionStoresHealingAmount() {
        Potion p = new Potion("Elixir", "d", 15);
        assertEquals(15, p.getHealingAmount());
    }

    @Test
    void potionUseHealsTargetPlayer() {
        Player player = new Player("P", 50, 1, 1, new Inventory(1));
        player.takeDamage(20);
        Potion p = new Potion("Elixir", "d", 15);
        p.use(player);
        assertEquals(45, player.getHealth());
    }

    @Test
    void potionUseDoesNotExceedMaxHealth() {
        Player player = new Player("P", 50, 1, 1, new Inventory(1));
        player.takeDamage(5);
        Potion p = new Potion("Elixir", "d", 99);
        p.use(player);
        assertEquals(50, player.getHealth());
    }

    @Test
    void keyIsAnItem() {
        Key k = new Key("Archive Key", "desc");
        assertEquals("Archive Key", k.getName());
        assertEquals("desc", k.getDescription());
        assertTrue(k instanceof Item);
    }

    @Test
    void questItemIsAnItem() {
        QuestItem q = new QuestItem("Gradebook", "d");
        assertEquals("Gradebook", q.getName());
        assertTrue(q instanceof Item);
    }

    @Test
    void itemToStringReturnsName() {
        Key k = new Key("Archive Key", "desc");
        assertEquals("Archive Key", k.toString());
    }

    @Test
    void itemRejectsNullName() {
        assertThrows(NullPointerException.class, () -> new Key(null, "d"));
    }

    @Test
    void itemRejectsNullDescription() {
        assertThrows(NullPointerException.class, () -> new Key("n", null));
    }
}
