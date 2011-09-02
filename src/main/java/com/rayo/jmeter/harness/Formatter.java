package com.rayo.jmeter.harness;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

public class Formatter {

	public static String format(String message) {
		
		return format(message, Thread.currentThread().getName());
	}
	
	public static String format(String message, String thread) {
		
		return String.format("[%s] - %s : %s",DateFormatUtils.format(new Date(), "hh:mm:ss.SSS"),
				thread,message);
	}
}
