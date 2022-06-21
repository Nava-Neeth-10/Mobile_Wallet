package com.example.Mobile_Wallet.Request.Payment;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class GetUserTransactionsRequest {
	
	@Pattern(regexp = "[\\d]{10}",message="Incorrect Mobile Number")
	@Getter@Setter
	String mobileno;

}
