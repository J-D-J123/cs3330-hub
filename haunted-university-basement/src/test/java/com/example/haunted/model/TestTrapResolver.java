/**
* File: 		TestTrapResolver.java
* Author:	Joey Johnson
* Date:		04/15/2026
* Desc:		tests for TrapResolver 
* 				checks for traps that go off once they are armed. 
* 				also tests the correct damage to be taken off per trap case.
*
* 			p.s. I will test on myself and give myself 20 health points to surivie 
*/

package com.example.haunted.model;

// required libarys from junit 
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

// files to be used when testing 
import com.example.haunted.events.InteractionResult;
import com.example.haunted.rules.TrapResolver;

class TrapResolverTest {

    private TrapResolver resolver = new TrapResolver();

    // paramtierized test cases of different types STEAM as well as ELECTRIC 
    static Stream<Arguments> trapCases() {
        return Stream.of(

            // no trap bc of null 
            Arguments.of(null, false, "No trap was triggered.", 20),

            // trap is in the room but is not enabled so no damage is able to be done 
            // i finally do not get attacked yayy :)
            Arguments.of(new Trap("Steam Vent", TrapType.STEAM, 10, false, false),
                false, "No trap was triggered.", 20),

            // the steam vent trap hits me for 15 health points   
            // 20-15=5 health points 
            Arguments.of(new Trap("Steam Vent", TrapType.STEAM, 15, true, false),
                true, "Trap 'Steam Vent' triggered for 15 damage.", 5),

            // shock wire is a one time trap then does not trigger ever again 
            // 20-10=10 health poitns
            Arguments.of(new Trap("Shock Wire", TrapType.ELECTRIC, 10, true, true),
                true, "Trap 'Shock Wire' triggered for 10 damage.", 10)
        );
    }

    /**
     * the method testResolveTrapDealsCorrectDamage() tests the resolveTrap() method in TrapResolver 
     * 	by comparing what the data is for example
     * 	if the trap was succesfful 
     * 	what message it reutrns
     * 	what is the Player's hp after the trap 
     * @param trap the Trap object 
     * @param triggered if the trap was triggered or not 
     * @param msg the message input to check the output message 
     * @param hpAfter the player's hp after the trap 
     */
    @ParameterizedTest
    @MethodSource("trapCases")
    void testResolveTrap(Trap trap, boolean triggered, String msg, int hpAfter) {
    	
        // new player :) [me] so it is a clean instance every run 
        Player joey = new Player("Joey", 20, 5, 5, new Inventory(10));

        InteractionResult result = resolver.resolveTrap(joey, trap);

        // check to see if the correct field is stored 
        assertEquals(triggered, result.isSuccess());
        assertEquals(msg, result.getMessage());
        assertEquals(hpAfter, joey.getHealth());
    }
}
