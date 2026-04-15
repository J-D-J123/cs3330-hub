/**
* File: 		TrapTest.java
* Author:	Joey Johnson
* Desc:		to test the Trap.java class logic in package
* 				com.example.haunted.model
*/

package com.example.haunted.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrapTest {

    @ParameterizedTest
    @CsvSource({
        "Bomb Trap, STEAM, 15, true, false",
        "Shock Trap, ELECTRIC, 10, true, true",
        "Wave Trap, STEAM, 5, false, false"
    })
    void makeTrap(String name, TrapType type, int damage, boolean armed, boolean oneTime) {

        Trap trap = new Trap(name, type, damage, armed, oneTime);

        assertEquals(name, trap.getName());
        assertEquals(type, trap.getType());
        assertEquals(damage, trap.getDamage());
        assertEquals(armed, trap.isArmed());
        assertEquals(oneTime, trap.isOneTimeTrigger());
    }

    @Test
    void testTrapDisarm() {

        Trap trap = new Trap("Bomb Trap", TrapType.STEAM, 15, true, false);

        trap.disarm();

        assertFalse(trap.isArmed());
    }
}
