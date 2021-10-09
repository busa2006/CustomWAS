package com.dooray.was;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Spec2Test {

	@Test
	public void serverAcceptsRequest() throws IOException {
		HttpServer server = new HttpServer();

		String propertiesPath = System.getProperty("user.dir") + "/src/main/resources/properties.json";
		InputStream getLocalJsonFile = new FileInputStream(propertiesPath);
        HashMap<String,Object> jsonMap = new ObjectMapper().readValue(getLocalJsonFile, HashMap.class);
		int port =  Integer.parseInt((String) jsonMap.get("port"));
		Map<String,Map<String,String>> hosts =  (Map<String,Map<String,String>>) jsonMap.get("hosts");
		
		// spec 2.
		// manage settings by host
		assertEquals(port, server.getPort());
		assertEquals(hosts.get("a.com").get("root"),server.getPropertiesElement("a.com","root"));
		assertEquals(hosts.get("a.com").get("403"),server.getPropertiesElement("a.com","403"));
		assertEquals(hosts.get("a.com").get("404"),server.getPropertiesElement("a.com","404"));
		assertEquals(hosts.get("a.com").get("500"),server.getPropertiesElement("a.com","500"));
		
		
	}

}
