package com.example.Mobile_Wallet.Request.Wallet;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class DelinkCardRequest {
	
	@Pattern(regexp = "\\d{14}",message="Incorrect WalletId")
	@Getter@Setter
	private String walletid;
	@Pattern(regexp = "\\d{4}",message="Incorrect Card Number")
	@Getter@Setter
	private String cardno;
	@Getter@Setter
	private String cardissuer;
	
}
