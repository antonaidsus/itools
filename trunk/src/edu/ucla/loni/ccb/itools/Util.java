package edu.ucla.loni.ccb.itools;

public class Util {
    public static String toJavaStyle(String reg) {
    	if (reg.startsWith("*")) {
			reg = "." + reg;
	    }
    	return reg;
    }
}
