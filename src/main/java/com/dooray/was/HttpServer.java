package com.dooray.was;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpServer {
	
	private static final Logger logger = Logger.getLogger(HttpServer.class.getCanonicalName());
	private static final int NUM_THREADS = 50;
	private static final String INDEX_FILE = "index.html";
	
	private static String projectRootPath;
	private static Map<String,Map<String,String>> hosts;
	
	private int port;
	private File rootDirectory;
	
	public HttpServer() throws IOException  {
			
			//set default root path
			if(projectRootPath == null) {
				projectRootPath = System.getProperty("user.dir");
			}
			
			//set root path
			File rootDirectory = new File(projectRootPath);
			this.rootDirectory = rootDirectory;
			
			//settings json
			String propertiesPath = projectRootPath + "/src/main/resources/properties.json";
			InputStream getLocalJsonFile = new FileInputStream(propertiesPath);
	        HashMap<String,Object> jsonMap = new ObjectMapper().readValue(getLocalJsonFile, HashMap.class);
			port =  Integer.parseInt((String) jsonMap.get("port"));
			if (port < 0 || port > 65535) port = 80;
			hosts =  (Map<String,Map<String,String>>) jsonMap.get("hosts");
		
	}
	
	public void start() throws IOException {
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		try (ServerSocket server = new ServerSocket(port)) {
			
			logger.info("Accepting connections on port " + server.getLocalPort());
            logger.info("Document Root: " + rootDirectory);
           
            while (true) {
                try {
                    Socket request = server.accept();
                    Runnable r = new RequestProcessor(rootDirectory, INDEX_FILE, request);
                    pool.submit(r);
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "Error accepting connection", ex);
                    
                }
            }
        }
	}
	
	public static void main(String[] args) {
		
		try {
			projectRootPath = args[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Set default path");
        }
		
		try {
			HttpServer httpServer = new HttpServer();
			httpServer.start();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "File Not File", ex);
		} catch( Exception ex) {
			logger.log(Level.SEVERE, "Server could not start", ex);
		}
		
	}
	
	
	
	public static String getProjectRootPath() {
		return projectRootPath;
	}
	
	public static Map<String, String> getPropertiesElement(String host) {
		return hosts.get(host);
	}

	public String getPropertiesElement(String host, String element) {
		
		if(hosts.get(host) == null) {
			return null;
		}
		
		return hosts.get(host).get(element);
	}
	
	public int getPort() {
		return port;
	}

	

	
}
