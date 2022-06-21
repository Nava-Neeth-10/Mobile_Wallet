package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class CMS_User {
	
	@Id
	@Getter@Setter
	private int userid;
	
	@Getter@Setter
	private String mobileno,email,weblogin,mobilelogin,firstname,middlename,lastname,aadharno,panno,usertype,dob;

	
}
