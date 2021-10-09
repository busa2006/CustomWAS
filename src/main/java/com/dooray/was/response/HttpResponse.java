package com.dooray.was.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.dooray.was.HttpServer;
import com.dooray.was.RequestProcessor;

public class HttpResponse {
	private final static Logger logger = Logger.getLogger(RequestProcessor.class.getCanonicalName());

    private OutputStreamWriter outputStreamWriter;
    private OutputStream outputStream;
    private static final String DEFAULT_RESOURCES_PATH = "/src/main/resources";
    
    public HttpResponse(OutputStream outputStream) throws IOException {
        this.outputStreamWriter = new OutputStreamWriter(outputStream);
        this.outputStream = outputStream;
    }
    
	public OutputStreamWriter getWriter() throws IOException {
		setResponseLine("200");
		return outputStreamWriter;
	}
	
	public void returnFile(String filePath) throws IOException {
		FileReader Reader = getFile(DEFAULT_RESOURCES_PATH + filePath);
		setResponseLine("200");
		fileRead(Reader);
	}

	public void exception(String rootPath, String fileName, String statusCode) throws IOException {
		
		FileReader Reader = getFile(DEFAULT_RESOURCES_PATH + rootPath + "/" + fileName);
		setResponseLine(statusCode);
        fileRead(Reader);
	}
	
	public void setResponseLine(String statusCode) throws IOException {
		outputStreamWriter.write("HTTP/1.1" + " " + statusCode + "\r\n");
        outputStreamWriter.write("\r\n");
        outputStreamWriter.flush();	
	}
	
	public FileReader getFile(String filePath) throws IOException {
		return new FileReader(HttpServer.getProjectRootPath() + filePath);
	}
	
	public void fileRead(FileReader reader) throws IOException {

        int ch;
        while ((ch = reader.read()) != -1) {
        	 outputStreamWriter.write((char) ch);
        }
        outputStreamWriter.flush();
	}

	public void flush() throws IOException {
		outputStreamWriter.flush();
	}
	
	
	
	

	

	
    
   
 
	
	

}
