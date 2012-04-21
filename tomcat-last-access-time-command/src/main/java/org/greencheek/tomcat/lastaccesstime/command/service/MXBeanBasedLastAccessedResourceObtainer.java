package org.greencheek.tomcat.lastaccesstime.command.service;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;
import org.greencheek.tomcat.lastaccesstime.domain.jmx.LastAccessedTimeMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses a MXBean proxy to access the current LastAccessedResource from the 
 * jmx MBean Server
 * @author dominictootell
 *
 */
public class MXBeanBasedLastAccessedResourceObtainer implements
		LastAccessedResourceObtainer {

	private static Logger log = LoggerFactory.getLogger(MXBeanBasedLastAccessedResourceObtainer.class);
	
	public LastAccessedResource getLastAccessedResource(
			MBeanServerConnection connection, ObjectName beanName) {
		
		LastAccessedTimeMXBean proxy = JMX.newMXBeanProxy(connection,beanName,LastAccessedTimeMXBean.class,false);
		
		try {
			LastAccessedResource resource = proxy.getLastAccessTime();			
			String timeAsString = resource.getTimeAsString();
			long timeAsMillis = resource.getTimeAsMillis();
			String requestMethod = resource.getRequestMethod();
			String requestedResource = resource.getRequestedResource();			
			LastAccessedResource nonProxy = new LastAccessedResource(timeAsString, timeAsMillis, requestMethod, requestedResource);
			return nonProxy;			
		} catch (Exception e) {
			log.error("Unable to access values in proxy object",e);
			return NOT_FOUND;
		}
		
	}

}
