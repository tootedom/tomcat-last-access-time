package org.greencheek.tomcat.lastaccesstime.service.jmx;

import static org.junit.Assert.*;

import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;
import org.greencheek.tomcat.lastaccesstime.domain.jmx.LastAccessedTimeMXBean;
import org.greencheek.tomcat.lastaccesstime.service.AccessTimeRecorderFactory;
import org.greencheek.tomcat.lastaccesstime.service.GMTLastAccessResourceFactory;
import org.greencheek.tomcat.lastaccesstime.service.LastAccessResourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JMXAccessTimeRecorderFactoryTest {

	private MBeanServer mbeanServer = ManagementFactory
			.getPlatformMBeanServer();

	private JMXAccessTimeRecorderFactory recorderFactory;
	private LastAccessResourceFactory factory;
	private static String DEFAULT_BEAN_NAME = JMXAccessTimeRecorder.DEFAULT_JMX_DOMAIN_NAME
			+ ":type=" + JMXAccessTimeRecorder.DEFAULT_JMX_BEAN_NAME;

	@Before
	public void setup() {
		factory = new GMTLastAccessResourceFactory();
		recorderFactory = new JMXAccessTimeRecorderFactory();

	}

	@Test
	public void testAccessTimeRecorderIsCreateWithDefaultJMXName()
			throws MalformedObjectNameException, NullPointerException {
		JMXAccessTimeRecorder recorder = null;
		try {
			recorder = recorderFactory.createAccessTimeRecorder(factory, "");
			assertEquals(DEFAULT_BEAN_NAME, recorder.getBeanObjectName());
		} finally {
			if (recorder != null)
				recorder.destroy();
		}

		try {
			recorder = recorderFactory.createAccessTimeRecorder(factory, null);
			assertEquals(DEFAULT_BEAN_NAME, recorder.getBeanObjectName());
		} finally {
			if (recorder != null)
				recorder.destroy();
		}

		try {
			recorder = recorderFactory.createAccessTimeRecorder(factory,
					"domain=xxx,name=");
			assertEquals(DEFAULT_BEAN_NAME, recorder.getBeanObjectName());
		} finally {
			if (recorder != null)
				recorder.destroy();
		}
		try {
			recorder = recorderFactory.createAccessTimeRecorder(factory,
					"domain=xxx");
			assertEquals(DEFAULT_BEAN_NAME, recorder.getBeanObjectName());
		} finally {
			if (recorder != null)
				recorder.destroy();
		}

		
	}
	
	@Test
	public void testAccessTimeRecorderIsCreateWithGivenJMXName() {
		JMXAccessTimeRecorder recorder = null;
		try {
			recorder = recorderFactory.createAccessTimeRecorder(factory, "domain=org.greencheck,name=lastaccessedresourceobject");
			assertEquals("org.greencheck:type=lastaccessedresourceobject", recorder.getBeanObjectName());
		} finally {
			if (recorder != null)
				recorder.destroy();
		}
	}
}
