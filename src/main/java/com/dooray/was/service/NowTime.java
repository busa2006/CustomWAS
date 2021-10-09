package com.dooray.was.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.dooray.was.request.HttpRequest;
import com.dooray.was.response.HttpResponse;
import com.dooray.was.servlet.SimpleServlet;

public class NowTime implements SimpleServlet {
	
	@Override
	public void service(HttpRequest req, HttpResponse res) throws IOException {
		java.io.Writer writer = res.getWriter();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		writer.write(dtf.format(LocalDateTime.now()));
		writer.flush();
	}
}
