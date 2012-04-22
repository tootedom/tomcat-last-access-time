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
