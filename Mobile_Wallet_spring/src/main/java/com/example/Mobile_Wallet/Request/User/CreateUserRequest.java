package com.example.Mobile_Wallet.Request.User;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class CreateUserRequest {
	
	@Pattern(regexp = "[a-zA-Z\s]{2,15}",message="First Name cannot have numbers or special charecters")
	@Getter@Setter
	private String first_name;
	@Pattern(regexp = "[a-zA-Z\s]{0,15}",message="Middle Name cannot have numbers or special charecters")
	@Getter@Setter
	private String middle_name;
	@Pattern(regexp = "[a-zA-Z\s]{2,15}",message="Last Name cannot have numbers or special charecters")
	@Getter@Setter
	private String last_name;
	@Pattern(regexp=".+@.+\\..+",message="Email format incorrect")
	@Getter@Setter
	private String email;
	@Pattern(regexp = "[\\S]{5,30}",message="Username cannot have spaces")
	@Getter@Setter
	private String username;
	@Pattern(regexp = "[\\S]{5,30}",message="Password cannot have spaces")
	@Getter@Setter
	private String password;
	@Pattern(regexp = "CUST|BUS",message="Incorrect UserType")
	@Getter@Setter
	private String userType;
	@Pattern(regexp = "[\\d]{10}",message="Incorrect Mobile Number")
	@Getter@Setter
	private String mobileno;
	@Pattern(regexp = "[\\d]{1,4}",message="Incorrect Country Code")
	@Getter@Setter
	private String countrycode;
	@Getter@Setter
	private String dob;

}
