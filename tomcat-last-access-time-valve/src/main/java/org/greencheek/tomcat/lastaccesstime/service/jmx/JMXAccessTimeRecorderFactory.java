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

package org.greencheek.tomcat.lastaccesstime.service.jmx;

import org.greencheek.tomcat.lastaccesstime.service.AccessTimeRecorderFactory;
import org.greencheek.tomcat.lastaccesstime.service.LastAccessResourceFactory;

/**
 * Creates a JMXAccessTimeRecorder that will register with the local MBeanServer
 * the LastAccessResource under a given jmx domain and name.
 * 
 * @author dominictootell
 *
 */
public class JMXAccessTimeRecorderFactory implements AccessTimeRecorderFactory {

	/**
	 * Create a JMXAccessTimeRecorders that takes the given jmx identifier in the form:
	 * domain=xxxx,name=xxx 
	 * 
	 * The JMXAccessTimeRecorder will register in the local JMX MBean Server the LastAccessedResource; under the
	 * given jmx domain and name.
	 * 
	 * @param resourceFactory The resource factory that is queried for an initial resource
	 *                        that is potentially used during the construction of the AccessTimeRecorder object
	 *                        returned to the called
	 *                         
	 * @param identifier The jmx domain and name under which to store the LastAccessedResource.  The format is: domain=xxx,name=xxx
	 *                   for example:  domain=org.greencheek,name=lastaccessed
	 * @return
	 */
	public JMXAccessTimeRecorder createAccessTimeRecorder(LastAccessResourceFactory resourceFactory,String identifier) {
	
		if(identifier==null) return createDefaultJMXAccessTimeRecorder(resourceFactory);
		String[] domainAndName = identifier.split(",");
		if(domainAndName.length<2) return createDefaultJMXAccessTimeRecorder(resourceFactory);
		String jmxdomain = getValue(domainAndName[0]);
		String jmxname = getValue(domainAndName[1]);
		if(jmxname == null || jmxdomain==null) return createDefaultJMXAccessTimeRecorder(resourceFactory);
		
		return new JMXAccessTimeRecorder(resourceFactory.createInitialResource(),jmxdomain,jmxname);
	}
	
	/**
	 * Gets the value from a key/pair that is separated by a '=', i.e.. key=value
	 * @param keyValPair The string that is the key pair
	 * @return null if no value is found, or the key/pair isn't a '=' separated value.
	 */
	private String getValue(String keyValPair) {
		String[] keyAndValue = keyValPair.split("=");
		if(keyAndValue.length<2) return null;
		if(keyAndValue[1].trim().length()==0) return null;		
		return keyAndValue[1];
	}
	
	/**
	 * Creates a default JMXAccessTimeRecorder that will store the LastAccessedResource under the domain: {@value JMXAccessTimeRecorder#DEFAULT_JMX_DOMAIN_NAME}
	 * with the name: {@value JMXAccessTimeRecorder#DEFAULT_JMX_BEAN_NAME}
	 * 
	 * @param factory
	 * @return
	 */
	private JMXAccessTimeRecorder createDefaultJMXAccessTimeRecorder(LastAccessResourceFactory factory) {
		return new JMXAccessTimeRecorder(factory.createInitialResource());
	}
	
	

}
