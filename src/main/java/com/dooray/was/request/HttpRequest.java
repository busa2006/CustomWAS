package com.dooray.was.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dooray.was.HttpServer;
import com.dooray.was.RequestProcessor;

public class HttpRequest {
	private final static Logger logger = Logger.getLogger(RequestProcessor.class.getCanonicalName());
	private String method;
	private String url;  
	private String host; 
	private String path; 
	private Map<String,String> parameters;
	private String version;
	private Map<String, String> headers; 
	private String body;
	
	public HttpRequest(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		line(br);
		header(br);
		body(br);
		
		
		logger.log(Level.INFO,"headers: " + headers.toString());
		logger.log(Level.INFO,"body: " + body);
		logger.log(Level.INFO,"parameters: " + parameters);
	}
	

	private void line(BufferedReader br) throws IOException {
		String brLine = br.readLine();
		String[] requestLine = 	brLine.split(" ");
		
        this.method = requestLine[0];
        this.url = requestLine[1];
        this.version= requestLine[2];
        String[] s = requestLine[1].split("\\?");
        this.path = s[0];
        if(s.length > 1)
        this.parameters = getQueryMap(s[1]);
        
	}
	
	public Map<String, String> getQueryMap(String query)
    {    	
    	if (query==null) return null;
    	
    	int pos1=query.indexOf("?");
    	if (pos1>=0) {
    		query=query.substring(pos1+1);
    	}
    	
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

	private void header(BufferedReader br) throws IOException {
		String line;
        Map<String, String> headers = new HashMap<>();
        while (!(line = br.readLine()).equals("")) {
            String[] header = line.split(": ");
            headers.put(header[0], header[1]);
        }
        
        if(headers.containsKey("Host")) {
        	this.host = headers.get("Host");
        }
        this.headers = headers;
		
	}

	private void body(BufferedReader br) throws IOException {
		int contentLength = Integer.parseInt(Optional.ofNullable(headers.get("Content-Length")).orElse("0"));
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        this.body =  String.copyValueOf(body);
	}
	
	
	
	
	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getParameter(String key) {
		
		if(parameters == null) {
			return null;
		}
		
		String parameter = parameters.get(key);
		if(parameter == null) {
			logger.log(Level.INFO, "not found parameter : "+ key);
		}
		return parameter;
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}


	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	
	
	

}
