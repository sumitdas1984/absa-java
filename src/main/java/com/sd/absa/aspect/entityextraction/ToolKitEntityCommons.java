package com.sd.absa.aspect.entityextraction;

import com.sd.absa.engine.config.ToolKitConfig;
import com.sd.absa.engine.core.ToolKitEngine;
import com.sd.absa.utils.CollectionUtils;
import com.sd.absa.utils.FileIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Koustuv Saha
 * Oct 15, 2015 1:50:24 PM
 * XpressoV3.0  XpEntityCommons
 */
public class ToolKitEntityCommons {

	private String allEntityFile;
	public int processedReviewCount;
	public int reviewBatchSize;
	public boolean saveFile = true;
	private Map<String, Integer> allEntityMap;

	public ToolKitEntityCommons(String fileName) {
		allEntityMap = new ConcurrentHashMap<String, Integer>();
		this.processedReviewCount = 0;
		this.reviewBatchSize = 0;
		if (fileName == null) {
			this.allEntityFile = ToolKitConfig.EXTRACTED_ENTITY_FILENAME + "_" + ToolKitEngine.getCurrentDateStr() + ".txt";
		} else {
			this.allEntityFile = ToolKitConfig.OUTPUT_DIR + "/" + fileName + ToolKitEngine.getCurrentDateStr() + ".txt";
		}
	}

	public Map<String, Integer> getAllEntityMap() {
		return allEntityMap;
	}

	public void setAllEntityMap(Map<String, Integer> allEntityMap) {
		this.allEntityMap = allEntityMap;
	}

	public void sortEntityMap() {
		this.allEntityMap = (HashMap<String, Integer>) CollectionUtils.sortByValue(allEntityMap, true);
	}

	public void writeMap() {
		this.sortEntityMap();
		List<String> entityCountList = new ArrayList<String>();
		for (Entry<String, Integer> entry : this.allEntityMap.entrySet()) {
			String entity = entry.getKey();
			int count = entry.getValue();
			String oneLine = entity + "\t" + count;
			entityCountList.add(oneLine);
		}
		System.out.println("FileName: " + this.allEntityFile);
		FileIO.write_file(entityCountList, this.allEntityFile, false);
	}

	public String getAllEntityFile() {
		return this.allEntityFile;
	}
}
