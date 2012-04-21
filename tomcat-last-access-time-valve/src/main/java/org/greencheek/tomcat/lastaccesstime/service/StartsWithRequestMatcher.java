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
