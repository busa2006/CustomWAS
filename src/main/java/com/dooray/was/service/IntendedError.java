package com.dooray.was.service;

import java.io.IOException;

import com.dooray.was.request.HttpRequest;
import com.dooray.was.response.HttpResponse;
import com.dooray.was.servlet.SimpleServlet;

public class IntendedError implements SimpleServlet {

	@Override
	public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
		throw new Exception();
	}
	

}
