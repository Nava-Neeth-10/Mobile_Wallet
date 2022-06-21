package com.example.Mobile_Wallet.Service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.http.HttpStatus;

import com.example.Mobile_Wallet.Response.StringResponse;

public class ValidatorService {
	
	public static StringResponse validate(Object obj)
	{
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Object>> cvs=validator.validate(obj);
		for (ConstraintViolation<Object> cv : cvs)
			return new StringResponse(cv.getMessage(),HttpStatus.BAD_REQUEST);
		return null;
	}

}
