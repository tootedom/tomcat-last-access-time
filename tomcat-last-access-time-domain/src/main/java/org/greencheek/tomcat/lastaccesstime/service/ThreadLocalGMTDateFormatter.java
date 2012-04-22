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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ThreadLocalGMTDateFormatter implements DateFormatter {
	public static final String TIMEZONE = "GMT";
	public static final String INITIAL_DATE_STRING = "1970-01-01T00:00:00.000GMT";
	public static final long INITIAL_MILLIS = 0;
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSz"; 
	
    private static final ThreadLocal<DateFormat> DATE_PARSER_THREAD_LOCAL =  new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
        	SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
        	f.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
            return f;
        }

    };
    
    public String format(Date date) {
        return ((DateFormat) DATE_PARSER_THREAD_LOCAL.get()).format(date);
    }
}
