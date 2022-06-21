package com.example.Mobile_Wallet.Database;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class WalletConfigurationId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Getter@Setter
	private String walletid,pin,paymenttype;
	
}