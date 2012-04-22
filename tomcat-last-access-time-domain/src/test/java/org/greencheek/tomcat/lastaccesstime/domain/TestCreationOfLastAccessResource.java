/*
Copyright 2012 Dominic Tootell

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.greencheek.tomcat.lastaccesstime.domain;

import static org.junit.Assert.*;

import java.util.Date;

import org.greencheek.tomcat.lastaccesstime.service.GMTLastAccessResourceFactory;
import org.greencheek.tomcat.lastaccesstime.service.LastAccessResourceFactory;
import org.greencheek.tomcat.lastaccesstime.service.ThreadLocalGMTDateFormatter;
import org.junit.Before;
import org.junit.Test;

public class TestCreationOfLastAccessResource {

	private LastAccessResourceFactory factory;
	private ThreadLocalGMTDateFormatter formatter;

	
	@Before
	public void createADateFormatter() {
		factory = new GMTLastAccessResourceFactory();
		formatter = new ThreadLocalGMTDateFormatter();
	}
	
	@Test
	public void testCanCreateALastAccessResourceFromStartOfComputerTime() {
		LastAccessedResource resource = factory.createInitialResource();
		
		assertEquals("1970-01-01T00:00:00.000GMT",resource.getTimeAsString());
		assertEquals(0,resource.getTimeAsMillis());
		assertEquals(GMTLastAccessResourceFactory.INITIAL_REQUEST_METHOD, resource.getRequestMethod());
		assertEquals(GMTLastAccessResourceFactory.INITIAL_REQUESTED_RESOURCE,resource.getRequestedResource());
		
	}
	
	@Test
	public void testCanCreateALastAccessResourceForCurrentDayTime() throws InterruptedException {
		String currentDay = formatter.format(new Date());
		currentDay = currentDay.substring(0,16);
		
		Date before = new Date();
		Thread.sleep(10);
		LastAccessedResource resource = factory.createLastAccessResourceFor("INIT", "/");
		Thread.sleep(10);
		
		Date after = new Date();	

		assertTrue(resource.getTimeAsString().startsWith(currentDay));		
		
		Date date = new Date(resource.getTimeAsMillis());		
		assertTrue((date.after(before) && date.before(after)));
		
		assertEquals("INIT", resource.getRequestMethod());
		assertEquals("/",resource.getRequestedResource());
	}
	
	
	@Test
	public void testCanCreateALastAccessResourceFromGivenItemRatherThanCurrentDate() {
		String currentDay = formatter.format(new Date());
		currentDay = currentDay.substring(0,16);
		
		LastAccessedResource resource = new LastAccessedResource("not sure",0,"bob","/non");
		
		System.out.println(new Date((int)(new Date().getTime()/86400)));
		
		assertEquals("not sure",resource.getTimeAsString());
		assertEquals(0,resource.getTimeAsMillis());
		assertEquals("bob", resource.getRequestMethod());
		assertEquals("/non",resource.getRequestedResource());
	}
	
	

}
