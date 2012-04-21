package org.greencheek.tomcat.lastaccesstime.domain.jmx;

import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;

public interface LastAccessedTimeMXBean {
	LastAccessedResource getLastAccessTime();
}
