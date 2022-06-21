package com.example.Mobile_Wallet.Database;


import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;


@Entity
public class CMS_Account {
	
	@Id
	@Getter@Setter
	private int accountid;

	@Getter@Setter
	private int userid,cvv;
	
	@Getter@Setter
	private String cardnumber,expiry;
	
	@Getter@Setter
	private double balance;
	
	@Getter@Setter
	private String status,accounttype;
}
