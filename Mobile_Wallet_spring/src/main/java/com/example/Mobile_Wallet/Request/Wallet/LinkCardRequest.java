package com.example.Mobile_Wallet.Request.Wallet;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class LinkCardRequest {
	
	@Getter@Setter
	private String cardissuer;
	@Pattern(regexp = "\\d{16}",message="Incorrect Card Number")
	@Getter@Setter
	private String cardno;
	@Pattern(regexp = "\\d{3}",message="Incorrect CVV")
	@Getter@Setter
	private String cvv;
	@Getter@Setter
	private String expiry;
	@Pattern(regexp = "\\d{14}",message="Incorrect Wallet Id")
	@Getter@Setter
	private String walletid;

}
