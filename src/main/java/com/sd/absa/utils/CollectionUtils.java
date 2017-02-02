/**
 * 
 */
package com.sd.absa.utils;

import java.util.*;

/**
 * @author Sumit Das
 * May 1, 2014 5:47:17 AM 
 */
public class CollectionUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	//	public static Map sortByValue(Map map) throws Exception {
	//		List list = new LinkedList(map.entrySet());
	//		Collections.sort(list, new Comparator() {
	//			public int compare(Object o1, Object o2) {
	//				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
	//			}
	//		});
	//
	//		Collections.reverse(list);
	//
	//		Map result = new LinkedHashMap();
	//		for (Iterator it = list.iterator(); it.hasNext();) {
	//			Map.Entry entry = (Map.Entry) it.next();
	//			result.put(entry.getKey(), entry.getValue());
	//		}
	//		return result;
	//	}

	//	public static Map sortByKey(Map map) throws Exception {
	//		Map result = new TreeMap<>();
	//		result.putAll(map);
	//		return result;
	//	}

	public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) throws Exception {
		//	public static <K, V extends Comparable<? super V>> Map<K, V> sortByKey(Map<K, V> map) throws Exception {
		Map<K, V> sortedMap = new TreeMap<K, V>(map);
		//		result.putAll(map);
		//		sortedMap.putAll(map);
		return sortedMap;

	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean isDescending) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		if (isDescending) {
			Collections.reverse(list);
		}

		Map<K, V> resultMap = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			resultMap.put(entry.getKey(), entry.getValue());
		}
		return resultMap;
	}
	
}
