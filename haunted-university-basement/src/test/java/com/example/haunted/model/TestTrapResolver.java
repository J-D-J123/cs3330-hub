/**
* File: 		TestTrapResolver.java
* Author:	Joey Johnson
* Date:		04/15/2026
* Desc:		to test the TrapResolver.java class logic in package
* 				com.example.haunted.rules
* Functions to test:
*  		resolveTrap() with null trap, unarmed trap, armed trap, and armed one time trap
*/

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.rules.TrapResolver;

class TestTrapResolver {

    private TrapResolver resolver = new TrapResolver();

    // parameterized test cases for different test cases 
    static Stream<Arguments> trapScenarios() {
        return Stream.of(
        		
            // null trap without no trigger or damage 
            Arguments.of(null, false, "No trap was triggered.", 20),
            
            // unarmed trap without a trigger 
            Arguments.of(new Trap("Unarmed Trap", TrapType.STEAM, 10, false, false), false, "No trap was triggered.", 20),
            
            // armed trap 
            Arguments.of(new Trap("Bomb Trap", TrapType.STEAM, 15, true, false), true, "Trap 'Bomb Trap' triggered for 15 damage.", 5),
            
            // armed one time trap then disarms 
            Arguments.of(new Trap("Shock Trap", TrapType.ELECTRIC, 10, true, true), true, "Trap 'Shock Trap' triggered for 10 damage.", 10)
        );
    }

    /**
     * the TestTrapResolver() uses the multiple parameterized test cases from trapScenarios 
     * and tests each one to see if the trap triggers, outputs the message, as well as the correct health of the player
     * @param trap the Trap object 
     * @param expectedSuccess whether or not the trap is successfully triggered 
     * @param expectedMessage the expected message returned by the getMessage()
     * @param expectedHealth the expected health of the player after the trap
     */
    @ParameterizedTest
    @MethodSource("trapScenarios")
    void testTrapResolver(Trap trap, boolean expectedSuccess, String expectedMessage, int expectedHealth) {
        Player player = new Player("Joey", 20, 5, 5, new Inventory(10));

        InteractionResult result = resolver.resolveTrap(player, trap);

        assertEquals(expectedSuccess, result.isSuccess());
        assertEquals(expectedMessage, result.getMessage());
        assertEquals(expectedHealth, player.getHealth());
    }
}
