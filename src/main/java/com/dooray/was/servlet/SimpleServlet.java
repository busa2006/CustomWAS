package com.dooray.was.servlet;

import com.dooray.was.request.HttpRequest;
import com.dooray.was.response.HttpResponse;

public interface SimpleServlet {
	void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
