package com.example.Mobile_Wallet.Request.User;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class CreateUserWithMobileNoRequest {
	
	@Pattern(regexp = "[\\d]{10}",message="Incorrect Mobile Number")
	@Getter@Setter
	String mobileno;
	@Getter@Setter
	String deviceid;

}
