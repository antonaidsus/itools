package edu.ucla.loni.ccb.itools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.lang.ArrayUtils;

public class Util {
    public static String toJavaStyle(String reg) {
    	if (reg.startsWith("*")) {
			reg = "." + reg;
	    }
    	return reg;
    }
    
    public static String toString(Object o) {
    	if (o instanceof Object[]) {
    		return toString((Object[])o);
    	} else {
    		return o.toString();
    	}
    }
    
    public static String toString(Object[] arrays) {
    	return ArrayUtils.toString(arrays);
    }

	/**
	 * echo data from OutputStream 2 InputStream
	 */
	public static long echoData(OutputStream out, InputStream in)
			throws IOException {
		byte[] buf = new byte[1024];
		int bytesRead;
		long total = 0L;
		while ((bytesRead = in.read(buf)) != -1) {
			out.write(buf, 0, bytesRead);
			total += bytesRead;
		}
		return total;
	}
	
	/**
	 * echo data from OutputStream 2 InputStream
	 */
	public static long echoData(Writer out, Reader in)
			throws IOException {
		char[] buf = new char[1024];
		int charsRead;
		long total = 0L;
		while ((charsRead = in.read(buf)) != -1) {
			out.write(buf, 0, charsRead);
			total += charsRead;
		}
		return total;
	}

	public static String wrapString(String s) {
		return "\"" + s + "\"";
	}

	public static String File2Str(File file) throws IOException {
		StringWriter sw = new StringWriter();
		FileReader in = new FileReader(file);
		echoData(sw, in);
		in.close();
		return sw.toString();
		
	}
	
	public static void str2File(String str, File file) throws IOException {
		StringReader reader = new StringReader(str);
		FileWriter writer = new FileWriter(file);
		echoData(writer, reader);
		writer.close();
	}

	public static void sleep(int timeInSeconds) {
		try {
			Thread.sleep( timeInSeconds * 1000);
		} catch (InterruptedException e) {
			//ignored
		}
	}
}
