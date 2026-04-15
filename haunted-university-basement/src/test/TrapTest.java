/**
* File: 		TrapTest.java 
* Author:	Joey Johnson
* Date:		04/15/2026
* Desc:		to test the Trap.java class logic in package 
* 				com.example.haunted.model
* Functions to test: 
*  		Trap(param(s)), getName(), getType(), getDamage(), isArmed(), isOneTimeTrigger(), 
*  		and then finally test the disarm() method all inside the Trap.java class 
*/ 

package com.example.haunted.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrapTest {

	/**
	 * make make the Trap object with params
	 * @param name the name of the Trap object 
	 * @param type the type of the object 
	 * @param damage the amount of damage that trap can do 
	 * @param armed is it turned on and read to be used
	 * @param oneTime is it one time use or can it be used multiple times
	 */
    @ParameterizedTest
    @CsvSource({
        "Bomb Trap, STEAM, 15, true, false",
        "Shock Trap, ELECTRIC, 10, true, true",
        "Wave Trap, STEAM, 5, false, false", 
        "NULL, NULL, NULL, NULL", 
        "Joey was here trap, NULL, NULL, NULL, NULL"
    })
    void makeTrap(String name, TrapType type, int damage, boolean armed, boolean oneTime) {

        Trap trap = new Trap(name, type, damage, armed, oneTime);

        // check private object fields 
        // to make sure they change 
        // check getName(), getType(), getDamage(), isArmed(), and isOneTimeTrigger()
        assertEquals(name, trap.getName());
        assertEquals(type, trap.getType());
        assertEquals(damage, trap.getDamage());
        assertEquals(armed, trap.isArmed());
        assertEquals(oneTime, trap.isOneTimeTrigger());
    }

    /**
     * testTrapDisarm() makes a new trap and disarms it and checks if 
     * 	that new Trap is amred
     */
    @Test
    void testTrapDisarm() {

        Trap trap = new Trap("Bomb Trap", TrapType.STEAM, 15, true, false);

        trap.disarm();

        assertFalse(trap.isArmed());
    }
}
