package com.dooray.was;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dooray.was.service.Hello;
import com.dooray.was.service.NowTime;
import com.dooray.was.servlet.SimpleServlet;

public class DispatcherServlet {
	private static Map<String, SimpleServlet> servlet = new HashMap<>();
	private static final Logger logger = Logger.getLogger(HttpServer.class.getCanonicalName());
    static {
    	
    	String className;
    	Class<?> servletClass;
    	Constructor<?> constructor;
    	Map<String, String> classMap = new HashMap<>();
    	String packagePath = "com.dooray.was.";
    	classMap.put("localhost/Hello", "Hello");
    	classMap.put("localhost/service.Hello", "service.Hello");
    	classMap.put("localhost/NowTime", "service.NowTime");
    	classMap.put("localhost/IntendedError", "service.IntendedError");
    	classMap.put("a.com/Hello", "Hello");
    	classMap.put("a.com/service.Hello", "service.Hello");
    	classMap.put("a.com/NowTime", "service.NowTime");
    	classMap.put("a.com/IntendedError", "service.IntendedError");
    	
		try {
			for(String classPath : classMap.keySet()) {
				className = packagePath + classMap.get(classPath);
				servletClass = Class.forName(className);
				constructor =  servletClass.getConstructor(null);
		    	servlet.put(classPath, (SimpleServlet) constructor.newInstance());
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "class setting error", e);
		}
    	
    }

    static SimpleServlet getServlet(String host, String path) {
        return servlet.get(host + path);
    }
}
