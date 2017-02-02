/**
 *
 */
package com.sd.absa.utils;

import edu.stanford.nlp.io.IOUtils;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * @author Koustuv Saha
 * 10-Mar-2014 4:59:38 pm
 * XpressoV2 FileIO
 */
public class FileIO {

	public static InputStream getInputStreamFromFileName(String fileName) {

		InputStream is = null;

		/*This is applicable when the xpresso is used as standalone application*/
		try {
			is = findStreamInClasspathOrFileSystem(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return is;
	}

	private static InputStream findStreamInClasspathOrFileSystem(String name) throws FileNotFoundException {
		InputStream is = IOUtils.class.getClassLoader().getResourceAsStream(name);
		if (is == null) {
			is = IOUtils.class.getClassLoader().getResourceAsStream(name.replaceAll("\\\\", "/"));
			if (is == null) {
				is = IOUtils.class.getClassLoader().getResourceAsStream(name.replaceAll("\\\\", "/").replaceAll("/+", "/"));
			}
		}
		if (is == null)
			is = new FileInputStream(name);
		if (name.endsWith(".gz")) {
			try {
				return new GZIPInputStream(is);
			} catch (IOException e) {
				System.err.println("Resource or file looks like a gzip file, but is not: " + name);
			}
		}
		return is;
	}

	public static void read_file(String fileName, Collection<String> collection) {
		read_file(fileName, collection, false);

	}

	public static void read_file(String fileName, Collection<String> collection, boolean ignoreHeader) {
		try {
			//			BufferedReader br = new BufferedReader(new FileReader(fileName));
			//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromFileName(fileName), "UTF8"));

			//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			String strLine;
			int i = 0;

			while ((strLine = br.readLine()) != null) {
				if (i++ == 0) {
					if (ignoreHeader) {
						continue;
					}
				}
				collection.add(strLine);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void read_file(String fileName, Collection<String> collection, String charset) {
		try {
			//			BufferedReader br = new BufferedReader(new FileReader(fileName));

			//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset));
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromFileName(fileName), charset));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				collection.add(strLine);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void read_file(String fileName, Collection<String> collection, boolean ignoreHeader, boolean isLowerCase) {
		try {
			//			BufferedReader br = new BufferedReader(new FileReader(fileName));

			//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromFileName(fileName), "UTF8"));

			String strLine;
			int i = 0;
			while ((strLine = br.readLine()) != null) {
				if (i++ == 0) {
					if (ignoreHeader) {
						continue;
					}
				}
				if (isLowerCase) {
					collection.add(strLine.toLowerCase());
				} else {
					collection.add(strLine);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void read_file(String fileName, Map<String, String> map) {
		try {
			//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromFileName(fileName), "UTF8"));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				String[] strArr = strLine.split("\\s+");
				if (strArr.length == 2) {
					map.put(strArr[1], strArr[0]);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write_file(Collection<?> triples_strList, String fileName, boolean isAppend) {
		File file = new File(fileName);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdir();
		}
		try {
			PrintWriter pw;
			pw = new PrintWriter(new FileWriter(file, isAppend));
			for (Object str : triples_strList) {
				pw.println(str.toString());
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void write_file(String strLine, String fileName, boolean isAppend) {
		File file = new File(fileName);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdir();
		}
		synchronized (file) {
			try {
				PrintWriter pw;
				pw = new PrintWriter(new FileWriter(file, isAppend));
				pw.println(strLine);
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void write_file(Map<?, ?> map, String fileName, boolean isAppend, String delimiter) {
		List<String> keyValueList = new ArrayList<String>();
		for (Entry<?, ?> entry : map.entrySet()) {
			keyValueList.add(entry.getKey() + delimiter + entry.getValue());
		}
		write_file(keyValueList, fileName, isAppend);
	}

	public static synchronized void write_fileNIO(String line, String fileName, boolean isAppend) {
		Path file = null;
		AsynchronousFileChannel asynchFileChannel = null;
		String filePath = fileName;
		try {

			if (fileName != null) {

				file = Paths.get(filePath);
				asynchFileChannel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

				CompletionHandler<Integer, Object> handler = new CompletionHandler<Integer, Object>() {
					@Override
					public void completed(Integer result, Object attachment) {
						//					System.out.println("Thread: " + Thread.currentThread().getName() + " File Write Completed with Result:" + result);
					}

					@Override
					public void failed(Throwable e, Object attachment) {
						//					System.err.println("File Write Failed Exception:");
						e.printStackTrace();
					}
				};
				asynchFileChannel.write(ByteBuffer.wrap(line.getBytes()), asynchFileChannel.size(), "", handler);
				asynchFileChannel.close();
			} else {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//		Future<FileLock> featureLock = asynchFileChannel.lock();
		//		FileLock lock = featureLock.get();
		//		if (lock.isValid()) {
		//			Future<Integer> featureWrite = asynchFileChannel.write(ByteBuffer.wrap(test), asynchFileChannel.size());
		//			lock.release();
		//		}
	}

	public static synchronized void write_fileNIO(Collection<?> collection, String fileName, boolean isAppend) {
		Path file = null;
		AsynchronousFileChannel asynchFileChannel = null;
		String filePath = fileName;
		try {
			StringBuilder sb = new StringBuilder();
			for (Object str : collection) {
				sb.append(str.toString()).append("\n");
			}
			if (fileName != null) {

				file = Paths.get(filePath);
				asynchFileChannel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

				CompletionHandler<Integer, Object> handler = new CompletionHandler<Integer, Object>() {
					@Override
					public void completed(Integer result, Object attachment) {
						//					System.out.println("Thread: " + Thread.currentThread().getName() + " File Write Completed with Result:" + result);
					}

					@Override
					public void failed(Throwable e, Object attachment) {
						//					System.err.println("File Write Failed Exception:");
						e.printStackTrace();
					}
				};
				//				System.out.println("Thread: " + Thread.currentThread().getName() + " Before write call");

				//			Future<FileLock> featureLock = asynchFileChannel.lock();
				//			//			FileLock featureLock = asynchFileChannel.tryLock();
				//			FileLock lock = featureLock.get();
				//
				//			if (lock.acquiredBy() == null) {
				//				lock = asynchFileChannel.tryLock();
				//			}
				//			if (lock.isValid()) {
				//			asynchFileChannel.write(ByteBuffer.wrap(sb.toString().getBytes()), asynchFileChannel.size(), "File Write", handler);

				//			if (lock.isValid()) {
				asynchFileChannel.write(ByteBuffer.wrap(sb.toString().getBytes()), asynchFileChannel.size(), "", handler);
				asynchFileChannel.close();
			} else {
				System.out.println(sb.toString());
			}
			//			}
			//			asynchFileChannel.force(true);
			//				lock.release();
			//			}

			//			System.out.println("Thread: " + Thread.currentThread().getName() + " After write call");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//		Future<FileLock> featureLock = asynchFileChannel.lock();
		//		FileLock lock = featureLock.get();
		//		if (lock.isValid()) {
		//			Future<Integer> featureWrite = asynchFileChannel.write(ByteBuffer.wrap(test), asynchFileChannel.size());
		//			lock.release();
		//		}
	}

	public static synchronized void write_fileNIO(Collection<?> collection, AsynchronousFileChannel asynchFileChannel, boolean isAppend) {
		try {
			StringBuilder sb = new StringBuilder();
			for (Object str : collection) {
				sb.append(str.toString()).append("\n");
			}
			if (asynchFileChannel != null) {

				CompletionHandler<Integer, Object> handler = new CompletionHandler<Integer, Object>() {
					@Override
					public void completed(Integer result, Object attachment) {
						//					System.out.println("Thread: " + Thread.currentThread().getName() + " File Write Completed with Result:" + result);
					}

					@Override
					public void failed(Throwable e, Object attachment) {
						//					System.err.println("File Write Failed Exception:");
						e.printStackTrace();
					}
				};
				//				System.out.println("Thread: " + Thread.currentThread().getName() + " Before write call");

				//			Future<FileLock> featureLock = asynchFileChannel.lock();
				//			//			FileLock featureLock = asynchFileChannel.tryLock();
				//			FileLock lock = featureLock.get();
				//
				//			if (lock.acquiredBy() == null) {
				//				lock = asynchFileChannel.tryLock();
				//			}
				//			if (lock.isValid()) {
				//			asynchFileChannel.write(ByteBuffer.wrap(sb.toString().getBytes()), asynchFileChannel.size(), "File Write", handler);

				//			if (lock.isValid()) {
				asynchFileChannel.write(ByteBuffer.wrap(sb.toString().getBytes()), asynchFileChannel.size(), "", handler);
				//				asynchFileChannel.close();
			} else {
				System.out.println(sb.toString());
			}
			//			}
			//			asynchFileChannel.force(true);
			//				lock.release();
			//			}

			//			System.out.println("Thread: " + Thread.currentThread().getName() + " After write call");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//		Future<FileLock> featureLock = asynchFileChannel.lock();
		//		FileLock lock = featureLock.get();
		//		if (lock.isValid()) {
		//			Future<Integer> featureWrite = asynchFileChannel.write(ByteBuffer.wrap(test), asynchFileChannel.size());
		//			lock.release();
		//		}
	}

	public static void main(String[] args) {
		List<String> justCheckList = Arrays.asList("ab", "bc");
		write_file(justCheckList, "./bullshitchalance/bull.txt", false);
	}
}
