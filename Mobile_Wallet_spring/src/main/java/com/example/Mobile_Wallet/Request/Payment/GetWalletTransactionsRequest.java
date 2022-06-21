package com.example.Mobile_Wallet.Request.Payment;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class GetWalletTransactionsRequest {
	
	@Pattern(regexp = "[\\d]{14}",message="Incorrect WalletID")
	@Getter@Setter
	private String walletid;

}
