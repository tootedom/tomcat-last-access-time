package org.greencheek.tomcat.lastaccesstime.command.service;

import javax.management.MBeanServerConnection;

/**
 * Creates a MBeanServerConnection given a port on which the jmx is exposed.
 * 
 * @author dominictootell
 *
 */
public interface MBeanServerConnectionCreator {
	MBeanServerConnection createConnection(String jmxPort);
}
