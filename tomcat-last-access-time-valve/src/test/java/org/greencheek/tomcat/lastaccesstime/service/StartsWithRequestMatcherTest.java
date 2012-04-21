package org.greencheek.tomcat.lastaccesstime.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StartsWithRequestMatcherTest {

	private RequestMatcher matcher;
	
	@Before
	public void setup() {
		matcher = new StartsWithRequestMatcher();
	}
	
	@Test
	public void testAddRequestUrisIndividually() {
		matcher.addRequestUris("/start.jsp");
		matcher.addRequestUris("/stop.jsp");
		
		assertTrue(matcher.matches("/start.jsp"));
		assertTrue(matcher.matches("/stop.jsp"));
		assertFalse(matcher.matches("/start2.jsp"));
		
	}
	
	@Test
	public void testAddRequestUrisAsCommaStrings() {
		matcher.addRequestUris("/start.jsp,/stop.jsp");		
		
		assertTrue(matcher.matches("/start.jsp"));
		assertTrue(matcher.matches("/stop.jsp"));
		assertFalse(matcher.matches("/start2.jsp"));
		
	}


}
