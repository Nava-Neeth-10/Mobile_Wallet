package com.example.Mobile_Wallet.Database;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class UserDetails {
	
	@Id
	@Getter@Setter
	private Integer userid;
	@Getter@Setter
	private String firstname,middlename,lastname,email,dob;
	@Getter@Setter
    private Integer countrycode;

}
