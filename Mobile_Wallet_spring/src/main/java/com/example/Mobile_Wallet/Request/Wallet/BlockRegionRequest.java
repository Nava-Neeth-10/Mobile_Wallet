package com.example.Mobile_Wallet.Request.Wallet;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class BlockRegionRequest {
	
	@Pattern(regexp = "\\d{14}",message="Incorrect Wallet Id")
	@Getter@Setter
	private String walletid;
	@Pattern(regexp = "[A-Z]{2}",message="Incorrect Region")
	@Getter@Setter
	private String region;

}
