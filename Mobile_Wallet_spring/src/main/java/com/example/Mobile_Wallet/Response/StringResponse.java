package com.example.Mobile_Wallet.Response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class StringResponse {
	
	@Getter@Setter
	private String Response;
	@Getter@Setter
	private HttpStatus ResponseCode;

}
