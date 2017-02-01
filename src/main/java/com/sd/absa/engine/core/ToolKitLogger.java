package com.sd.absa.engine.core;

import ch.qos.logback.classic.Logger;

public class ToolKitLogger {

	private static Logger tkLogger;
	
	public static void init() {
		
		tkLogger = (Logger) org.slf4j.LoggerFactory.getLogger("nlptoolkit");
	}

	public static Logger getXpLogger() {
		return tkLogger;
	}
}
