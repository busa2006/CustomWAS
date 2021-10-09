package com.dooray.was;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dooray.was.request.HttpRequest;
import com.dooray.was.response.HttpResponse;
import com.dooray.was.servlet.SimpleServlet;


public class RequestProcessor implements Runnable {
	private static final Logger logger = (Logger) LoggerFactory.getLogger(RequestProcessor.class);
    private File rootDirectory;
    private String indexFileName = "index.html";
    private Socket connection;

    public RequestProcessor(File rootDirectory, String indexFileName, Socket connection) {
        if (rootDirectory.isFile()) {
            throw new IllegalArgumentException(
                    "rootDirectory must be a directory, not a file");
        }
        try {
            rootDirectory = rootDirectory.getCanonicalFile();
        } catch (IOException ex) {
        	  logger.error("error in root directory setting ",ex);
        }
        
        this.rootDirectory = rootDirectory;
        if (indexFileName != null)
            this.indexFileName = indexFileName;
        this.connection = connection;
    }
    
    @Override
	public void run() {
    	try (InputStream in = connection.getInputStream(); 
        	 OutputStream out = connection.getOutputStream()) {
    		
    		
            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);
            
            /*
             *  1. found the java file specified in the url
             *     -> return content
             *     
             *  2. .(dot) not included
             *     -> return 404
             *     
             *  3. violation of security rules
             *     -> return 403
             *     
             *  4. found the file specified in the url
             *     -> return content
             *     
             *  5. file not found
             *     -> return 404
             */ 
            
            
            // load settings for host
            Map<String,String> settings = Optional.ofNullable(HttpServer.getPropertiesElement(httpRequest.getHost()))
            		.orElse(HttpServer.getPropertiesElement("default"));
          
           try {
        	    //find java class 
	            SimpleServlet servlet = DispatcherServlet.getServlet(httpRequest.getHost(), httpRequest.getPath());
	            
	            //response java class
	            if(servlet != null) {
	            	servlet.service(httpRequest, httpResponse);
	            	return;
	            }
	            
	            //not found java class
	            if(!httpRequest.getPath().contains(".")) {
	            	httpResponse.exception(settings.get("root"),settings.get("404"),"404");
	            	return;
	            }	
	            
	            //invalid path
	            if(httpRequest.getPath().chars().filter(e -> e == '/').count() <=
	            		settings.get("root").chars().filter(e -> e == '/').count()
	            		|| httpRequest.getPath().contains(".exe")) {
	            	httpResponse.exception(settings.get("root"),settings.get("403"),"403");
	            	return;
	            }
	            
	            //find file
	            try {
	            	httpResponse.returnFile(httpRequest.getPath());
	            	return;
	            }catch(FileNotFoundException e) {
	            	httpResponse.exception(settings.get("root"),settings.get("404"),"404");
	            	 logger.error("File not found in path ",e);
	            }
            
	        //internal server error
            }catch(Exception e){
            	httpResponse.exception(settings.get("root"),settings.get("500"),"500");
            	 logger.error("Internal server error ",e);
            }
            
            
            
        } catch (Exception e) {
        	 logger.error("Error ",e);
        }

	}
    
   

	
    
    

}
