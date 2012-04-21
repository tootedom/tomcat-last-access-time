package org.greencheek.tomcat.lastaccesstime.service

scenario "a date object can be converted to a gmt string object",{
	before "Create a GMT Date Parser",{
		formatter = new ThreadLocalGMTDateFormatter();
	}
	
	given "A Date of January 1, 1970, 00:00:00 GMT",{
		beginningOfComputerTime = new Date(0);
	}
	
	when "The Date object is format to a String", {
		formattedDate = formatter.format(beginningOfComputerTime)
	}
	
	then "It is represented as: yyyy-MM-dd'T'HH:mm:ss.SSSz (1970-01-01T00:00:00.000GMT)", {
		formattedDate.shouldBeEqualTo "1970-01-01T00:00:00.000GMT"
	}
}