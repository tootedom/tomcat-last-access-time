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
