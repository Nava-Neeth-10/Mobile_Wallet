package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity @AllArgsConstructor
public class bindetails {
	
	@Id
	@Getter@Setter
	private Integer Binid;
	
	@Getter@Setter
	private String bank_name,country,url,type,scheme,bin;
}
