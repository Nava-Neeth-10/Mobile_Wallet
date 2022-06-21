package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class User {
	
	@Id
	@Getter@Setter
	private Integer userid;
	@Getter@Setter
	private Integer otpid;
	@Getter@Setter
	private String usertype,mobileno,userstatus,createddate,primarywallet;

}
