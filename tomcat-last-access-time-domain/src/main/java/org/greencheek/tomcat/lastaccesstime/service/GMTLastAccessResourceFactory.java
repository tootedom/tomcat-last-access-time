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
