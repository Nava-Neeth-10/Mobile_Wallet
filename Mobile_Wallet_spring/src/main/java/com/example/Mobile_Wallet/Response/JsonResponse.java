package com.example.Mobile_Wallet.Response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class JsonResponse {
	
	@Getter@Setter
	private Object Response;
	@Getter@Setter
	private HttpStatus ResponseCode;
}
