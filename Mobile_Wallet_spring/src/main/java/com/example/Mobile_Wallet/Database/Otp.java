package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Otp {
	
	@Id
	@Getter@Setter
	private Integer otpid;
	@Getter@Setter
	private Integer otp,attempts;
	@Getter@Setter
	private String createdon,reason,Otpkey;

}
