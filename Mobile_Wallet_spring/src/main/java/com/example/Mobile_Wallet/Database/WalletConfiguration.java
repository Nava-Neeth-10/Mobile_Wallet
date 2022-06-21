package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(WalletConfigurationId.class)
public class WalletConfiguration{
	
	@Id
	private String walletid,pin,paymenttype;
	@Getter@Setter
	private String region,wallettype;
	@Getter@Setter
	private Double transactionlimitsingle,transactionlimitdaily,transactionlimitmonthly,transactionlimityearly,balancelimit;
	@Getter@Setter
	private Integer transactioncountlimitdaily;
	
}