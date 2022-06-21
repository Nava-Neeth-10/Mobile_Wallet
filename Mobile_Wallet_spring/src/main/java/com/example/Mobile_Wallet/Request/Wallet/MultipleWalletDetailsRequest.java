package com.example.Mobile_Wallet.Request.Wallet;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class MultipleWalletDetailsRequest {
	@Pattern(regexp = "\\d{10}",message="Incorrect Mobile Number")
	@Getter@Setter
	private String mobileno;
	@Pattern(regexp="ASC|DESC",message="Incorrect Sorting option")
	@Getter@Setter
	private String sortby;
	@Pattern(regexp = "ALL|INACTIVE|ACTIVE",message="Incorrect Status")
	@Getter@Setter
	private String status;
}
