package com.example.Mobile_Wallet.Request.User;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class ValidateOtpRequest {
	
	@Pattern(regexp = "[\\d]{10}",message="Incorrect Mobile Number")
	@Getter@Setter
	String mobileno;
	@Pattern(regexp = "[\\d]{4}",message="Incorrect OTP")
	@Getter@Setter
	String otp;

}
