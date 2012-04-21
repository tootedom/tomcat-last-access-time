package org.greencheek.tomcat.lastaccesstime.service;

public interface RequestMatcher {
	void addRequestUris(String requestUris);
	boolean matches(String requestUri);
}
