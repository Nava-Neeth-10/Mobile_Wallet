package com.example.Mobile_Wallet.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class CMS_Service {
	
	public static String Linking(String cardno, String cvv,String expiry,String walletid) throws Exception
	{
		String uri = "http://localhost:8080/CMS/linkingaccount?walletid="+walletid+"&cardno="+cardno+"&cvv="+cvv+"&expiry="+expiry;
		HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
	    HttpResponse<String> cms_response_body =client.send(request, BodyHandlers.ofString());
	    return cms_response_body.body();
	}
	
	public static String Delinking(String token,String walletid) throws Exception
	{
		String uri = "http://localhost:8080/CMS/delinkingaccount?token="+token+"&walletid="+walletid;
		HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
	    HttpResponse<String> cms_response_body =client.send(request, BodyHandlers.ofString());
		return cms_response_body.body();
	}
	
	public static String SendMoney(String token,String amount) throws Exception
	{
		String uri = "http://localhost:8080/CMS/SendMoney?token="+token+"&amount="+amount;
		HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
	    HttpResponse<String> response =client.send(request, BodyHandlers.ofString());
		return response.body();
	}
	
	public static String AddMoney(String token,String amount) throws Exception
	{
		String uri = "http://localhost:8080/CMS/AddMoney?token="+token+"&amount="+amount;
		HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
	    HttpResponse<String> response =client.send(request, BodyHandlers.ofString());
		return response.body();
	}
	
	public static String GetBalance(String token) throws Exception{
		String uri = "http://localhost:8080/CMS/getaccountbalance?token="+token;
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request=HttpRequest.newBuilder().uri(URI.create(uri)).build();		
		HttpResponse<String> response=client.send(request, BodyHandlers.ofString());
		return response.body();
	}

}
