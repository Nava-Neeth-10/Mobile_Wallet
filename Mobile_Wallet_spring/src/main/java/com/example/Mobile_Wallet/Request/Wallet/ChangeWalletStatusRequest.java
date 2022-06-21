package com.example.Mobile_Wallet.Request.Wallet;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class ChangeWalletStatusRequest {
	
	@Pattern(regexp = "\\d{10}",message="Incorrect Mobile Number")
	@Getter@Setter
	private String mobileno;
	@Pattern(regexp = "\\d{14}",message="Incorrect Wallet Id")
	@Getter@Setter
	private String walletid;
	@Pattern(regexp = "INACTIVE|ACTIVE",message="Incorrect Status")
	@Getter@Setter
	private String status;

}
