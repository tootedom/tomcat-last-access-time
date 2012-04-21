package org.greencheek.tomcat.lastaccesstime.valve;

import java.io.IOException;


import javax.servlet.ServletException;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.util.StringManager;
import org.apache.catalina.valves.Constants;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import org.greencheek.tomcat.lastaccesstime.service.AccessTimeRecorder;
import org.greencheek.tomcat.lastaccesstime.service.AccessTimeRecorderFactory;
import org.greencheek.tomcat.lastaccesstime.service.GMTLastAccessResourceFactory;
import org.greencheek.tomcat.lastaccesstime.service.LastAccessResourceFactory;
import org.greencheek.tomcat.lastaccesstime.service.RequestMatcher;
import org.greencheek.tomcat.lastaccesstime.service.StartsWithRequestMatcher;
import org.greencheek.tomcat.lastaccesstime.service.jmx.JMXAccessTimeRecorder;
import org.greencheek.tomcat.lastaccesstime.service.jmx.JMXAccessTimeRecorderFactory;


public class LastAccessValve extends ValveBase implements Valve, Lifecycle {

	/**
	 * Logger
	 */
	private static final Log log = LogFactory.getLog(LastAccessValve.class);
	
	/**
	* The descriptive information related to this implementation.
	*/
	private static final String info = "org.greencheek.tomcat.lastaccesstime.valve.LastAccessValve/1.0";
	/**
	 * The string manager for this package.
	 */
	private StringManager sm = StringManager.getManager("org.greencheek.tomcat.lastaccesstime.valve");
	
	private LastAccessResourceFactory factory = new GMTLastAccessResourceFactory();
	private RequestMatcher requestUriMatcher = new StartsWithRequestMatcher();
	private AccessTimeRecorderFactory accessTimeRecorderFactory = new JMXAccessTimeRecorderFactory();
	private AccessTimeRecorder accessTimeRecorder = null;
	
	private boolean accessTimeRecorderCreated = false;
	
	/**
	* The lifecycle event support for this component.
	*/
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	 	
	/**
	* Has this component been started yet?
	*/
	protected boolean started = false; 
	
	/**
	 * The name under which to store the last access time in jmx
	 */
	private String jmxName;
	

	/**
	 * String in the format :  domain=xxx,name=xxx
	 * @param name
	 */
	public void setJmxName(String name) {
		this.jmxName = name;
	}
	
	public String getJmxName() {
		return this.jmxName;
	}
	
	

	public void setIgnoreRequestUris(String paths) {
		requestUriMatcher.addRequestUris(paths);
	}
	
	public void invoke(Request request, Response response)
			throws ServletException, IOException {

		try {
			String method = request.getMethod();
			String path = request.getRequestURI(); 						
			StringBuilder requestedResource = new StringBuilder(path.length());			
			requestedResource.append(path);
		    String queryString = request.getQueryString();          
		    
		    // Reconstruct original requesting URL
		    if (queryString != null) {
		    	requestedResource.append('?');
		    	requestedResource.append(queryString);
		    }
		    
		    path = requestedResource.toString();
						
			// record access for non ignored paths
			if(!requestUriMatcher.matches(path)) recordAccessTime(method,path);
						
			getNext().invoke(request, response);

		}  finally {
		}

	}
	
	void recordAccessTime(String method, String requestedResource) {
		if(accessTimeRecorderCreated) {
			accessTimeRecorder.recordAccesss(factory.createLastAccessResourceFor(method,requestedResource));
		}
	}

	/**
	 * Add a lifecycle event listener to this component.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addLifecycleListener(LifecycleListener listener) {
		lifecycle.addLifecycleListener(listener);
	}

	/**
	 * Get the lifecycle listeners associated with this lifecycle. If this
	 * Lifecycle has no listeners registered, a zero-length array is returned.
	 */
	public LifecycleListener[] findLifecycleListeners() {
		return lifecycle.findLifecycleListeners();
	}

	/**
	 * Remove a lifecycle event listener from this component.
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void removeLifecycleListener(LifecycleListener listener) {
		lifecycle.removeLifecycleListener(listener);
	}

	public void start() throws LifecycleException {
		if(log.isInfoEnabled()) {
			log.info("Starting LastAccessValve");
		}
				
		// Validate and update our current component state
		if (started)
			throw new LifecycleException(sm.getString("lastAccessValve.alreadyStarted"));
		
		lifecycle.fireLifecycleEvent(START_EVENT, null);
		
		started = true;
		
		accessTimeRecorder =  getAccessTimeRecorderFactory().createAccessTimeRecorder(factory, getJmxName());
		accessTimeRecorder.init();		
		accessTimeRecorderCreated=true;

		
	}

	public void stop() throws LifecycleException {
		if(log.isInfoEnabled()) {
			log.info("Stopping LastAccessValve");
		}
		
		
		if (!started)
			throw new LifecycleException(sm.getString("lastAccessValve.notStarted"));
		
		started = false;
		accessTimeRecorder.destroy();
	}

	
	AccessTimeRecorderFactory getAccessTimeRecorderFactory() {
		return accessTimeRecorderFactory;
	}

	void setAccessTimeRecorderFactory(AccessTimeRecorderFactory accessTimeRecorderFactory) {
		this.accessTimeRecorderFactory = accessTimeRecorderFactory;
	}
	
	
}
