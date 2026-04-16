package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * File:    TestPlayerExtra.java
 * Author:  Jack Belleville
 * Desc:    Supplemental mutation analysis tests for Player. Targets the
 *          Math.max/Math.min clamping in takeDamage and heal, the
 *          attack/defense power computation (weapon + armor branches),
 *          and equip null-guards.
 */
public class TestPlayerExtra {

    @ParameterizedTest
    @CsvSource({
        // maxHp, dmg, expectedHp
        "50,   0, 50",   // zero damage
        "50,  -5, 50",   // negative damage clamped to 0
        "50,  10, 40",   // normal
        "50,  50,  0",   // exact to zero
        "50, 100,  0"    // overkill clamps at 0
    })
    void takeDamageClampsAtZeroAndIgnoresNegative(int maxHp, int dmg, int expected) {
        Player p = new Player("P", maxHp, 1, 1, new Inventory(1));
        p.takeDamage(dmg);
        assertEquals(expected, p.getHealth());
    }

    @ParameterizedTest
    @CsvSource({
        // dmgTaken, heal, expectedHp (maxHp = 50)
        "20,   0, 30",    // zero heal
        "20,  -5, 30",    // negative heal ignored
        "20,  10, 40",    // normal
        "20,  30, 50",    // overheal clamps at maxHp
        " 0,  10, 50"     // already full, still capped
    })
    void healClampsAtMaxAndIgnoresNegative(int dmg, int heal, int expected) {
        Player p = new Player("P", 50, 1, 1, new Inventory(1));
        p.takeDamage(dmg);
        p.heal(heal);
        assertEquals(expected, p.getHealth());
    }

    @Test
    void isAliveBoundaryAtZero() {
        Player p = new Player("P", 10, 1, 1, new Inventory(1));
        assertTrue(p.isAlive());
        p.takeDamage(9);
        assertTrue(p.isAlive());
        p.takeDamage(1);
        assertFalse(p.isAlive()); // health == 0 is NOT alive
    }

    @Test
    void attackPowerWithoutWeaponEqualsBaseAttack() {
        Player p = new Player("P", 10, 7, 2, new Inventory(1));
        assertEquals(7, p.getAttackPower());
    }

    @Test
    void attackPowerAddsWeaponBonus() {
        Player p = new Player("P", 10, 7, 2, new Inventory(1));
        p.equipWeapon(new Weapon("W", "d", 4));
        assertEquals(11, p.getAttackPower());
    }

    @Test
    void defensePowerWithoutArmorEqualsBaseDefense() {
        Player p = new Player("P", 10, 7, 2, new Inventory(1));
        assertEquals(2, p.getDefensePower());
    }

    @Test
    void defensePowerAddsArmorBonus() {
        Player p = new Player("P", 10, 7, 2, new Inventory(1));
        p.equipArmor(new Armor("A", "d", 3));
        assertEquals(5, p.getDefensePower());
    }

    @Test
    void equipWeaponNullRejected() {
        Player p = new Player("P", 10, 7, 2, new Inventory(1));
        assertThrows(NullPointerException.class, () -> p.equipWeapon(null));
    }

    @Test
    void equipArmorNullRejected() {
        Player p = new Player("P", 10, 7, 2, new Inventory(1));
        assertThrows(NullPointerException.class, () -> p.equipArmor(null));
    }

    @Test
    void constructorRejectsNullNameAndInventory() {
        assertThrows(NullPointerException.class,
                () -> new Player(null, 10, 1, 1, new Inventory(1)));
        assertThrows(NullPointerException.class,
                () -> new Player("P", 10, 1, 1, null));
    }

    @Test
    void setCurrentRoomRejectsNull() {
        Player p = new Player("P", 10, 1, 1, new Inventory(1));
        assertThrows(NullPointerException.class, () -> p.setCurrentRoom(null));
    }

    @Test
    void equipReplacesExistingEquipment() {
        Player p = new Player("P", 10, 7, 2, new Inventory(1));
        Weapon w1 = new Weapon("W1", "d", 2);
        Weapon w2 = new Weapon("W2", "d", 5);
        p.equipWeapon(w1);
        p.equipWeapon(w2);
        assertSame(w2, p.getEquippedWeapon());
        assertEquals(12, p.getAttackPower());
    }
}
