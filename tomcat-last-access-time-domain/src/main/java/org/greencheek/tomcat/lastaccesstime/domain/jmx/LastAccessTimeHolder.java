package org.greencheek.tomcat.lastaccesstime.domain.jmx;

import java.util.concurrent.atomic.AtomicReference;

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;

public class LastAccessTimeHolder implements LastAccessedTimeMXBean {
	
	private AtomicReference<LastAccessedResource> lastAccessed = new AtomicReference<LastAccessedResource>();
	
	public LastAccessTimeHolder(LastAccessedResource initialResource) {
		lastAccessed.set(initialResource);
	}
	
	public void setLastAccessTime(LastAccessedResource accessTime) {
		lastAccessed.set(accessTime);
	}
	
	public LastAccessedResource getLastAccessTime() {
		return lastAccessed.get();
	}
	
	

}
