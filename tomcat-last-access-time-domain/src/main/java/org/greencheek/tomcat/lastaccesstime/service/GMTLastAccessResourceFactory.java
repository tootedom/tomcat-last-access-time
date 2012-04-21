package org.greencheek.tomcat.lastaccesstime.service;

import java.util.Date;

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;

public class GMTLastAccessResourceFactory implements LastAccessResourceFactory {

	public static final String INITIAL_REQUEST_METHOD = "OPTIONS";
	public static final String INITIAL_REQUESTED_RESOURCE = "<clinit>"; 
	
	public static final DateFormatter formatter = new ThreadLocalGMTDateFormatter();
	
	public LastAccessedResource createInitialResource() {		
		return new LastAccessedResource(ThreadLocalGMTDateFormatter.INITIAL_DATE_STRING,
										ThreadLocalGMTDateFormatter.INITIAL_MILLIS,
										INITIAL_REQUEST_METHOD,INITIAL_REQUESTED_RESOURCE);
	}

	public LastAccessedResource createLastAccessResourceFor(String method, String resource) {
		Date now = new Date();
		return new LastAccessedResource(formatter.format(now), 
										now.getTime(), method,resource);
	}

}
