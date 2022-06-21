package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Login {
	
	@Id
	@Getter@Setter
	private Integer loginid;
	@Getter@Setter
	private Integer userid;
	@Getter@Setter
	private String username,password,createddate,lastupdateddate,loginstatus;
	
}
	
	
	