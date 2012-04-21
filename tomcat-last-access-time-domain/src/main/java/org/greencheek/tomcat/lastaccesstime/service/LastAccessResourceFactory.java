package org.greencheek.tomcat.lastaccesstime.service;

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;

public interface LastAccessResourceFactory {
	LastAccessedResource createInitialResource();
	LastAccessedResource createLastAccessResourceFor(String method, String resource);
}
