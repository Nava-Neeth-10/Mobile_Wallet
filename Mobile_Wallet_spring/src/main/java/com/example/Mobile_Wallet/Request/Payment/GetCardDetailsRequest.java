package com.example.Mobile_Wallet.Request.Payment;

import lombok.Getter;
import lombok.Setter;

public class GetCardDetailsRequest {
	
	
	@Getter@Setter
	private String cardno;
	@Getter@Setter
	private String cvv;
	@Getter@Setter
	private String expiry_date;
	

}
