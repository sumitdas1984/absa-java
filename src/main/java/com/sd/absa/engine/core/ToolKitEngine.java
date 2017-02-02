/**
 *
 */
package com.sd.absa.engine.core;

import ch.qos.logback.classic.Logger;
import com.sd.absa.engine.config.ToolKitConfig;
import com.sd.absa.textanalytics.CoreNLPController;
import com.sd.absa.utils.FileIO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Koustuv Saha 
 * 10-Mar-2014 2:27:11 pm 
 * XpressoV2 XPengine
 */
public class ToolKitEngine {

	private static boolean interactiveMode = false;
	private static boolean mlMode = false;

	private static Logger logger;

	private static String CURRENT_DATE_STR = null;
	private static String outputFileName;

	public static String mode;
	public static void setOutputFileName(String fileName) {
		outputFileName = fileName;
	}

	public static String getOutputFileName() {
		return outputFileName;
	}

	public static String getCurrentDateStr() {
		return CURRENT_DATE_STR;
	}

	public static boolean getMachineLearningMode() {
		return mlMode;
	}

	public static void init(String... varargs) {

		String propsFile = "config/xpressoConfig.properties";

		double startTime = System.currentTimeMillis();

		try {
			ToolKitConfig.init(propsFile, varargs);
			ToolKitLogger.init();
			logger = ToolKitLogger.getXpLogger();

			SimpleDateFormat sdf = new SimpleDateFormat(ToolKitConfig.DATE_FORMAT);
			CURRENT_DATE_STR = sdf.format(Calendar.getInstance().getTime());

			/*---------------*/
			double time0 = System.currentTimeMillis();
			CoreNLPController.init();
			double time1 = System.currentTimeMillis();
			System.out.println("CoreNLPController Loaded: " + (time1 - time0) / 1000 + " seconds");

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Time Taken to execute the Initialization: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
	}

}