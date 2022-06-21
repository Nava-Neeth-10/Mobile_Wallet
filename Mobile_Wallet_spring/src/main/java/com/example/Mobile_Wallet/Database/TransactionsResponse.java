package com.example.Mobile_Wallet.Database;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class TransactionsResponse {
	
	@Id
	@Getter@Setter
	private String transactionid;
	@Getter@Setter
	private String sender;
	@Getter@Setter
	private String receiver;
	@Getter@Setter
	private String type,status;
	@Getter@Setter
	private String transactiontime;
	@Getter@Setter
	private Double transactionamount;


	
	
	

}
