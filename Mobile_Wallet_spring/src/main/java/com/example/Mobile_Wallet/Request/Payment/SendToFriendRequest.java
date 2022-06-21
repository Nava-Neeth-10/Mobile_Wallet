package com.example.Mobile_Wallet.Request.Payment;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
public class SendToFriendRequest {
	
	@Pattern(regexp = "[\\d]{10}",message="Incorrect Mobile Number")
	@Getter@Setter
	String Receivermobileno;
	@Pattern(regexp = "\\d{14}",message="Incorrect Sender Wallet Id")
	@Getter@Setter
	private String senderwalletid;
	@Pattern(regexp = "\\d+",message="Incorrect Amount")
	@Getter@Setter
	private String money;
	@Getter@Setter
	private String deviceid;
	

}
