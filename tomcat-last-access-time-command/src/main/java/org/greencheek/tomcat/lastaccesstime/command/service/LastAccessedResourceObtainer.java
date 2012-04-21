package org.greencheek.tomcat.lastaccesstime.command.service;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;

/**
 * Returns a local LastAccessResource instance from the given MBeanServerConnection
 * 
 * @author dominictootell
 *
 */
public interface LastAccessedResourceObtainer {
	public static LastAccessedResource NOT_FOUND = new LastAccessedResource("NOT_FOUND",0, "NOT_FOUND", "NOT_FOUND");
	
	/**
	 * Returns the LastAccessedReource or the NOT_FOUND object above.  Must return a local instance,
	 * and not a proxy object that might error when method calls are made.
	 * 
	 * @param connection The connection to the MBeanServerConnection
	 * @param beanName The jmx bean name under which the LastAccessedResource is stored.
	 * 
	 * @return
	 */
	public LastAccessedResource getLastAccessedResource(MBeanServerConnection connection,ObjectName beanName);
}
