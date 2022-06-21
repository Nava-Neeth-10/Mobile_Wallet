package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class WMS_Token {
	
	@Id
	@Getter@Setter
	String tokennumber;
	@Getter@Setter
	String walletid,cardnumber,cardissuer,tokenexpiry,status;

}