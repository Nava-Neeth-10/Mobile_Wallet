package com.example.Mobile_Wallet.Request.User;

import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class ModifyUserDetailsRequest {
	
	@Pattern(regexp = "[a-zA-Z\s]{2,15}",message="First Name cannot have numbers or special charecters")
	@Getter@Setter
	private String first_name;
	@Pattern(regexp = "[a-zA-Z\s]{0,15}",message="Middle Name cannot have numbers or special charecters")
	@Getter@Setter
	private String middle_name;
	@Pattern(regexp = "[a-zA-Z\s]{2,15}",message="Last Name cannot have numbers or special charecters")
	@Getter@Setter
	private String last_name;
	@Getter@Setter
	private String dob;
	@Pattern(regexp=".{1,30}",message="Search Key cannot be empty")
	@Getter@Setter
	private String searchkey;
	@Pattern(regexp = "MobileNo|Email|Username",message="Incorrect Key Name")
	@Getter@Setter
	private String keyname;

}
