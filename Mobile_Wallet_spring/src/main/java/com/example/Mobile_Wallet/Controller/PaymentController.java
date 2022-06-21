package com.example.Mobile_Wallet.Controller;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

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
import org.springframework.web.client.RestTemplate;

import com.example.Mobile_Wallet.Database.Transactions;
import com.example.Mobile_Wallet.Database.TransactionsResponse;
import com.example.Mobile_Wallet.Database.User;
import com.example.Mobile_Wallet.Database.WMS_Token;
import com.example.Mobile_Wallet.Database.Wallet;
import com.example.Mobile_Wallet.Database.WalletConfiguration;
import com.example.Mobile_Wallet.Repository.TransactionsRepository;
import com.example.Mobile_Wallet.Repository.UserRepository;
import com.example.Mobile_Wallet.Repository.WMS_TokenRepository;
import com.example.Mobile_Wallet.Repository.WalletConfigurationRepository;
import com.example.Mobile_Wallet.Repository.WalletRepository;
import com.example.Mobile_Wallet.Request.Payment.AddFromBankRequest;
import com.example.Mobile_Wallet.Request.Payment.AddToBankRequest;
import com.example.Mobile_Wallet.Request.Payment.GetBankDetailsviaEmailRequest;
import com.example.Mobile_Wallet.Request.Payment.GetBinDetailsRequest;
import com.example.Mobile_Wallet.Request.Payment.GetUserTransactionsRequest;
import com.example.Mobile_Wallet.Request.Payment.GetWalletTransactionsRequest;
import com.example.Mobile_Wallet.Request.Payment.SendToFriendRequest;
import com.example.Mobile_Wallet.Request.Payment.SendToMultipleRequest;
import com.example.Mobile_Wallet.Request.Wallet.WalletsByMobileRequest;
import com.example.Mobile_Wallet.Response.JsonResponse;
import com.example.Mobile_Wallet.Response.StringResponse;
import com.example.Mobile_Wallet.Service.CMS_Service;
import com.example.Mobile_Wallet.Service.EmailService;
import com.example.Mobile_Wallet.Service.ValidatorService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@CrossOrigin
@RestController
@RequestMapping("/Payment")
public class PaymentController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	WalletRepository walletRepository;
	
	@Autowired
	WalletConfigurationRepository walletConfigurationRepository;
	
	@Autowired
	WMS_TokenRepository wms_TokenRepository;
	
	@Autowired
	TransactionsRepository transactionsRepository;
	
	JSONObject responseMessage,jo;
	
	@EventListener(ApplicationReadyEvent.class)
	public void start() throws Exception
	{
		responseMessage = (JSONObject)new JSONParser().parse(new FileReader("src/main/resources/response.json"));
		jo=(JSONObject) responseMessage.get("PaymentController");
	}
	
	@RequestMapping(value="/AddFromBank",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> AddFromBank(@RequestBody AddFromBankRequest addFromBankRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(addFromBankRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		Wallet wallet=walletRepository.getWalletById(addFromBankRequest.getWalletid());
		WMS_Token wms_Token=wms_TokenRepository.getByCard(addFromBankRequest.getCardno(),addFromBankRequest.getCardissuer(),addFromBankRequest.getWalletid());
		if(wallet==null)
			response=new StringResponse(jo.get("RES2").toString(),HttpStatus.BAD_REQUEST);
		else if(wallet.getStatus().equals("INACTIVE"))
			response=new StringResponse(jo.get("RES3").toString(),HttpStatus.BAD_REQUEST);
		else if(wms_Token==null)
			response=new StringResponse(jo.get("RES4").toString(),HttpStatus.BAD_REQUEST);
		else if(wms_Token.getStatus().equals("INACTIVE"))
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			String TransactionStatus=CheckTransactionLimitReceiver(wallet.getWalletid(),Double.parseDouble(addFromBankRequest.getAmount()));
			if(!TransactionStatus.equals("Approved"))
			{
				transactionhistory(null,wms_Token.getCardissuer()+wms_Token.getCardnumber(),wallet.getUserid(),wallet.getWalletid(),Double.parseDouble(addFromBankRequest.getAmount()),"BT",null,"400");
				response=new StringResponse(TransactionStatus,HttpStatus.BAD_REQUEST);
			}
			else
			{
			    String cms_response=CMS_Service.SendMoney(wms_Token.getTokennumber(),addFromBankRequest.getAmount());
			    if(cms_response.equals("Token Expired"))
			    {
			    	transactionhistory(null,wms_Token.getCardissuer()+wms_Token.getCardnumber(),wallet.getUserid(),wallet.getWalletid(),Double.parseDouble(addFromBankRequest.getAmount()),"BT",null,"400");
			    	wms_TokenRepository.changeStatus(wms_Token.getTokennumber(),"INACTIVE");
			    	response=new StringResponse(jo.get("RES9").toString(),HttpStatus.BAD_REQUEST);
			    }
			    else if(!cms_response.equals("Success"))
			    {
			    	transactionhistory(null,wms_Token.getCardissuer()+wms_Token.getCardnumber(),wallet.getUserid(),wallet.getWalletid(),Double.parseDouble(addFromBankRequest.getAmount()),"BT",null,"400");
			    	response=new StringResponse(cms_response,HttpStatus.BAD_REQUEST);
			    }
				else
				{
					response=new StringResponse(cms_response,HttpStatus.OK);
					walletRepository.addMoney(addFromBankRequest.getWalletid(),addFromBankRequest.getAmount());
					transactionhistory(null,wms_Token.getCardissuer()+wms_Token.getCardnumber(),wallet.getUserid(),wallet.getWalletid(),Double.parseDouble(addFromBankRequest.getAmount()),"BT",null,"200");
				}
			}
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	@RequestMapping(value="/AddToBank",method=RequestMethod.POST)
	public ResponseEntity<?> AddToBank(@RequestBody AddToBankRequest addToBankRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(addToBankRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		Wallet wallet=walletRepository.getWalletById(addToBankRequest.getWalletid());
		WMS_Token wms_Token=wms_TokenRepository.getByCard(addToBankRequest.getCardno(),addToBankRequest.getCardissuer(),addToBankRequest.getWalletid());
		if(wallet==null)
			response=new StringResponse(jo.get("RES2").toString(),HttpStatus.BAD_REQUEST);
		else if(wallet.getStatus().equalsIgnoreCase("INACTIVE")) 
			response=new StringResponse(jo.get("RES3").toString(),HttpStatus.BAD_REQUEST);
		else if(wms_Token==null)
			response=new StringResponse(jo.get("RES4").toString(),HttpStatus.BAD_REQUEST);
		else if(wms_Token.getStatus().equals("INACTIVE"))
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			String TransactionStatus=CheckTransactionLimitSender(addToBankRequest.getWalletid(),Double.parseDouble(addToBankRequest.getAmount()));
			if(!TransactionStatus.equals("Approved")) {
				transactionhistory(wallet.getUserid(),wallet.getWalletid(),null,wms_Token.getCardissuer()+wms_Token.getCardnumber(),Double.parseDouble(addToBankRequest.getAmount()),"BT",null,"400");
				response=new StringResponse(TransactionStatus,HttpStatus.BAD_REQUEST);
				return new ResponseEntity<StringResponse>(response,response.getResponseCode());
			}
			WMS_Token wms_token=wms_TokenRepository.checkExpiry(wms_Token.getTokennumber());
			if(wms_token==null) {
				JsonResponse response1=new JsonResponse(jo.get("RES8").toString(),HttpStatus.BAD_REQUEST); 
				transactionhistory(wallet.getUserid(),wallet.getWalletid(),null,wms_Token.getCardissuer()+wms_Token.getCardnumber(),Double.parseDouble(addToBankRequest.getAmount()),"BT",null,"400");
				return new ResponseEntity<JsonResponse>(response1,response1.getResponseCode());
			}			
			String cmsResponse=CMS_Service.AddMoney(wms_token.getTokennumber(),addToBankRequest.getAmount());	
			if(!cmsResponse.equals("Success")) {
				transactionhistory(wallet.getUserid(),wallet.getWalletid(),null,wms_Token.getCardissuer()+wms_Token.getCardnumber(),Double.parseDouble(addToBankRequest.getAmount()),"BT",null,"400");
		    	response=new StringResponse(cmsResponse,HttpStatus.BAD_REQUEST);
			}
			else {
				response=new StringResponse(cmsResponse,HttpStatus.OK);
				walletRepository.deductMoney(addToBankRequest.getWalletid(),addToBankRequest.getAmount());
				transactionhistory(wallet.getUserid(),wallet.getWalletid(),null,wms_Token.getCardissuer()+wms_Token.getCardnumber(),Double.parseDouble(addToBankRequest.getAmount()),"BT",null,"200");
			}
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());	
	}
	
	@RequestMapping(value="/SendToFriend",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> SendToFriend(@RequestBody SendToFriendRequest sendToFriendRequest)throws Exception
	{
		StringResponse response=ValidatorService.validate(sendToFriendRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		Wallet senderwallet=walletRepository.getWalletById(sendToFriendRequest.getSenderwalletid());
		Wallet receiverwallet=walletRepository.getWalletById(userRepository.getPWId(sendToFriendRequest.getReceivermobileno()));
		if(senderwallet==null)
			response=new StringResponse(jo.get("RES23").toString(),HttpStatus.BAD_REQUEST);
		else if(senderwallet.getStatus().equals("INACTIVE"))
			response=new StringResponse(jo.get("RES24").toString(),HttpStatus.BAD_REQUEST);
		else if(receiverwallet==null)
			response=new StringResponse(jo.get("RES25").toString(),HttpStatus.BAD_REQUEST);
		else if(receiverwallet.getStatus().equals("INACTIVE"))
			response=new StringResponse(jo.get("RES26").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			String TransactionStatus=CheckTransactionLimitSender(senderwallet.getWalletid(),Double.parseDouble(sendToFriendRequest.getMoney()));
			if(!TransactionStatus.equals("Approved"))
			{
				transactionhistory(senderwallet.getUserid(),senderwallet.getWalletid(),receiverwallet.getUserid(),receiverwallet.getWalletid(),Double.parseDouble(sendToFriendRequest.getMoney()),"NT",sendToFriendRequest.getDeviceid(),"400");
				response=new StringResponse("Sender "+TransactionStatus,HttpStatus.BAD_REQUEST);
			}
			else
			{
				TransactionStatus=CheckTransactionLimitReceiver(receiverwallet.getWalletid(),Double.parseDouble(sendToFriendRequest.getMoney()));
				if(!TransactionStatus.equals("Approved"))
				{
					transactionhistory(senderwallet.getUserid(),senderwallet.getWalletid(),receiverwallet.getUserid(),receiverwallet.getWalletid(),Double.parseDouble(sendToFriendRequest.getMoney()),"NT",sendToFriendRequest.getDeviceid(),"400");
					response=new StringResponse("Receiver "+TransactionStatus,HttpStatus.BAD_REQUEST);
				}
				else
				{
					walletRepository.deduct(senderwallet.getWalletid(),sendToFriendRequest.getMoney());
					walletRepository.addMoney(receiverwallet.getWalletid(),sendToFriendRequest.getMoney());
					transactionhistory(senderwallet.getUserid(),senderwallet.getWalletid(),receiverwallet.getUserid(),receiverwallet.getWalletid(),Double.parseDouble(sendToFriendRequest.getMoney()),"NT",sendToFriendRequest.getDeviceid(),"200");
					response=new StringResponse("Success",HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	@RequestMapping(value="/SendToMultiple",method=RequestMethod.POST)
	public ResponseEntity<?> TransferToFriendsWallet(@RequestBody SendToMultipleRequest sendToMultipleRequest)throws Exception{
		StringResponse response=ValidatorService.validate(sendToMultipleRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		String receivers[]=sendToMultipleRequest.getReceivermobileno().split(",");
		HashMap<String,StringResponse> responsemap=new LinkedHashMap<>();
		int amount=Integer.parseInt(sendToMultipleRequest.getMoney())/receivers.length;
		for(String receiver:receivers)
		{
			SendToFriendRequest req=new SendToFriendRequest();
			req.setDeviceid(sendToMultipleRequest.getDeviceid());
			req.setMoney(String.valueOf(amount));
			req.setSenderwalletid(sendToMultipleRequest.getSenderwalletid());
			req.setReceivermobileno(receiver);
			ResponseEntity<StringResponse> res=SendToFriend(req);
			responsemap.put(receiver,res.getBody());
		}
		JsonResponse jsonResponse=new JsonResponse(responsemap,HttpStatus.OK);
		return new ResponseEntity<JsonResponse>(jsonResponse,jsonResponse.getResponseCode());
	}
	
	public String CheckTransactionLimitSender(String walletid,Double money) {
		String result="Approved";
		Wallet wallet=walletRepository.getWalletById(walletid);
		WalletConfiguration walletconfig=walletConfigurationRepository.getById(walletid,"Y","WALLET");
		if(wallet.getBalance()<money)
			result=jo.get("RES17").toString();		
		else if(walletconfig.getTransactionlimitsingle()!=null && walletconfig.getTransactionlimitsingle()<money)
			result=jo.get("RES18").toString();
		else if(walletconfig.getTransactioncountlimitdaily()!=null && 1+transactionsRepository.getDailyCountSender(walletid)>walletconfig.getTransactioncountlimitdaily())
			result=jo.get("RES19").toString();
		else if(walletconfig.getTransactionlimitdaily()!=null && money+transactionsRepository.getDailySender(walletid)>walletconfig.getTransactionlimitdaily())
			result=jo.get("RES20").toString();
		else if(walletconfig.getTransactionlimitmonthly()!=null && money+transactionsRepository.getMonthlySender(walletid)>walletconfig.getTransactionlimitmonthly())
			result=jo.get("RES21").toString();
		else if(walletconfig.getTransactionlimityearly()!=null && money+transactionsRepository.getYearlySender(walletid)>walletconfig.getTransactionlimityearly())
			result=jo.get("RES22").toString();
		return result;
	}
	
	public String CheckTransactionLimitReceiver(String walletid,Double money) {
		String result="Approved";
		Wallet wallet=walletRepository.getWalletById(walletid);
		WalletConfiguration walletconfig=walletConfigurationRepository.getById(walletid,"Y","WALLET");
		if(wallet.getBalance()+money>walletconfig.getBalancelimit())
			result=jo.get("RES27").toString();		
		else if(walletconfig.getTransactionlimitsingle()!=null && walletconfig.getTransactionlimitsingle()<money)
			result=jo.get("RES18").toString();
		else if(walletconfig.getTransactioncountlimitdaily()!=null && 1+transactionsRepository.getDailyCountReceiver(walletid)>walletconfig.getTransactioncountlimitdaily())
			result=jo.get("RES19").toString();
		else if(walletconfig.getTransactionlimitdaily()!=null && money+transactionsRepository.getDailyReceiver(walletid)>walletconfig.getTransactionlimitdaily())
			result=jo.get("RES20").toString();
		else if(walletconfig.getTransactionlimitmonthly()!=null && money+transactionsRepository.getMonthlyReceiver(walletid)>walletconfig.getTransactionlimitmonthly())
			result=jo.get("RES21").toString();
		else if(walletconfig.getTransactionlimityearly()!=null && money+transactionsRepository.getYearlyReceiver(walletid)>walletconfig.getTransactionlimityearly())
			result=jo.get("RES22").toString();
		return result;
	}
	
	public void TransactionReceipt(String txid,String senderwalletid,String receiverwalletid,String amount,String status) throws Exception
	{
		Wallet senderwallet=walletRepository.getWalletById(senderwalletid);
		Wallet receiverwallet=walletRepository.getWalletById(receiverwalletid);
		String body="TransactionID :"+txid;
		if(senderwallet!=null)
			body+="\nFrom Wallet :"+senderwalletid;
		if(receiverwallet!=null)
			body+="\nTo Mobile Number :"+userRepository.getById(receiverwallet.getUserid()).getMobileno()+"\nTo Wallet :"+receiverwalletid;
		body+="\nAmount :"+amount+"\nStatus :";
		if(status.equals("200"))
			body+="Success";
		else
			body+="Failed";
		EmailService.send_mail("Transaction receipt",body);
	}
	
	public void transactionhistory(Integer senderuserid,String senderwalletid,Integer receiveruserid,String receiverwalletid,Double transactionamount,String transactiontype,String deviceid,String responsecode) throws Exception
	{
		String sample="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Date d=new Date();
        int sequence=(int)(Math.random()*9000+1000);
        SimpleDateFormat ft =new SimpleDateFormat ("yyyy,MM,dd,HH,mm,ss");
        String transactionid="";
        String date=ft.format(d);
        String[] dates=date.split(",");
        transactionid+=dates[0];
        for(int i=1;i<dates.length;i++)
        {
            char c=sample.charAt(Integer.parseInt(dates[i]));
            transactionid+=c;
        }
        while(true)
        {
	        try
	        {
	        	transactionid+=String.format("%04d", sequence);
	        	transactionsRepository.insert(transactionid, senderuserid, senderwalletid, receiveruserid, receiverwalletid, transactionamount, transactiontype, deviceid, responsecode);
	        	break;
	        }
	        catch(Exception e)
	        {
	        	continue;
	        }
        }
        TransactionReceipt(transactionid,senderwalletid,receiverwalletid,transactionamount.toString(),responsecode);
	}
	
	//This API integrates a third party website to validate the BIN provided and the details of the BIN are returned as a result if the provided BIN is a valid number.
	@RequestMapping(value="/GetBinDetails",method=RequestMethod.POST)
	public ResponseEntity<?> GetBinDetails(@RequestBody GetBinDetailsRequest getBinDetailsRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(getBinDetailsRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		System.out.println(getBinDetailsRequest.getBinnumber());
		Request request = new Request.Builder().url("https://api.promptapi.com/bincheck/"+getBinDetailsRequest.getBinnumber()).addHeader("apikey", "sJ6mOzbrAknDBAYQdloLS7RHN8LsY7MJ").build();
		Response response1 = client.newCall(request).execute();
		JSONParser parser = new JSONParser();  
		JSONObject json = (JSONObject) parser.parse(response1.body().string());
		JsonResponse jsonresponse=new JsonResponse(json,HttpStatus.ACCEPTED);
		return new ResponseEntity<>(jsonresponse,jsonresponse.getResponseCode());
	}
	
	@RequestMapping(value="/GetBankAccountsviaMobile",method=RequestMethod.POST)
	public ResponseEntity<?> GetBankDetailsviaMobile(@RequestBody WalletsByMobileRequest getbankaccountsRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(getbankaccountsRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		String uri = "http://localhost:8080/CMS/getaccountsviamobile?mobileno="+getbankaccountsRequest.getMobileno();
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<JsonResponse> CMSresponse = restTemplate.getForEntity(uri, JsonResponse.class);
		//ResponseEntity<JsonResponse> CMSresponse=new CMSController().getaccountsviamobile(getbankaccountsRequest.getMobileNo());
		@SuppressWarnings("unchecked")
		HashMap<String,LinkedHashMap<String,String>> Accountlist = (HashMap<String,LinkedHashMap<String,String>>)CMSresponse.getBody().getResponse();
		System.out.println(Accountlist.size());
		if(Accountlist.isEmpty())
		{
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
	        return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		}
		HashMap<Object,Object> Finallist=new LinkedHashMap<>();
		for (Entry<String,LinkedHashMap<String,String>> mapElement : Accountlist.entrySet())
		{
			if(mapElement.getValue().get("status").equals("ACTIVE"))
				Finallist.put(mapElement.getValue().get("accountno"), mapElement.getValue());
			else
				Finallist.put(mapElement.getValue().get("accountno"),"Account is Inactive");		
		}
		JsonResponse jsonResponseF = new JsonResponse(Finallist,HttpStatus.OK);
		return new ResponseEntity<JsonResponse>(jsonResponseF,jsonResponseF.getResponseCode());			
	}
	
	@RequestMapping(value="/GetBankAccountsviaEmail",method=RequestMethod.POST)
	public ResponseEntity<?> GetBankAccountsviaEmail(@RequestBody GetBankDetailsviaEmailRequest getbankdetailsviaEmail) throws Exception
	{
		StringResponse response=ValidatorService.validate(getbankdetailsviaEmail);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		String mobileno =userRepository.getByEmail(getbankdetailsviaEmail.getEmail()).getMobileno();
		WalletsByMobileRequest requestclass=new WalletsByMobileRequest();
		requestclass.setMobileno(mobileno);
		return GetBankDetailsviaMobile(requestclass);
	}
	
	public ArrayList<String> WalletsByMobile(String mobileno)throws Exception
	{
		//ArrayList<String> ar=new ArrayList<String>();
		User user=userRepository.getByMobileNo(mobileno);
		List<Wallet> walletlist=walletRepository.getByUserId(user.getUserid(),"%");
		ArrayList<String> walletidlist=new ArrayList<String>();
		for(Wallet wallet:walletlist)
			walletidlist.add(wallet.getWalletid());
		return walletidlist;
	}
	
	
	@RequestMapping(value="/GetUserTransactions",method=RequestMethod.GET)
	public ResponseEntity<?> GetUserTransactions(@RequestParam String mobileno ) throws Exception
	{
		GetUserTransactionsRequest getUserTransactions=new GetUserTransactionsRequest();
		getUserTransactions.setMobileno(mobileno);
		StringResponse response=ValidatorService.validate(getUserTransactions);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(getUserTransactions.getMobileno());
		ArrayList<String> ar=WalletsByMobile(mobileno);
		if(ar.isEmpty()) {
			return new ResponseEntity<StringResponse>(new StringResponse("No wallets",HttpStatus.BAD_REQUEST),HttpStatus.BAD_REQUEST);			
		}
		ArrayList<Transactions> t=transactionsRepository.getUserTransactions(ar);
		HashMap<String,TransactionsResponse> transmap=new LinkedHashMap<>();
		if(!t.isEmpty()) {
			for(Transactions trans: t) {
				TransactionsResponse tr=new TransactionsResponse();		
				tr.setTransactionid(trans.getTransactionid());
				if(trans.getResponsecode().equals("200")) {
					tr.setStatus("Success");
				}else {
					tr.setStatus("Failed");
				}
				if(trans.getSenderuserid()!=user.getUserid()) {
					if(trans.getSenderwalletid().length()==14){
						tr.setSender(walletRepository.getMobileFromWId(trans.getSenderwalletid()));
					}else {
						tr.setSender(trans.getSenderwalletid());
					}	
					tr.setReceiver(walletRepository.getWnameFromWId(trans.getReceiverwalletid()));
					tr.setType("Credit");
				}else {
					tr.setSender(walletRepository.getWnameFromWId(trans.getSenderwalletid()));
					tr.setType("Debit");
					if(trans.getReceiverwalletid().length()==14){
						tr.setReceiver(walletRepository.getMobileFromWId(trans.getReceiverwalletid()));
					}else {
						tr.setReceiver(trans.getReceiverwalletid());
					}	
				}
				tr.setTransactiontime((trans.getTransactiontime()));
				tr.setTransactionamount(trans.getTransactionamount());
				transmap.put(trans.getTransactionid(), tr);
			}
			System.out.println(transmap);
			JsonResponse json=new JsonResponse(transmap,HttpStatus.ACCEPTED);
			return new ResponseEntity<>(json,HttpStatus.OK);
		}
		else {
			StringResponse res=new StringResponse("No transactions",HttpStatus.BAD_REQUEST);
			return new ResponseEntity<StringResponse>(res,HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(value="/GetWalletTransactions",method=RequestMethod.GET)
	public ResponseEntity<?> GetWalletTransactions(@RequestParam String walletid ) throws Exception
	{
		GetWalletTransactionsRequest getWalletTransactions= new GetWalletTransactionsRequest();
		getWalletTransactions.setWalletid(walletid);
		StringResponse response=ValidatorService.validate(getWalletTransactions);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		ArrayList<String> ar=new ArrayList<String>();
		ar.add(walletid);
		ArrayList<Transactions> t=transactionsRepository.getUserTransactions(ar);
		HashMap<String,TransactionsResponse> transmap=new LinkedHashMap<>();
		if(!t.isEmpty()) {
			for(Transactions trans: t) {
				TransactionsResponse tr=new TransactionsResponse();	
				if(trans.getResponsecode().equals("200")) {
					tr.setStatus("Success");
				}else {
					tr.setStatus("Failed");
				}
				tr.setTransactionid(trans.getTransactionid());
				if(!trans.getSenderwalletid().equals(getWalletTransactions.getWalletid())) {
					if(trans.getSenderwalletid().length()==14){
						tr.setSender(walletRepository.getMobileFromWId(trans.getSenderwalletid()));
					}else {
						tr.setSender(trans.getSenderwalletid());
					}	
					tr.setReceiver(walletRepository.getWnameFromWId(trans.getReceiverwalletid()));
					tr.setType("Credit");
				}else {
					tr.setSender(walletRepository.getWnameFromWId(trans.getSenderwalletid()));
					tr.setType("Debit");
					if(trans.getReceiverwalletid().length()==14){
						tr.setReceiver(walletRepository.getMobileFromWId(trans.getReceiverwalletid()));
					}else {
						tr.setReceiver(trans.getReceiverwalletid());
					}	
				}
				tr.setTransactiontime(trans.getTransactiontime());
				tr.setTransactionamount(trans.getTransactionamount());
				transmap.put(trans.getTransactionid(), tr);
			}
			System.out.println(transmap);
			JsonResponse json=new JsonResponse(transmap,HttpStatus.ACCEPTED);
			return new ResponseEntity<>(json,HttpStatus.OK);
		}
		StringResponse res=new StringResponse("No transactions",HttpStatus.BAD_REQUEST);
		return new ResponseEntity<StringResponse>(res,HttpStatus.BAD_REQUEST);
	}

}
