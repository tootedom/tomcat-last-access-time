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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartsWithRequestMatcher implements RequestMatcher {

	private final List<String> ignorePaths = new ArrayList<String>();	
	
	public void addRequestUris(String paths) {
		if(paths==null) return;
		String[] ignores =  paths.split(",");
		for(String s : ignores) {
			s = s.trim();
			if(s.length()>0) ignorePaths.add(s);
		}
	}
	
	private List<String> getRequestUris() {
		return ignorePaths;
	}
	
	public boolean matches(String requestUri) {
		boolean found = false;
		for(String s : getRequestUris()) {
			if(requestUri.startsWith(s)) { found=true; break;} 
		}
		return found;
	}

}
