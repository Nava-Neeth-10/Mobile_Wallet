package com.example.Mobile_Wallet.Controller;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Mobile_Wallet.Database.User;
import com.example.Mobile_Wallet.Database.WMS_Token;
import com.example.Mobile_Wallet.Database.Wallet;
import com.example.Mobile_Wallet.Repository.OtpRepository;
import com.example.Mobile_Wallet.Repository.UserActivityLogRepository;
import com.example.Mobile_Wallet.Repository.UserRepository;
import com.example.Mobile_Wallet.Repository.WMS_TokenRepository;
import com.example.Mobile_Wallet.Repository.WalletConfigurationRepository;
import com.example.Mobile_Wallet.Repository.WalletRepository;
import com.example.Mobile_Wallet.Request.Wallet.BlockRegionRequest;
import com.example.Mobile_Wallet.Request.Wallet.ChangeWalletStatusRequest;
import com.example.Mobile_Wallet.Request.Wallet.CreateWalletRequest;
import com.example.Mobile_Wallet.Request.Wallet.DelinkCardRequest;
import com.example.Mobile_Wallet.Request.Wallet.GetCardBalanceRequest;
import com.example.Mobile_Wallet.Request.Wallet.GetPrimaryWalletRequest;
import com.example.Mobile_Wallet.Request.Wallet.GetWalletBalanceRequest;
import com.example.Mobile_Wallet.Request.Wallet.LinkCardRequest;
import com.example.Mobile_Wallet.Request.Wallet.MultipleWalletDetailsRequest;
import com.example.Mobile_Wallet.Request.Wallet.SetPrimaryWalletRequest;
import com.example.Mobile_Wallet.Request.Wallet.SingleWalletDetailsRequest;
import com.example.Mobile_Wallet.Request.Wallet.WalletsByMobileRequest;
import com.example.Mobile_Wallet.Response.JsonResponse;
import com.example.Mobile_Wallet.Response.StringResponse;
import com.example.Mobile_Wallet.Service.CMS_Service;
import com.example.Mobile_Wallet.Service.EmailService;
import com.example.Mobile_Wallet.Service.ValidatorService;
import com.fasterxml.jackson.databind.ObjectMapper;


