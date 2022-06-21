package com.example.Mobile_Wallet.Request.User;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class LoginUserRequest {
	
	@Pattern(regexp = "[\\S]{5,30}",message="Username must be have only 5 to 30 characters without spaces")
	@Getter@Setter
	private String username;
	@Pattern(regexp = "[\\S]{5,30}",message="Password must be have only 5 to 30 characters without spaces")
	@Getter@Setter
	private String password;

}
