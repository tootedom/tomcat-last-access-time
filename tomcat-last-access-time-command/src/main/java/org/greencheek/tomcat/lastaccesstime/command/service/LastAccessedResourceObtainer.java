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
