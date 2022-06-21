package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Wallet{
	
	@Id
	@Getter@Setter
	private String walletid;
	@Getter@Setter
	private int userid;
	@Getter@Setter
	private String accounttype,wallettype,currency,status,lastupdateddate,walletname;
	@Getter@Setter
	private Double balance;

}
