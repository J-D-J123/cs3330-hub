/**
* File: 		TestTrapResolver.java 
* Author:	Joey Johnson
* Date:		04/15/2026
* Desc:		to test the Trap.java class logic in package 
* 				com.example.haunted.model
* Functions to test: 
*  		Trap(param(s)), getName(), getType(), getDamage(), isArmed(), isOneTimeTrigger(), 
*  		and then finally test the disarm() method all inside the Trap.java class 
*/ 

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.rules.TrapResolver;

class TestTrapResolver {
	
	private TrapResolver resolver = new TrapResolver(); 

	/**
	 * testNullTrap() tests the logic of the TrapResolver object
	 * 
	 */
	@Test
	void testNullTrap() {
		
		// make new Player to test with NULL trap
		// not sure if i need a Inventory for the player tho
		Player player = new Player("Joey", 20, 5, 5, null); 
		
		// result from the interaction 
		InteractionResult result = resolver.resolveTrap(player, null);
		
		assertFalse(result.isSuccess()); 
		
		// if the output equals "No trap was triggered." then 
		assertEquals("No trap was triggered.", result.getMessage()); 
		assertEquals(20, player.getHealth()); 
	}
	
	
	/**
	 * testUnarmedTrap() 
	 */
//	@Test
//	void testUnarmedTrap() {
//		fail("Not yet implemented");
//	}
	
	

}
