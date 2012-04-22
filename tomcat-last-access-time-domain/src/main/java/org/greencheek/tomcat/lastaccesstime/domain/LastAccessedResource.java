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

package org.greencheek.tomcat.lastaccesstime.domain;

import java.beans.ConstructorProperties;

/**
 * Represents the Last time that a resource on the server was accessed via a web request.
 * Has properties for recording:
 * <ul>
 * <li>Resource uri</li>
 * <li>Request HTTP Method</li>
 * <li>Time in as a String</li>
 * <li>Time in millis (since epoch, in relation to the date string)</li>
 * </ul>
 * 
 * @author dominictootell
 *
 */
public class LastAccessedResource {
	private final String lastAccessedTimeAsString;
	private final long lastAccessedTimeInMillis;
	private final String lastRequestedResource;
	private final String lastRequestMethod;
	
	
	@ConstructorProperties({"timeAsString", "timeAsMillis","requestMethod","requestedResource"})
	public LastAccessedResource(String timeAsString, long timeAsMillis , 
			String requestMethod, String requestedResource) {
		lastAccessedTimeInMillis = timeAsMillis;	
		lastAccessedTimeAsString = timeAsString;
		lastRequestMethod = requestMethod;
		lastRequestedResource = requestedResource;
	}
	
	
	public String getTimeAsString() {
		return lastAccessedTimeAsString;
	}

	public long getTimeAsMillis() {
		return lastAccessedTimeInMillis;
	}

	public String getRequestMethod() {
		return lastRequestMethod;
	}

	public String getRequestedResource() {
		return lastRequestedResource;
	}
	
	
	public String toString() {
		StringBuilder b = new StringBuilder(lastRequestMethod).append(':');
		b.append(lastRequestedResource).append('@').append(lastAccessedTimeAsString);
		b.append('(').append(lastAccessedTimeInMillis).append(')');
		return b.toString();
	}

}
