package com.example.Mobile_Wallet.Request.Wallet;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class SingleWalletDetailsRequest {
	
	@Pattern(regexp = "\\d{14}",message="Invalid wallet ID")
	@Getter@Setter
	private String walletid;

}
