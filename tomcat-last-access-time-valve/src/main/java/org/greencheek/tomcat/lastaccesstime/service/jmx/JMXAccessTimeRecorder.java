package org.greencheek.tomcat.lastaccesstime.service.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory; 

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;
import org.greencheek.tomcat.lastaccesstime.domain.jmx.LastAccessTimeHolder;
import org.greencheek.tomcat.lastaccesstime.service.AccessTimeRecorder;

public class JMXAccessTimeRecorder implements AccessTimeRecorder {

	/**
	 * Logger
	 */
	private static final Log log = LogFactory.getLog(JMXAccessTimeRecorder.class);
	
	public static final String DEFAULT_JMX_DOMAIN_NAME="org.greencheek";
	public static final String DEFAULT_JMX_BEAN_NAME="last_accessed";
	
	private static MBeanServer server = ManagementFactory.getPlatformMBeanServer();
	private final LastAccessTimeHolder lastAccessMBeanObject;
	private final String beanObjectName;
	private final boolean registeredInJmx;
	private final ObjectName jmxRegisteredObjectName;
	
	public JMXAccessTimeRecorder(LastAccessedResource initialResource, String jmxDomainName, String beanName) {
		lastAccessMBeanObject = new LastAccessTimeHolder(initialResource);
	    beanObjectName = jmxDomainName+":type="+beanName;
	    boolean registered = false;
	    ObjectName objName = null;
		try {
			objName = new ObjectName(getBeanObjectName());
			server.registerMBean(lastAccessMBeanObject, objName);
			registered = true;
		} catch (MalformedObjectNameException e) {
			if(log.isWarnEnabled()) {
				log.warn("Unable to register with jmx, invalid bean name:" + getBeanObjectName(), e);
			}			
		} catch (NullPointerException e) {
			if(log.isWarnEnabled()) {
				log.warn("Unable to register with jmx, invalid bean name:" + getBeanObjectName(), e);
			}
		} catch (InstanceAlreadyExistsException e) {
			if(log.isWarnEnabled()) {
				log.warn("Unable to register with jmx, bean already exists:" + getBeanObjectName(), e);
			}
		} catch (MBeanRegistrationException e) {
			if(log.isWarnEnabled()) {
				log.warn("Unable to register with jmx, exception during registration:" + getBeanObjectName(), e);
			}
		} catch (NotCompliantMBeanException e) {
			if(log.isWarnEnabled()) {
				log.warn("Unable to register with jmx, bean is not compatible:" + getBeanObjectName(), e);
			}
		} finally {
			registeredInJmx = registered;
			jmxRegisteredObjectName = objName;
		}
		
	}
	
	public JMXAccessTimeRecorder(LastAccessedResource initialResource)  {
		this(initialResource,DEFAULT_JMX_DOMAIN_NAME,DEFAULT_JMX_BEAN_NAME);
	}
	
	public void recordAccesss(LastAccessedResource time) {
		lastAccessMBeanObject.setLastAccessTime(time);
	}

	public String getBeanObjectName() {
		return beanObjectName;
	}

	public void init() {
				
	}

	public void destroy() {
		if(isRegisteredInJmx()) {
			try {
				server.unregisterMBean(getJmxRegisteredObjectName());
			} catch (MBeanRegistrationException e) {
				// TODO Auto-generated catch block
				if(log.isWarnEnabled()) {
					log.warn("Unable to unregister object with jmx:" + getBeanObjectName(), e);
				}			
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				if(log.isWarnEnabled()) {
					log.warn("Unable to unregister object with jmx, object not registered:" + getBeanObjectName(), e);
				}
			}
		}
	}

	public boolean isRegisteredInJmx() {
		return registeredInJmx;
	}

	public ObjectName getJmxRegisteredObjectName() {
		return jmxRegisteredObjectName;
	}

}
