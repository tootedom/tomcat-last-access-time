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
