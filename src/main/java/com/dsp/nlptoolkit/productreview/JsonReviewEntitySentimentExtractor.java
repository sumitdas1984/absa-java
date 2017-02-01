package com.dsp.nlptoolkit.productreview;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.dsp.nlptoolkit.engine.core.ToolKitExpression;
import com.google.gson.Gson;

public class JsonReviewEntitySentimentExtractor {

	private static List<Result> resultList = new ArrayList<Result>();

	public static void main(String[] args) {

		JSONParser parser = new JSONParser();
		ToolKitExpression.init();

		try {

			Object obj = parser.parse(new FileReader("input/Reviews_SamsungAVSForum_sample.json"));

			JSONArray reviewList = (JSONArray) obj;
			Iterator<JSONObject> iterator = reviewList.iterator();
			int i = 1;
			while (iterator.hasNext()) {

				JSONObject object = iterator.next();
				String description = (String) object.get("description");
				ToolKitExpression review = new ToolKitExpression(description);
				Map<String, String> review_entitySentimentMap = review.getSentiment();

				//JsonArray linkArray = (JsonArray) object.get("link");

				Result result = new Result((String) object.get("link").toString(), (String) object.get("type"), (String) object.get("description"), iterateHashMap(review_entitySentimentMap));
				resultList.add(result);
				System.out.println("@@review processed: "+i);
				i++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

//		writeToCSV(resultList);
//		System.out.println("writing to csv completed...");
		
		Gson gson = new Gson();
		String json = gson.toJson(resultList);
		writeJson(json);
		System.out.println("writing to json completed...");

		System.out.println("Done");
	}

	private static void writeJson(String json) {

		String outputFile = "output/FeatureSentiment_SamsungAVSForum.json";
		FileWriter out;
		try {
			out = new FileWriter(outputFile, true);
			out.write(json);
			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static List<EntitySentiment> iterateHashMap(Map<String, String> entitySentimentMap) {
		
		List<EntitySentiment> returnList = new ArrayList<EntitySentiment>();

		Set temp = entitySentimentMap.entrySet();

		Iterator itr = temp.iterator();
		while (itr.hasNext()) {
			Map.Entry pair = (Map.Entry) itr.next();
			EntitySentiment  aspectSentiment = new EntitySentiment((String)pair.getKey(), (String)pair.getValue());
			returnList.add(aspectSentiment);
		}

		return returnList;
	}

//	private static void writeToCSV(List<Result> resultList) {
//
//		String outputFile = "result.csv";
//
//		// before we open the file check to see if it already exists
//		boolean alreadyExists = new File(outputFile).exists();
//
//		try {
//			// use FileWriter constructor that specifies open for appending
//			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), '#');
//
//			// if the file didn't already exist then we need to write out the header line
//			if (!alreadyExists) {
//				csvOutput.write("type");
//				csvOutput.write("link");
//				csvOutput.write("description");
//				csvOutput.write("entity-sentiment");
//				csvOutput.endRecord();
//			}
//			// else assume that the file already has the correct header line
//
//			for (Result result : resultList) {
//				csvOutput.write(result.getType());
//				csvOutput.write(result.getLink());
//				csvOutput.write(result.getDescription());
//				csvOutput.write(result.getAspectSentiment());
//				csvOutput.endRecord();
//			}
//
//			csvOutput.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

}
