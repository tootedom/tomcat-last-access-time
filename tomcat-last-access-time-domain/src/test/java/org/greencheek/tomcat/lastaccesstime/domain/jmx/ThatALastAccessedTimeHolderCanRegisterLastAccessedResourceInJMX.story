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
package org.greencheek.tomcat.lastaccesstime.domain.jmx

import java.lang.management.ManagementFactory

import javax.management.MBeanServer
import javax.management.ObjectName
import javax.management.JMX

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource
import org.greencheek.tomcat.lastaccesstime.domain.jmx.LastAccessedTimeMXBean
import org.greencheek.tomcat.lastaccesstime.domain.jmx.LastAccessTimeHolder
import org.greencheek.tomcat.lastaccesstime.service.GMTLastAccessResourceFactory;
import org.greencheek.tomcat.lastaccesstime.service.ThreadLocalGMTDateFormatter;

scenario "Check that the LastAccessedTime MXBean can be registered with JMX Mbean Server",{
	before "Create a GMT Date Parser and MBeanServer Connection",{
		formatter = new ThreadLocalGMTDateFormatter()
		mbeanServer = ManagementFactory.getPlatformMBeanServer()
		factory = new GMTLastAccessResourceFactory();		
		String beanObjectName ="org.greencheek:type=lastaccessed"
		name = new ObjectName(beanObjectName)
	}
	
	given "A Last Accesss Time Holder object",{
		holder = new LastAccessTimeHolder(factory.createInitialResource())
	}
	
	then "It can be regsitered in the JMX MBean Server", {
		mbeanServer.registerMBean(holder, name)	
	}
	and 
	then "unregsiter from the mbean server", {
		mbeanServer.unregisterMBean(name)
	}
}

scenario "Check that the LastAccessedTime can be retrieved from the JMX Mbean Server via a Proxy",{
	before "Create a GMT Date Parser and MBeanServer Connection",{
		formatter = new ThreadLocalGMTDateFormatter()
		mbeanServer = ManagementFactory.getPlatformMBeanServer()
		factory = new GMTLastAccessResourceFactory();
		beforeTime = new Date()
	}
	
	given "A Last Accesss Time Holder MX Bean object",{
		holder = new LastAccessTimeHolder(factory.createInitialResource());
	}
	
	when "The Last Accessed Time Holder is registered in JMX Server, and the last access time is updated", {
		ObjectName name
	    String beanObjectName ="org.greencheek:type=lastaccessed"
		name = new ObjectName(beanObjectName)
		mbeanServer.registerMBean(holder, name)
		
		holder.setLastAccessTime(factory.createLastAccessResourceFor("GET","/"))
	}
	
	then "A Proxy is able to obtain the last access time object from JMX Server", {
		LastAccessedTimeMXBean proxy = JMX.newMXBeanProxy(mbeanServer,name,LastAccessedTimeMXBean.class,false)
		LastAccessedResource res = proxy.getLastAccessTime()
		
		afterTime = new Date()
		Date now = new Date(res.getTimeAsMillis())
		now.after(beforeTime).shouldBe true
		now.before(afterTime).shouldBe true
	}	
	and
	then "unregsiter from the mbean server", {
		mbeanServer.unregisterMBean(name)
	}
}