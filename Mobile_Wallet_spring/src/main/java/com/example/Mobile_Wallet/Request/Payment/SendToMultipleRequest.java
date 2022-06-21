package com.example.Mobile_Wallet.Request.Payment;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class SendToMultipleRequest {
	
	@Pattern(regexp = "\\d{14}",message="Incorrect Wallet Id")
	@Getter@Setter
	private String senderwalletid;
	@Getter@Setter
	private String receivermobileno;
	@Pattern(regexp = "\\d+",message="Incorrect Amount")
	@Getter@Setter
	private String money;
	@Getter@Setter
	private String deviceid;
	

}