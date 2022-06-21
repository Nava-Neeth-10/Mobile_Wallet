package com.example.Mobile_Wallet.Request.Payment;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class GetBinDetailsRequest {
	
	@Pattern(regexp = "\\d{6}",message="Incorrect Bim Number")
	@Getter@Setter
	String binnumber;
}
