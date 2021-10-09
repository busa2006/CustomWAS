package com.dooray.was;

import java.io.IOException;

import com.dooray.was.request.HttpRequest;
import com.dooray.was.response.HttpResponse;
import com.dooray.was.servlet.SimpleServlet;

public class Hello implements SimpleServlet {
	
	@Override
	public void service(HttpRequest req, HttpResponse res) throws IOException {
		String str = "Hello, " + req.getParameter("name");
		res.getWriter().write(str);
		res.flush();
	}
	
}
