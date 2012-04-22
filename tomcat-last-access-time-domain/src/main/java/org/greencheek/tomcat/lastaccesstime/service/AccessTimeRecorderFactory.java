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

/**
 * Responsible for creating a AccessTimeRecorder.  At the moment the only 
 * recorder is that of a JMX recorder; where last accessed resources are 
 * recorded within jmx.
 * 
 * @author dominictootell
 *
 */
public interface AccessTimeRecorderFactory {
	/**
	 * Create a AccessTimeRecorder that takes the given identifier and understands
	 * the type of AccessTimeRecorder to create.  The identifier is specific to the 
	 * AccessTimeRecorder that is being created.
	 * 
	 * @param resourceFactory The resource factory that is queried for an initial resource
	 *                        that is potentially used during the construction of the AccessTimeRecorder object
	 *                        returned to the called
	 *                         
	 * @param identifier Is specific to the AccessTimeRecorder that is being created 
	 *                   and provided information relating to how that implementation should be created
	 * @return
	 */
	AccessTimeRecorder createAccessTimeRecorder(LastAccessResourceFactory resourceFactory, String identifier);
}
