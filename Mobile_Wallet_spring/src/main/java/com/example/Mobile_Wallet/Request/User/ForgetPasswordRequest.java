package com.example.Mobile_Wallet.Request.User;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class ForgetPasswordRequest {
	
	@Pattern(regexp = "[\\S]{5,30}",message="Username cannot have spaces")
	@Getter@Setter
	private String username;

}
