package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class CMS_Token {
	
	@Id
	@Getter@Setter
	Integer tokenid;
	@Getter@Setter
	String tokennumber,walletid,tokenexpiry,createddate;
	@Getter@Setter
	Integer accountid;

}