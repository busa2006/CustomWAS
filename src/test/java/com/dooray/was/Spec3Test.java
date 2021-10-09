package com.dooray.was;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class Spec3Test {

	public class MockSocket extends Socket {
		// create an empty list of bytes
		private List<Byte> bytesList = new ArrayList<>();
		private String request;
		
		public MockSocket() {
		}
		
		public void setRequest(String request) {
			this.request = request;
		}

		public InputStream getInputStream() {
			return new ByteArrayInputStream(request.getBytes());
		}

		public OutputStream getOutputStream() {
			return new OutputStream() {
				@Override
				// every time we call `write` (out.print),
				// we add the bytes to the list 'bytesList'
				public void write(int b) throws IOException {
					bytesList.add((byte) b);
				}
			};
		}

		// this method does not exist in the extended class 'Socket'
		// it is used to return the string formed by the bytes added to 'bytesList'
		public String output() {
			byte[] converted = toByteArray(bytesList);
			return new String(converted, StandardCharsets.UTF_8);
		}

		// convert a list of Bytes objects to an byte array
		private byte[] toByteArray(List<Byte> byteList) {
			byte[] byteArray = new byte[byteList.size()];
			int index = 0;
			for (byte b : byteList) {
				byteArray[index++] = b;
			}
			return byteArray;
		}
	}
	
	MockSocket mockSocket;
	RequestProcessor rp;
	File root = new File(System.getProperty("user.dir"));
	String indexFile = "index.html";
	
	public String request(String requestString) {
		mockSocket = new MockSocket();
		mockSocket.setRequest(requestString);
		rp = new RequestProcessor(root, indexFile, mockSocket);
		rp.run();
		return mockSocket.output();
	}
	
	@Test
	public void serverAcceptsRequest() throws IOException {
		HttpServer server = new HttpServer();
		
		String body1 = request("GET /file.exe HTTP/1.1\nHost: a.com\n\n");
		String body2 = request("GET /nonExistPath HTTP/1.1\nHost: a.com\n\n");
		String body3 = request("GET /IntendedError HTTP/1.1\nHost: a.com\n\n");
		
		// spec 3.
		// error handling
		assertEquals("HTTP/1.1 403\r\n\r\n<h1> a 403!</h1>", body1);
		assertEquals("HTTP/1.1 404\r\n\r\n<h1> a 404!</h1>", body2);
		assertEquals("HTTP/1.1 500\r\n\r\n<h1> a 500!</h1>", body3);
		
	}

}