@CrossOrigin
@RestController
@RequestMapping("/Wallet")
public class WalletController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	WalletRepository walletRepository;
	
	@Autowired
	WalletConfigurationRepository walletConfigurationRepository;
	
	@Autowired
	UserActivityLogRepository userActivityLogRepository;
	
	@Autowired
	OtpRepository otpRepository;
	
	@Autowired
	WMS_TokenRepository wms_TokenRepository;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	JSONObject responseMessage,jo;
	
	@EventListener(ApplicationReadyEvent.class)
	public void start() throws Exception
	{
		responseMessage = (JSONObject)new JSONParser().parse(new FileReader("src/main/resources/response.json"));
		jo=(JSONObject) responseMessage.get("WalletController");
	}
	
	//This API allows user to create a wallet for transacting and save money in the wallet. It is created by providing user details to the system.
	@RequestMapping(value="/CreateWallet",method=RequestMethod.PUT)
	public ResponseEntity<StringResponse> CreateWallet(@RequestBody CreateWalletRequest createWalletRequest) throws Exception 
	{
		StringResponse response=ValidatorService.validate(createWalletRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(createWalletRequest.getMobileno());
		String walletid=null;
		if(user==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else if(user.getUserstatus().equals("INACTIVE"))
			response=new StringResponse(jo.get("RES2").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			boolean flag=true;
			try
			{
				Currency.getInstance(createWalletRequest.getCurrency());
			}
			catch(Exception e)
			{
				response=new StringResponse(jo.get("RES3").toString(),HttpStatus.BAD_REQUEST);
				flag=false;
			}
			if(flag)
			{
				if(walletRepository.countByWname(createWalletRequest.getWalletname(),user.getUserid())==1) {
					response=new StringResponse("Use different Wallet Name",HttpStatus.BAD_REQUEST);
				}
				else {
//					int no_of_wallets_forthisuser=walletRepository.countByUId(user.getUserid());
//					String walletname="Wallet-"+Integer.toString(no_of_wallets_forthisuser+1);
					walletid = RandomStringUtils.randomNumeric(14);
					while(walletRepository.getWalletById(walletid)!=null)
						walletid = RandomStringUtils.randomNumeric(14);
					walletRepository.insert(walletid,user.getUserid(),createWalletRequest.getWalletname(),createWalletRequest.getCurrency(),createWalletRequest.getStatus());
					JSONObject config=(JSONObject)responseMessage.get("walletconfig");
					walletConfigurationRepository.insert(walletid,"Y","WALLET",Double.parseDouble(config.get("TransactionLimitSingle").toString()),Integer.parseInt(config.get("TransactionCountLimitDaily").toString()),Double.parseDouble(config.get("TransactionLimitDaily").toString()),Double.parseDouble(config.get("TransactionLimitMonthly").toString()),Double.parseDouble(config.get("TransactionLimitYearly").toString()),Double.parseDouble(config.get("BalanceLimit").toString()));
					response=new StringResponse(jo.get("RES4").toString(),HttpStatus.OK);
					if(user.getPrimarywallet()==null)
						userRepository.setPWId(walletid, user.getMobileno());
					}

			}
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","CreateWallet");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",createWalletRequest.getMobileno());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(user.getUserid(),walletid,json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API is used to get the wallet id of the user's primary wallet
	@RequestMapping(value="/GetPrimaryWallet",method=RequestMethod.GET)
	public ResponseEntity<StringResponse> GetPrimaryWallet(@RequestParam String mobileno){
		GetPrimaryWalletRequest getpw=new  GetPrimaryWalletRequest();
		getpw.setMobileno(mobileno);
		StringResponse response=ValidatorService.validate(getpw);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(getpw.getMobileno());
		if(user==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else if(user.getPrimarywallet()==null) {
			response=new StringResponse("No Primary Wallet",HttpStatus.BAD_REQUEST);
		}else {
			response=new StringResponse(user.getPrimarywallet(),HttpStatus.OK);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API sets the user's primary wallet in the system
	@RequestMapping(value="/SetPrimaryWallet",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> SetPrimaryWallet(@RequestBody SetPrimaryWalletRequest setpw){
		StringResponse response=ValidatorService.validate(setpw);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(setpw.getMobileno());
		if(user==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else {
			Wallet wallet=walletRepository.getWalletById(setpw.getWalletid());
			if(wallet==null)
				response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
			else {
				if(user.getUserid()!=wallet.getUserid())
					response=new StringResponse("This wallet is not linked to this user",HttpStatus.BAD_REQUEST);
				else {
					userRepository.setPWId(setpw.getWalletid(), setpw.getMobileno());
					response=new StringResponse("Success",HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API is used to make the wallet active or inactive based on the user's choice. 
	@RequestMapping(value="/ChangeWalletStatus",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> ChangeWalletStatus(@RequestBody ChangeWalletStatusRequest changeWalletStatusRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(changeWalletStatusRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(changeWalletStatusRequest.getMobileno());
		Wallet wallet=walletRepository.getWalletById(changeWalletStatusRequest.getWalletid());
		if(user==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else if(user.getUserstatus().equals("INACTIVE"))
			response=new StringResponse(jo.get("RES2").toString(),HttpStatus.BAD_REQUEST);
		else if(wallet==null)
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else if(wallet.getStatus().equals(changeWalletStatusRequest.getStatus()))
			response=new StringResponse("Wallet is already"+wallet.getStatus(),HttpStatus.BAD_REQUEST);
		else
		{
			if(wallet.getStatus().equals("ACTIVE") && walletRepository.checkStatusTime(wallet.getWalletid())==null)
				response=new StringResponse(jo.get("RES6").toString(),HttpStatus.BAD_REQUEST);
			
				Integer otpid=otpRepository.getMaxOtpId()+1;
				int otp=(int)(Math.random()*9000+1000);
				JSONObject ot=(JSONObject)responseMessage.get("OTP");
				otpRepository.insert(otpid,otp,"WALLET",wallet.getWalletid()+","+changeWalletStatusRequest.getStatus(),ot.get("attempts").toString());
				userRepository.updateOtpId(changeWalletStatusRequest.getMobileno(),otpid);
				EmailService.send_mail("Mobile Wallet Status Change OTP","Your OTP is "+otp);
				response=new StringResponse(jo.get("RES20").toString(),HttpStatus.OK);

			
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","ChangeWalletStatus");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",changeWalletStatusRequest.getMobileno());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(user.getUserid(),changeWalletStatusRequest.getWalletid(),json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API retrieves details about a particular wallet held by the user and the wallet is chosen by the user to view details.
	@RequestMapping(value="/SingleWalletDetails",method=RequestMethod.GET)
	public ResponseEntity<?> SingleWalletDetails(@RequestParam String walletid) throws Exception
	{
		SingleWalletDetailsRequest singleWalletDetailsRequest = new SingleWalletDetailsRequest();
		singleWalletDetailsRequest.setWalletid(walletid);
		StringResponse response=ValidatorService.validate(singleWalletDetailsRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		Wallet wallet=walletRepository.getWalletById(singleWalletDetailsRequest.getWalletid());
		if(wallet==null)
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			response=new StringResponse(jo.get("RES7").toString(),HttpStatus.OK);
			JsonResponse jsonResponse=new JsonResponse(wallet,HttpStatus.OK);
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","SingleWalletDetails");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",singleWalletDetailsRequest.getWalletid());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(wallet.getUserid(),singleWalletDetailsRequest.getWalletid(),json);
			return new ResponseEntity<JsonResponse>(jsonResponse,jsonResponse.getResponseCode());
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	
	
	
	//This API retrieves details of every wallets held by the user. It also allows user to choose the wallet based on the status and sorting option. 
	@RequestMapping(value="/MultipleWalletDetails",method=RequestMethod.GET)
	public ResponseEntity<?> walletinfo(@RequestParam String mobileno,String sortby,String status) throws Exception
	{
		MultipleWalletDetailsRequest multipleWalletDetailsRequest = new MultipleWalletDetailsRequest();
		multipleWalletDetailsRequest.setMobileno(mobileno);
		multipleWalletDetailsRequest.setSortby(sortby);
		multipleWalletDetailsRequest.setStatus(status);
		StringResponse response=ValidatorService.validate(multipleWalletDetailsRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(multipleWalletDetailsRequest.getMobileno());
		List<Wallet> walletlist=new ArrayList<Wallet>();
		HashMap<String,Wallet> walletmap=new LinkedHashMap<>();

		JsonResponse jsonResponse = null;
		if(multipleWalletDetailsRequest.getStatus().equals("ALL"))
			multipleWalletDetailsRequest.setStatus("%");
		else
			multipleWalletDetailsRequest.setStatus(multipleWalletDetailsRequest.getStatus()+"%");
		if(user==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			walletlist=walletRepository.getByUserId(user.getUserid(),multipleWalletDetailsRequest.getStatus());
			if(walletlist.size()==0)
				response=new StringResponse(jo.get("RES8").toString(),HttpStatus.BAD_REQUEST);
			else
			{
				if(multipleWalletDetailsRequest.getSortby().equals("ASC"))
					walletlist.sort(Comparator.comparing(Wallet::getWalletid));
				else
					walletlist.sort(Comparator.comparing(Wallet::getWalletid).reversed());
				for(Wallet wallet:walletlist)
					walletmap.put(wallet.getWalletid()+","+wallet.getWalletname() ,wallet);
				jsonResponse=new JsonResponse(walletmap,HttpStatus.OK);
				response=new StringResponse(jo.get("RES7").toString(),HttpStatus.OK);
				HashMap<String,String> map=new LinkedHashMap<>();
				map.put("ActivityType","MultipleWalletDetails");
				map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
				map.put("Description",response.getResponse());
				map.put("CreatedBy",user.getMobileno());
				String json=objectMapper.writeValueAsString(map);
				userActivityLogRepository.insert(user.getUserid(),"null",json);
				return new ResponseEntity<JsonResponse>(jsonResponse,jsonResponse.getResponseCode());
			}
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());

	}
	
	//This API will block a region for any wallet
	@RequestMapping(value="/BlockRegion",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> BlockRegion(@RequestBody BlockRegionRequest blockRegionRequest)
	{
		StringResponse response=ValidatorService.validate(blockRegionRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		List<String> regions = new ArrayList<>(Arrays.asList(Locale.getISOCountries()));
		Wallet wallet=walletRepository.getWalletById(blockRegionRequest.getWalletid());
		if(!regions.contains(blockRegionRequest.getRegion()))
			response=new StringResponse(jo.get("RES9").toString(),HttpStatus.BAD_REQUEST);
		else if(wallet==null)
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			List<String> region=new ArrayList<>(Arrays.asList(walletConfigurationRepository.getRegionById(blockRegionRequest.getWalletid()).split(",")));
			if(region.contains(blockRegionRequest.getRegion()))
				response=new StringResponse(jo.get("RES10").toString(),HttpStatus.BAD_REQUEST);
			else
			{
				walletConfigurationRepository.updateRegion(blockRegionRequest.getWalletid(),blockRegionRequest.getRegion());
				response=new StringResponse(jo.get("RES11").toString(),HttpStatus.OK);
			}
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API provides the list of cards attached to wallet
	@RequestMapping(value="/CardList",method=RequestMethod.GET)
	public ResponseEntity<?> CardList(@RequestParam String walletid) throws Exception
	{
		SingleWalletDetailsRequest singleWalletDetailsRequest=new SingleWalletDetailsRequest();
		singleWalletDetailsRequest.setWalletid(walletid);
		StringResponse response=ValidatorService.validate(singleWalletDetailsRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		ArrayList<String> wallet=wms_TokenRepository.getCardById(singleWalletDetailsRequest.getWalletid());
		if(wallet==null)
			response=new StringResponse(jo.get("RES19").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			JsonResponse jsonResponse=new JsonResponse(wallet,HttpStatus.OK);
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","CardList");
			map.put("ResponseCode",jsonResponse.getResponseCode().toString());
			map.put("Description",jsonResponse.getResponse().toString());
			map.put("CreatedBy",singleWalletDetailsRequest.getWalletid());
			String json=objectMapper.writeValueAsString(map);
			//userActivityLogRepository.insert(walletRepository.getUserId(singleWalletDetailsRequest.getWalletid()),singleWalletDetailsRequest.getWalletid(),json);
			return new ResponseEntity<JsonResponse>(jsonResponse,jsonResponse.getResponseCode());
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API will link a Card to wallet for 1 hour
	@RequestMapping(value="/LinkCard",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> LinkCard(@RequestBody LinkCardRequest linkCardRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(linkCardRequest);
	    if(response!=null)
	    	return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	    Wallet wallet=walletRepository.getWalletById(linkCardRequest.getWalletid());
	    if(wallet==null)
	    	response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
	    else if(wallet.getStatus().equals("INACTIVE"))
	    	response=new StringResponse(jo.get("RES12").toString(),HttpStatus.BAD_REQUEST);
	    else
	    {
		    String cms_response=CMS_Service.Linking(linkCardRequest.getCardno(),linkCardRequest.getCvv(),linkCardRequest.getExpiry(),linkCardRequest.getWalletid());
		    if(!cms_response.matches("\\w{20}"))
		    	response=new StringResponse(cms_response,HttpStatus.BAD_REQUEST);
		    else
		    {
		    	JSONObject tok=(JSONObject)responseMessage.get("Token");
		    	if(wms_TokenRepository.getByCard(linkCardRequest.getCardno().substring(12,16),linkCardRequest.getCardissuer(),linkCardRequest.getWalletid())==null)
		    		wms_TokenRepository.insert(cms_response,linkCardRequest.getWalletid(),linkCardRequest.getCardno().substring(12,16),linkCardRequest.getCardissuer(),tok.get("expiry").toString());
		    	else
		    		wms_TokenRepository.changeToken(cms_response,linkCardRequest.getCardno().substring(12,16),linkCardRequest.getCardissuer(),linkCardRequest.getWalletid());
		    	response=new StringResponse(jo.get("RES13").toString(),HttpStatus.OK);
		    }
	    }
	    return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This will delink the card from wallet
	@RequestMapping(value="/DelinkCard",method=RequestMethod.POST)
	public ResponseEntity<?> DelinkCard(@RequestBody DelinkCardRequest delinkCardRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(delinkCardRequest);
	    if(response!=null)
	    	return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	    Wallet wallet=walletRepository.getWalletById(delinkCardRequest.getWalletid());
		WMS_Token wms_Token=wms_TokenRepository.getByCard(delinkCardRequest.getCardno(),delinkCardRequest.getCardissuer(),delinkCardRequest.getWalletid());
		if(wallet==null)
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else if(wms_Token==null)
			response=new StringResponse(jo.get("RES14").toString(),HttpStatus.BAD_REQUEST);
		else
		{
		    String cms_response=CMS_Service.Delinking(wms_Token.getTokennumber(),delinkCardRequest.getWalletid());
		    if(!cms_response.equals("Success"))
		    	response=new StringResponse(cms_response,HttpStatus.BAD_REQUEST);
		    else
		    {
		    	wms_TokenRepository.changeStatus(wms_Token.getTokennumber(),"INACTIVE");
		    	response=new StringResponse(jo.get("RES15").toString(),HttpStatus.OK);
		    }
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}

	//This API will get the Card Balance from CMS
	@RequestMapping(value="/GetCardBalance",method=RequestMethod.GET)
	public ResponseEntity<?> GetCardBalance(@RequestParam String walletid,String cardno, String cardissuer ) throws Exception
	{
		GetCardBalanceRequest getCardBalanceRequest=new GetCardBalanceRequest();
		getCardBalanceRequest.setCardissuer(cardissuer);
		getCardBalanceRequest.setCardno(cardno);
		getCardBalanceRequest.setWalletid(walletid);
		StringResponse response=ValidatorService.validate(getCardBalanceRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		Wallet wallet=walletRepository.getWalletById(getCardBalanceRequest.getWalletid());
		WMS_Token wms_Token=wms_TokenRepository.getByCard(getCardBalanceRequest.getCardno(),getCardBalanceRequest.getCardissuer(),getCardBalanceRequest.getWalletid());
		if(wallet==null)
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else if(wallet.getStatus().equalsIgnoreCase("INACTIVE"))
			response=new StringResponse(jo.get("RES12").toString(),HttpStatus.BAD_REQUEST);
		else if(wms_Token==null)
			response=new StringResponse(jo.get("RES14").toString(),HttpStatus.BAD_REQUEST);
		else if(wms_Token.getStatus().equals("INACTIVE"))
			response=new StringResponse(jo.get("RES16").toString(),HttpStatus.BAD_REQUEST);
		else 
		{
			WMS_Token wms_token=wms_TokenRepository.checkExpiry(wms_Token.getTokennumber());
			if(wms_token==null)
				response=new StringResponse(jo.get("RES17").toString(),HttpStatus.BAD_REQUEST);
			else
			{
				String cms_response=CMS_Service.GetBalance(wms_token.getTokennumber());	
				if(!cms_response.matches("\\d*\\.\\d*"))
					response=new StringResponse(cms_response,HttpStatus.BAD_REQUEST);
				else
					response=new StringResponse(cms_response,HttpStatus.OK);
			}
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());			
	}
	
	//This API will get all the walletId's linked with a mobile number
	@RequestMapping(value="/WalletsByMobile",method=RequestMethod.GET)
	public ResponseEntity<?> WalletsByMobile(@RequestBody WalletsByMobileRequest walletsByMobileRequest)throws Exception
	{
		StringResponse response=ValidatorService.validate(walletsByMobileRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(walletsByMobileRequest.getMobileno());
		if(user==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			List<Wallet> walletlist=walletRepository.getByUserId(user.getUserid(),"%");
			List<String> walletidlist=new ArrayList<String>();
			for(Wallet wallet:walletlist)
				walletidlist.add(wallet.getWalletid());
			if(walletlist.size()==0)
				response=new StringResponse(jo.get("RES8").toString(),HttpStatus.BAD_REQUEST);
			else
			{
				JsonResponse jsonresponse=new JsonResponse(walletidlist,HttpStatus.OK);
				return new ResponseEntity<JsonResponse>(jsonresponse,jsonresponse.getResponseCode());
			}
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API will get the Wallet Balance
	@RequestMapping(value="/GetWalletBalance",method=RequestMethod.GET)
	public ResponseEntity<StringResponse> GetWalletBalance(@RequestParam String walletid) throws Exception
	{
		GetWalletBalanceRequest getWalletBalanceRequest=new GetWalletBalanceRequest();
		getWalletBalanceRequest.setWalletid(walletid);
		StringResponse response=ValidatorService.validate(getWalletBalanceRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		Wallet wallet=walletRepository.getWalletById(getWalletBalanceRequest.getWalletid());
		if(wallet==null)
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else
			response=new StringResponse(wallet.getBalance().toString()+" "+wallet.getCurrency(),HttpStatus.OK);
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}

}