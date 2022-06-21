package com.example.Mobile_Wallet.Request.Payment;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class AddFromBankRequest {
	
	@Pattern(regexp = "\\d{14}",message="Incorrect Wallet Id")
	@Getter@Setter
	private String walletid;
	@Pattern(regexp = "\\d{4}",message="Incorrect Card Number")
	@Getter@Setter
	private String cardno;
	@Getter@Setter
	private String cardissuer;
	@Pattern(regexp = "\\d+",message="Incorrect Amount")
	@Getter@Setter
	private String amount;

}
