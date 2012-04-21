package org.greencheek.tomcat.lastaccesstime.service.jmx;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;
import org.greencheek.tomcat.lastaccesstime.domain.jmx.LastAccessedTimeMXBean;
import org.greencheek.tomcat.lastaccesstime.service.GMTLastAccessResourceFactory;
import org.greencheek.tomcat.lastaccesstime.service.LastAccessResourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JMXAccessTimeRecorderTest {

	private MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

	private JMXAccessTimeRecorder recorder;
	private LastAccessResourceFactory factory;
	
	
	@Before
	public void setup() {
		factory = new GMTLastAccessResourceFactory();
		recorder = new JMXAccessTimeRecorder(factory.createInitialResource());
	}
	
	@After
	public void tearDown() {
		recorder.destroy();
	}
	
	@Test
	public void testLastAccessTimeIsRecordedInJMX() throws MalformedObjectNameException, NullPointerException {
		LastAccessedResource resource = factory.createLastAccessResourceFor("GET", "/");
		recorder.recordAccesss(resource);
		
		ObjectName name = new ObjectName(recorder.getBeanObjectName());
		LastAccessedTimeMXBean proxy = JMX.newMXBeanProxy(mbeanServer,name,LastAccessedTimeMXBean.class,false);
		LastAccessedResource res = proxy.getLastAccessTime();
		
		assertEquals(resource.getTimeAsMillis(),res.getTimeAsMillis());
		assertEquals(resource.getTimeAsString(),res.getTimeAsString());
		assertEquals(resource.getRequestedResource(),res.getRequestedResource());
		assertEquals(resource.getRequestMethod(),res.getRequestMethod());
		
	}
}
