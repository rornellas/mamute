package br.com.caelum.brutal.input;


import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Test;

import br.com.caelum.brutal.dao.TestCase;
import br.com.caelum.brutal.model.User;
import br.com.caelum.timemachine.Block;
import br.com.caelum.timemachine.TimeMachine;

public class InputManagerTest extends TestCase {
	final InputManager input = new InputManager();
	final User leo = user("Leonardo", "leo@leo", 1l);

	@Test
	public void should_allow_if_after_15_secconds() {
		input.ping(leo);
		TimeMachine.goTo(new DateTime().plusSeconds(16)).andExecute(new Block<Void>() {
			@Override
			public Void run() {
				assertTrue(input.can(leo));
				return null;
			}
		});
	}
	
	@Test
	public void should_not_allow_if_before_15_secconds() {
		input.ping(leo);
		assertFalse(input.can(leo));
	}
	
	@Test
	public void should_get_the_remaining_time() {
		input.ping(leo);
		TimeMachine.goTo(new DateTime().plusSeconds(5)).andExecute(new Block<Void>() {
			@Override
			public Void run() {
				assertEquals(10 ,input.getRemainingTime(leo));
				return null;
			}
		});		
	}

}