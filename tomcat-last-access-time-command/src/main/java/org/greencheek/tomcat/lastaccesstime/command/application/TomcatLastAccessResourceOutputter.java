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
package org.greencheek.tomcat.lastaccesstime.command.application;

import java.net.URI;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.greencheek.tomcat.lastaccesstime.command.service.LastAccessedResourceObtainer;
import org.greencheek.tomcat.lastaccesstime.command.service.MBeanServerConnectionCreator;
import org.greencheek.tomcat.lastaccesstime.command.service.MXBeanBasedLastAccessedResourceObtainer;
import org.greencheek.tomcat.lastaccesstime.command.service.TryAllInterfacesMBeanServerConnectionCreator;
import org.greencheek.tomcat.lastaccesstime.domain.LastAccessedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//pgrep -f '^.*catalina.home=/usr/local/apache-tomcat-.*org.apache.catalina.startup.Bootstrap start' | sed ':a;N;$!ba;s/\n/,/g'
//ps -ef | grep '^.*catalina.home=/usr/local/apache-tomcat-.*org.apache.catalina.startup.Bootstrap start' | grep -v 'grep' | sed -e 's/.*port=\(11[0-9][0-9][0-9]\).*/\1/' | sed ':a;N;$!ba;s/\n/,/g'

public class TomcatLastAccessResourceOutputter {
	
	private static Pattern COMMA_REPLACEMENT = Pattern.compile(",", Pattern.LITERAL);
	private static Logger log = LoggerFactory.getLogger(TomcatLastAccessResourceOutputter.class);
	
	private static final String CATALINA_BASE_DIR_ATTR_NAME = "baseDir";
	private static final ObjectName catalinaObjectName;
	static {
		ObjectName cat = null;
		try {
			 cat = new ObjectName("Catalina:type=Engine");
		} catch (Exception e) {
		} finally {
			catalinaObjectName = cat;
		}
	}
	
	public static void main(String[] args) {
		
		if(args.length<2) {
			System.out.println("Invalid Number of Arguments:");
			System.out.println("port[,port] lastAccessResourceJmxObjectName");
			System.out.println();
			System.out.println("For Example:");
			System.out.println("<prog> 11054,11055 org.greencheek:type=last_accessed");
			System.exit(-1);
		}

		ObjectName lastAccessResourceBeanName = null;
		try {
			lastAccessResourceBeanName = new ObjectName(args[1]);
		} catch(Exception e) {
			System.out.println("Invalid BeanName:" + args[1]);
			System.out.println("port[,port] lastAccessResourceJmxObjectName");
			System.out.println();
			System.out.println("For Example:");
			System.out.println("<prog> 11054,11055 org.greencheek:type=last_accessed");
			System.exit(-1);
		}
		
		
		MBeanServerConnectionCreator conn = new TryAllInterfacesMBeanServerConnectionCreator();
		LastAccessedResourceObtainer resource = new MXBeanBasedLastAccessedResourceObtainer();
		for(String port : args[0].split(",")) {
			MBeanServerConnection c = conn.createConnection(port);
			if(c!=null ) {
				if(catalinaObjectName!=null) {
					String catalinaBaseDir = "";
					try {
						String s = (String)c.getAttribute(catalinaObjectName, CATALINA_BASE_DIR_ATTR_NAME);
						catalinaBaseDir = s;
					} catch (Exception e) {
						
					}
					
					LastAccessedResource r = resource.getLastAccessedResource(c, lastAccessResourceBeanName);
					
					
					StringBuilder b = new StringBuilder(catalinaBaseDir);
					if(catalinaBaseDir!=null) {
						b.append(',');
					}
					
					b.append(r.getTimeAsMillis()).append(',').append(r.getTimeAsString()).append(',');
					
					try {
						URI uri = new URI("http",null,r.getRequestedResource(),null);
						String path = COMMA_REPLACEMENT.matcher(uri.getPath()).replaceAll("%2c");
						StringBuilder encodedPath = new StringBuilder(path);
						
						String query = uri.getQuery();
						if(query!=null && query.trim().length()!=0) {
							encodedPath.append('?');
							encodedPath.append(COMMA_REPLACEMENT.matcher(query).replaceAll("%2c"));
						}
						b.append(r.getRequestMethod()).append(',').append(encodedPath.toString());
					} catch(Exception e) {}
					
					log.info(b.toString());
				}
			}
		}
			
	}

}
