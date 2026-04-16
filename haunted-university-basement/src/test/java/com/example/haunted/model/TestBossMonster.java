package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * File:    TestBossMonster.java
 * Author:  Jack Belleville
 * Desc:    Mutation-analysis tests for BossMonster. Specifically exercises the
 *          boundary condition on getCurrentAttack (health <= maxHealth/2).
 */
public class TestBossMonster {

    @ParameterizedTest
    @CsvSource({
        // maxHealth, damageTaken, baseAttack, enragedBonus, expectedCurrentAttack
        "40,  0, 10, 3, 10",   // full HP, not enraged
        "40, 19, 10, 3, 10",   // HP=21 > 20, NOT enraged
        "40, 20, 10, 3, 13",   // HP=20 == max/2, enraged (boundary!)
        "40, 21, 10, 3, 13",   // HP=19, enraged
        "40, 40, 10, 3, 13",   // HP=0, still reports enraged
        "10,  5,  5, 2,  7"    // odd-maxHealth-like boundary
    })
    void getCurrentAttackRespectsEnrageBoundary(int maxHp, int dmg, int baseAtk, int bonus, int expected) {
        BossMonster boss = new BossMonster("Boss", maxHp, baseAtk, 0, List.of(), bonus);
        boss.takeDamage(dmg);
        assertEquals(expected, boss.getCurrentAttack());
    }

    @Test
    void bossInheritsMonsterBehavior() {
        BossMonster boss = new BossMonster("Boss", 100, 10, 5, List.of(), 3);
        assertEquals("Boss", boss.getName());
        assertEquals(100, boss.getMaxHealth());
        assertEquals(100, boss.getHealth());
        assertEquals(10, boss.getAttack());
        assertEquals(5, boss.getDefense());
        assertTrue(boss.isAlive());
    }

    @Test
    void bossBaseAttackUnchangedEvenWhenEnraged() {
        BossMonster boss = new BossMonster("Boss", 40, 10, 0, List.of(), 5);
        boss.takeDamage(30);
        // getAttack() returns the raw base attack, not enraged.
        assertEquals(10, boss.getAttack());
        // getCurrentAttack() returns the enraged value.
        assertEquals(15, boss.getCurrentAttack());
    }
}
