package com.example.Mobile_Wallet.Request.Payment;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

public class GetBankDetailsviaEmailRequest {
	
	@Pattern(regexp=".+@.+\\..+",message="Email format incorrect")
	@Getter@Setter
	private String email;
	
}
