package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Entity
public class UserActivityLog{
	
	@Id
	@Getter@Setter
	private Integer useractivitylogid;
	@Getter@Setter
	private Integer userid;
	@Getter@Setter
	private String walletid,timestamp;
	@Getter@Setter
	private JSONObject json;
	
}
