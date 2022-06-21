package com.example.Mobile_Wallet.Request.Wallet;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class CreateWalletRequest {
	
	@Pattern(regexp = "[\\d]{10}",message="Incorrect Mobile Number")
	@Getter@Setter
	private String mobileno;
	@Pattern(regexp = ".{1,10}",message="Invalid Walletname")
	@Getter@Setter
	private String walletname;
	@Pattern(regexp = "[A-Z]{3}",message="Incorrect Currency")
	@Getter@Setter
	private String currency;
	@Pattern(regexp = "ACTIVE|INACTIVE",message="Incorrect Status")
	@Getter@Setter
	private String status;

}
