package org.greencheek.tomcat.lastaccesstime.valve;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;
import org.greencheek.tomcat.lastaccesstime.domain.jmx.LastAccessedTimeMXBean;
import org.greencheek.tomcat.lastaccesstime.service.GMTLastAccessResourceFactory;
import org.greencheek.tomcat.lastaccesstime.service.LastAccessResourceFactory;
import org.greencheek.tomcat.lastaccesstime.service.jmx.JMXAccessTimeRecorder;
import org.greencheek.tomcat.lastaccesstime.service.jmx.JMXAccessTimeRecorderFactory;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LastAccessValueTest {
    Mockery context = new Mockery() {{    	 
    	   setImposteriser(ClassImposteriser.INSTANCE);    	   
    }};
    
	private MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

	private LastAccessValve valve;
	private LastAccessResourceFactory factory;
	private Request request;
	private Response response;
	private Valve nextValve;
	
	@Before 
	public void setUp() {
		valve = new LastAccessValve();		
		factory = new GMTLastAccessResourceFactory();
		request = context.mock(Request.class);
		response = context.mock(Response.class);
		nextValve = context.mock(Valve.class);
	}
	
	@After
	public void tearDown() {
		try {
			valve.stop();
		} catch(LifecycleException e) {
		
		} finally {
			
		}
	}
	
	private void setupMockContext(final String method, final String uri, final String queryString) {
		context.checking(new Expectations() {{
		    oneOf (request).getMethod(); will(returnValue(method));
		    oneOf (request).getRequestURI(); will(returnValue(uri));
		    oneOf (request).getQueryString(); will(returnValue(queryString));
		    try {
		    	oneOf (nextValve).invoke(request, response);
		    } catch(Exception e) { }
 		}});
		
	}
	
	private void startValve() throws LifecycleException {
		valve.setNext(nextValve);
		valve.setJmxName("domain=org.greencheek,name=lastaccessedresource");
		valve.start();
		
	}
	
	@Test
	public void testSettingJMXName() throws LifecycleException {

		valve.setJmxName("xxx");
		valve.start();

		valve.recordAccessTime("GET", "/");		
	}
	
	@Test
	public void testValueInvocation() throws MalformedObjectNameException, NullPointerException, InterruptedException, LifecycleException {

		
		startValve();
		
		Date before = new Date();	
		Thread.sleep(10);		
		
		setupMockContext("GET","/status.jsp",null);
		
		
		try {
			valve.invoke(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.assertIsSatisfied();
		Thread.sleep(10);
		Date after = new Date();
		
		ObjectName name = new ObjectName("org.greencheek:type=lastaccessedresource");
		LastAccessedTimeMXBean proxy = JMX.newMXBeanProxy(mbeanServer,name,LastAccessedTimeMXBean.class,false);
		
		LastAccessedResource resource = proxy.getLastAccessTime();
		System.out.println(resource);
		Date date = new Date(resource.getTimeAsMillis());		
		assertTrue((date.after(before) && date.before(after)));
		
	}
	
	
	
	@Test
	public void testValueInvocationIncludesQueryPath() throws MalformedObjectNameException, NullPointerException, InterruptedException, LifecycleException {
				
		startValve();
		
		setupMockContext("GET","/status.jsp","item=one&item2=two");

		
		try {
			valve.invoke(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.assertIsSatisfied();
		
		ObjectName name = new ObjectName("org.greencheek:type=lastaccessedresource");
		LastAccessedTimeMXBean proxy = JMX.newMXBeanProxy(mbeanServer,name,LastAccessedTimeMXBean.class,false);
		
		LastAccessedResource resource = proxy.getLastAccessTime();
		assertTrue(resource.toString().contains("item=one&item2=two"));				
	}
	
	@Test
	public void testValueInvocationAndAccessNotRecordedForIgnoredResource() throws MalformedObjectNameException, NullPointerException, InterruptedException, LifecycleException {
		
		
		valve.setIgnoreRequestUris("/status.jsp");
		
		startValve();
		
		Date before = new Date();	
		Thread.sleep(10);		
		
		setupMockContext("GET","/status.jsp",null);
		
		try {
			valve.invoke(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.assertIsSatisfied();
		Thread.sleep(10);
		Date after = new Date();
		
		ObjectName name = new ObjectName("org.greencheek:type=lastaccessedresource");
		LastAccessedTimeMXBean proxy = JMX.newMXBeanProxy(mbeanServer,name,LastAccessedTimeMXBean.class,false);
		
		LastAccessedResource resource = proxy.getLastAccessTime();
		System.out.println(resource);
		Date date = new Date(resource.getTimeAsMillis());		
		assertTrue((date.before(before)));
		
		
		setupMockContext("GET","/status2.jsp",null);

		
		try {
			valve.invoke(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.assertIsSatisfied();
		Thread.sleep(10);
		after = new Date();
		
		
		resource = proxy.getLastAccessTime();
		System.out.println(resource);
		date = new Date(resource.getTimeAsMillis());		
		assertTrue((date.after(before) && date.before(after)));
	}
	
	
	@Test(expected=org.apache.catalina.LifecycleException.class)
	public void testValueInvocationNotStarted() throws MalformedObjectNameException, NullPointerException, InterruptedException, LifecycleException {
		
		valve.setNext(nextValve);
		valve.setJmxName("domain=org.greencheek,name=lastaccessedresource");
		// should start the value, but will not to test throw exception
		
		setupMockContext("GET","/status.jsp",null);

		
		try {
			valve.invoke(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		context.assertIsSatisfied();
		
		// This will throw an exception
		valve.stop();
		
		
	}
	
	@Test
	public void testValueInvocationNotStartMeansAccessResourceIsNotRecorded() throws MalformedObjectNameException, NullPointerException, InterruptedException, LifecycleException {
		
		valve.setNext(nextValve);
		JMXAccessTimeRecorderFactory factory = new JMXAccessTimeRecorderFactory();
		JMXAccessTimeRecorder recorder = factory.createAccessTimeRecorder(new GMTLastAccessResourceFactory(), "domain=org.greencheek,name=lastaccessedresource");
		recorder.init();
		
		Date before = new Date();	
		Thread.sleep(10);		
		
		setupMockContext("GET","/status.jsp",null);		
		
		try {
			valve.invoke(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.assertIsSatisfied();
		Thread.sleep(10);
		
		ObjectName name = new ObjectName("org.greencheek:type=lastaccessedresource");
		LastAccessedTimeMXBean proxy = JMX.newMXBeanProxy(mbeanServer,name,LastAccessedTimeMXBean.class,false);
		
		LastAccessedResource resource = proxy.getLastAccessTime();
		System.out.println(resource);
		Date date = new Date(resource.getTimeAsMillis());		
		assertTrue((date.before(before)));
		recorder.destroy();
	}
}
