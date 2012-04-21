package org.greencheek.tomcat.lastaccesstime.service;

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;

public interface AccessTimeRecorder {
	/**
	 * Perform any post construction operations in this
	 * method
	 */
	public void init();
	
	/**
	 * Perform any tear down operations in this.
	 */
	public void destroy();
	
	/**
	 * Persists the last time a resource was accessed.
	 * 
	 * @param time
	 */
	public void recordAccesss(LastAccessedResource time);
}
