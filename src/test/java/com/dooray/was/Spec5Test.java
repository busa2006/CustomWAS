package com.dooray.was;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

public class Spec5Test {
	
	@Test
	public void serverAcceptsRequest() throws IOException {
		
		
		FileReader log1 = new FileReader(System.getProperty("user.dir") + "/logs/file/was.log");
		FileReader log2 = new FileReader(System.getProperty("user.dir") + "/logs/file/was.log.2021-08-27.log");
		
		// spec 5.
		// log file management
		assertThat(log1,is(not(equalTo(null))));
		assertThat(log2,is(not(equalTo(null))));
	}

}
