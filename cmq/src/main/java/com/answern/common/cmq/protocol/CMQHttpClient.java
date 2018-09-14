package com.answern.common.cmq.protocol;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class CMQHttpClient {
	private static volatile CloseableHttpClient httpClient;
	private static final Object lock = new Object();


	public static CloseableHttpClient getInstance(){
		if(httpClient == null) {
			synchronized(lock) {
				if(httpClient == null) {
					httpClient = HttpClients.createDefault();
				}
			}
		}
		return httpClient;
	}

}