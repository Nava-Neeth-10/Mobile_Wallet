package com.example.Mobile_Wallet.Controller;

import java.io.FileReader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Mobile_Wallet.Database.CMS_Account;
import com.example.Mobile_Wallet.Database.*;
import com.example.Mobile_Wallet.Repository.CMS_AccountRepository;
import com.example.Mobile_Wallet.Repository.CMS_TokenRepository;
import com.example.Mobile_Wallet.Repository.CMSUserRepository;
import com.example.Mobile_Wallet.Response.JsonResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@CrossOrigin
@RestController
@RequestMapping("/CMS")
public class CMSController {
	
	@Autowired
	CMSUserRepository cmsUserRepository;
	
	@Autowired
	CMS_AccountRepository cms_accountrepository;
	
	@Autowired
	CMS_TokenRepository cms_TokenRepository;
	
	JSONObject responseMessage,jo;
	
	@EventListener(ApplicationReadyEvent.class)
	public void start() throws Exception
	{
		responseMessage = (JSONObject)new JSONParser().parse(new FileReader("src/main/resources/response.json"));
		jo=(JSONObject) responseMessage.get("CMSController");
	}
	
	@RequestMapping(value="/SendMoney",method=RequestMethod.GET)
	public ResponseEntity<?> SendMoney(@RequestParam String token,String amount)
	{
		CMS_Token cms_token=cms_TokenRepository.getByToken(token);
		if(cms_token==null)
			return new ResponseEntity<>(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		if(cms_TokenRepository.checkExpiry(token)==null)
		{
			cms_TokenRepository.deleteByToken(token);
			return new ResponseEntity<>(jo.get("RES2").toString(),HttpStatus.BAD_REQUEST);
		}
		CMS_Account cms_account=cms_accountrepository.getById(cms_token.getAccountid());
		if(cms_account.getBalance()<Double.parseDouble(amount))
			return new ResponseEntity<>(jo.get("RES3").toString(),HttpStatus.BAD_REQUEST);
		if(cms_account.getStatus().equals("INACTIVE"))
			return new ResponseEntity<>(jo.get("RES4").toString(),HttpStatus.BAD_REQUEST);
		cms_accountrepository.deduct(cms_token.getAccountid(),amount);
		return new ResponseEntity<>(jo.get("RES12").toString(),HttpStatus.OK);
	}
	
	@RequestMapping(value="/AddMoney",method=RequestMethod.GET)
	public ResponseEntity<?> AddMoney(@RequestParam String token,String amount)
	{
		CMS_Token cms_token=cms_TokenRepository.getByToken(token);
		if(cms_token==null)
			return new ResponseEntity<>(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		if(cms_TokenRepository.checkExpiry(token)==null)
		{
			cms_TokenRepository.deleteByToken(token);
			return new ResponseEntity<>(jo.get("RES2").toString(),HttpStatus.BAD_REQUEST);
		}
		cms_accountrepository.addamount(cms_token.getAccountid(),amount);
		return new ResponseEntity<>(jo.get("RES5").toString(),HttpStatus.OK);
	}
	
	@RequestMapping(value="/getaccountsviamobile",method=RequestMethod.GET)
	public ResponseEntity<?> getaccountsviamobile(@RequestParam String mobileno)
	{
		CMS_User cms_user=cmsUserRepository.getByMobile(mobileno);
		if(cms_user==null)
			return new ResponseEntity<>(jo.get("RES9").toString(),HttpStatus.BAD_REQUEST);
		List<CMS_Account> accountlist=cms_accountrepository.getByUser(cms_user.getUserid());
		if(accountlist.size()==0)
			return new ResponseEntity<>(jo.get("RES10").toString(),HttpStatus.BAD_REQUEST);
		HashMap<String,CMS_Account> accountmap=new LinkedHashMap<>();
		for(CMS_Account cms_account:accountlist)
			accountmap.put(cms_account.getCardnumber(),cms_account);
		return new ResponseEntity<Object>(accountmap,HttpStatus.OK);
	}
	
	@RequestMapping(value="/linkingaccount",method=RequestMethod.GET)
	public ResponseEntity<?> linkingaccount(@RequestParam String walletid, String cardno,String cvv,String expiry)
	{
		CMS_Account cms_Account=cms_accountrepository.getByCard(cardno, cvv, expiry);
		if(cms_Account==null)
			return new ResponseEntity<>(jo.get("RES7").toString(),HttpStatus.BAD_REQUEST);
		else if(cms_Account.getStatus().equals("INACTIVE"))
			return new ResponseEntity<>(jo.get("RES11").toString(),HttpStatus.BAD_REQUEST);
	/*	else if(cms_TokenRepository.count(cms_Account.getAccountid(),walletid)==0)
		{*/
			String token = RandomStringUtils.randomAlphanumeric(20);
			while(cms_TokenRepository.getByToken(token)!=null)
				token = RandomStringUtils.randomAlphanumeric(20);
			JSONObject tok=(JSONObject)responseMessage.get("Token");
			cms_TokenRepository.insert(token,cms_Account.getAccountid(),walletid,tok.get("expiry").toString());
			return new ResponseEntity<>(token,HttpStatus.OK);
		//}
		//return new ResponseEntity<>(jo.get("RES8").toString(),HttpStatus.BAD_REQUEST);
	}
	
    @RequestMapping(value="/delinkingaccount",method=RequestMethod.GET)
    public ResponseEntity<?> delinkingaccount(@RequestParam String token,String walletid)
    {
    	CMS_Token cms_token=cms_TokenRepository.getByToken(token);
		if(cms_token==null || !cms_token.getWalletid().equals(walletid))
			return new ResponseEntity<>(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else
			cms_TokenRepository.deleteByToken(token);
		return new ResponseEntity<>(jo.get("RES5").toString(),HttpStatus.OK);
    }
    
    @RequestMapping(value="/getaccountbalance",method=RequestMethod.GET)
    public ResponseEntity<?> getaccountbalance(@RequestParam String token)
    {
    	CMS_Token cms_token=cms_TokenRepository.getByToken(token);
		if(cms_token==null)
			return new ResponseEntity<>(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		if(cms_TokenRepository.checkExpiry(token)==null)
		{
			cms_TokenRepository.deleteByToken(token);
			return new ResponseEntity<>(jo.get("RES2").toString(),HttpStatus.BAD_REQUEST);
		}
		CMS_Account cms_account=cms_accountrepository.getById(cms_token.getAccountid());
		return new ResponseEntity<>(cms_account.getBalance(),HttpStatus.OK);
    }

}
