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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * see http://weblogs.java.net/blog/emcmanus/archive/2007/05/making_a_jmx_co.html
 */

/**
 * Iterates over the iterfaces that are available on the machine on which the JVM is located,
 * and attempts to connect, via rmi, to the jvm; using the connection string:
 * <pre>
 * service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi
 * </pre>
 * Where the first substitution value is the interface address, and the second is the port
 *
 * @author dominictootell
 *
 */
public class TryAllInterfacesMBeanServerConnectionCreator implements MBeanServerConnectionCreator {

	private static final long DEFAULT_JVM_TIMEOUT = 15000;
	private static final long CONNECTION_TIMEOUT = 5000;
	private static final ThreadFactory daemonThreadFactory = new DaemonThreadFactory();
	
	private static final Logger log = LoggerFactory.getLogger(TryAllInterfacesMBeanServerConnectionCreator.class);
	private static String JMX_CONNECTION = "service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi";
	private static List<String> NETWORK_INTERFACE_IP_ADDYS = new ArrayList<String>();
	
	static {
		Enumeration<NetworkInterface> nets=null;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(nets!=null) {
			for (NetworkInterface netint : Collections.list(nets)) {
				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					if(inetAddress instanceof Inet6Address) {
						NETWORK_INTERFACE_IP_ADDYS.add("["+inetAddress.getHostAddress()+"]");
					} else {
						NETWORK_INTERFACE_IP_ADDYS.add(inetAddress.getHostAddress());
					}
				}
			}
		}
		
		// time out for reading properties from jmx
		//		
		//		 Default timeout for read operations.  i.e. no single mbean query over the socket
		//		 should take longer than 15 seconds.  
		//		 see http://download.oracle.com/javase/6/docs/technotes/guides/rmi/sunrmiproperties.html also
		//		 for details on what sun.rmi.transport.tcp.responseTimeout and sun.rmi.transport.tcp.handshakeTimeout means
		//		
		System.setProperty("sun.rmi.transport.tcp.responseTimeout",""+DEFAULT_JVM_TIMEOUT);
		System.getProperty("sun.rmi.transport.tcp.handshakeTimeout",""+DEFAULT_JVM_TIMEOUT);
		
    }
	
    
	public MBeanServerConnection createConnection(String jmxPort) {
		MBeanServerConnection con = null;
		for(String ip : NETWORK_INTERFACE_IP_ADDYS) {
			String jmxurl = String.format(JMX_CONNECTION, new String[]{ip,jmxPort});
			log.info("Trying jmxurl: {}",jmxurl);
			con = createMBeanServerConnection(jmxurl);
			if(con!=null) {
				break;
			}			
		}		
		return con;		
	}
	
	private MBeanServerConnection createMBeanServerConnection(final String connectorAddress) {
		final BlockingQueue<Object> mailbox = new ArrayBlockingQueue<Object>(1);
		
		
		
		final JMXServiceURL url;
		try {
			url = new JMXServiceURL(connectorAddress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
	
		ExecutorService executor = Executors
				.newSingleThreadExecutor(daemonThreadFactory);

		try {
			executor.submit(new Runnable() {
				public void run() {
					try {
						JMXConnector connector = JMXConnectorFactory.connect(url);
						if (!mailbox.offer(connector))
							connector.close();
					} catch (Throwable t) {
						mailbox.offer(t);
					}
				}
			});
		}
		catch (RejectedExecutionException e) {
			log.error("Unable to attempt to connect to {}",connectorAddress);
			return null;
		}
			
		
		Object result;
		try {
		    result = mailbox.poll(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
		    if (result == null) {
			if (!mailbox.offer(""))
			    result = mailbox.take();
		    }
		} catch (InterruptedException e) {
		    mailbox.offer("");
		    return null;
		} finally {
		    executor.shutdown();
		}
		
		
		if (result == null) {
			log.info("Unable to connect to jvm using jmx connection, timeout {}",connectorAddress);
			return null;
		}
		
		if (!(result instanceof JMXConnector)) {
			log.info("Exception when attempting to connect to jvm using jmx connection {}",connectorAddress);
			return null;
		}
		
		JMXConnector connectorObj =  (JMXConnector) result;
		
	    try {
	        MBeanServerConnection mbeanConn = connectorObj.getMBeanServerConnection();
	        return mbeanConn;
	    } catch (IOException e) {
	    	log.info("Unable to obtain MBeanServerConnection from JMXConnector");
	    	return null;
		} 
	}

	private static class DaemonThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
		    Thread t = Executors.defaultThreadFactory().newThread(r);
		    t.setDaemon(true);
		    return t;
		}
	}
	    

}
