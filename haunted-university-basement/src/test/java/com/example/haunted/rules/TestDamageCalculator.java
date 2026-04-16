package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.haunted.model.Armor;
import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Weapon;

/**
 * File:    TestDamageCalculator.java
 * Author:  Jack Belleville
 * Desc:    Mutation analysis tests for DamageCalculator. Targets the Math.max(1, ...)
 *          floor (conditional boundary mutation) and the BossMonster enrage branch
 *          inside calculateMonsterDamage.
 */
public class TestDamageCalculator {

    private final DamageCalculator calc = new DamageCalculator();

    @ParameterizedTest
    @CsvSource({
        // attack, weaponBonus, monsterDef, expectedDamage
        "5,  0,  2, 3",   // normal
        "10, 0,  4, 6",   // normal
        "2,  0,  1, 1",   // minimal positive
        "1,  0,  5, 1",   // attack < def  => floor at 1 (kills the Math.max mutant)
        "5,  0,  5, 1",   // attack == def => floor at 1 (boundary)
        "3,  4,  2, 5"    // weapon bonus applied
    })
    void calculatePlayerDamageFloorsAtOne(int atk, int bonus, int def, int expected) {
        Player p = new Player("P", 10, atk, 0, new Inventory(1));
        if (bonus > 0) p.equipWeapon(new Weapon("W", "d", bonus));
        Monster m = new Monster("M", 10, 1, def, List.of());
        assertEquals(expected, calc.calculatePlayerDamage(p, m));
    }

    @ParameterizedTest
    @CsvSource({
        // monsterAtk, playerDef, armorBonus, expectedDamage
        "6,  2,  0, 4",
        "10, 4,  0, 6",
        "1,  5,  0, 1",   // floor case
        "5,  5,  0, 1",   // boundary equal
        "8,  2,  3, 3"    // armor applied
    })
    void calculateMonsterDamageFloorsAtOne(int atk, int def, int armor, int expected) {
        Player p = new Player("P", 10, 0, def, new Inventory(1));
        if (armor > 0) p.equipArmor(new Armor("A", "d", armor));
        Monster m = new Monster("M", 10, atk, 0, List.of());
        assertEquals(expected, calc.calculateMonsterDamage(m, p));
    }

    @Test
    void bossUsesNormalAttackWhenAboveHalfHealth() {
        Player p = new Player("P", 10, 0, 0, new Inventory(1));
        BossMonster boss = new BossMonster("Boss", 40, 10, 0, List.of(), 5);
        // full HP, no enrage
        assertEquals(10, calc.calculateMonsterDamage(boss, p));
    }

    @Test
    void bossUsesEnragedAttackWhenAtOrBelowHalfHealth() {
        Player p = new Player("P", 10, 0, 0, new Inventory(1));
        BossMonster boss = new BossMonster("Boss", 40, 10, 0, List.of(), 5);
        boss.takeDamage(20); // exactly half HP => enraged
        assertEquals(15, calc.calculateMonsterDamage(boss, p));

        boss.takeDamage(1); // below half
        assertEquals(15, calc.calculateMonsterDamage(boss, p));
    }

    @Test
    void bossEnrageBoundaryIsInclusive() {
        // mutation target: replacing <= with < in BossMonster.getCurrentAttack.
        // At maxHealth/2 exactly, we should see enraged damage.
        Player p = new Player("P", 10, 0, 0, new Inventory(1));
        BossMonster boss = new BossMonster("Boss", 20, 8, 0, List.of(), 4);
        boss.takeDamage(10); // health = 10, maxHealth/2 = 10 => at boundary
        assertEquals(12, calc.calculateMonsterDamage(boss, p),
                "Boss should enrage at exactly half HP");
    }

    @Test
    void plainMonsterUsesItsOwnAttackValue() {
        // Ensures the instanceof check is NOT treated as always-true, which would
        // call getCurrentAttack on non-boss monsters (doesn't compile, but the
        // branch for regular monsters is what we verify here).
        Player p = new Player("P", 10, 0, 0, new Inventory(1));
        Monster m = new Monster("Ghost", 10, 7, 0, List.of());
        assertEquals(7, calc.calculateMonsterDamage(m, p));
    }
}
