package com.example.Mobile_Wallet.Database;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Transactions {
	
	@Id
	@Getter@Setter
	private String transactionid;
	@Getter@Setter
	private String senderwalletid;
	@Getter@Setter
	private Integer senderuserid;
	@Getter@Setter
	private String receiverwalletid;
	@Getter@Setter
	private Integer receiveruserid;
	@Getter@Setter
	private Double transactionamount;
	@Getter@Setter
	private String transactiontype;
	@Getter@Setter
	private String senderdeviceid;
	@Getter@Setter
	private String transactiontime,responsecode;
	
	
	

}
