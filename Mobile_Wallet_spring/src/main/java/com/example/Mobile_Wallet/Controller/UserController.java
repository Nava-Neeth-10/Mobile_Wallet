package com.example.Mobile_Wallet.Controller;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

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

import com.example.Mobile_Wallet.Database.Login;
import com.example.Mobile_Wallet.Database.Otp;
import com.example.Mobile_Wallet.Database.User;
import com.example.Mobile_Wallet.Database.UserDetails;
import com.example.Mobile_Wallet.Repository.LoginRepository;
import com.example.Mobile_Wallet.Repository.OtpRepository;
import com.example.Mobile_Wallet.Repository.UserActivityLogRepository;
import com.example.Mobile_Wallet.Repository.UserDetailsRepository;
import com.example.Mobile_Wallet.Repository.UserRepository;
import com.example.Mobile_Wallet.Repository.WalletRepository;
import com.example.Mobile_Wallet.Request.User.ChangePasswordRequest;
import com.example.Mobile_Wallet.Request.User.CreateUserRequest;
import com.example.Mobile_Wallet.Request.User.CreateUserWithMobileNoRequest;
import com.example.Mobile_Wallet.Request.User.ForgetPasswordRequest;
import com.example.Mobile_Wallet.Request.User.InviteUserRequest;
import com.example.Mobile_Wallet.Request.User.LoginUserRequest;
import com.example.Mobile_Wallet.Request.User.LoginUserWithMobileNoRequest;
import com.example.Mobile_Wallet.Request.User.ModifyUserDetailsRequest;
import com.example.Mobile_Wallet.Request.User.UserDetailsRequest;
import com.example.Mobile_Wallet.Request.User.ValidateOtpRequest;
import com.example.Mobile_Wallet.Response.JsonResponse;
import com.example.Mobile_Wallet.Response.StringResponse;
import com.example.Mobile_Wallet.Service.EmailService;
import com.example.Mobile_Wallet.Service.EncryptService;
import com.example.Mobile_Wallet.Service.ValidatorService;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
@RequestMapping("/User")
public class UserController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	LoginRepository loginRepository;
	
	@Autowired
	UserActivityLogRepository userActivityLogRepository;
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Autowired
	OtpRepository otpRepository;
	
	@Autowired
	WalletRepository walletRepository;
	
	JSONObject responseMessage,jo;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@EventListener(ApplicationReadyEvent.class)
	public void start() throws Exception
	{
		responseMessage = (JSONObject)new JSONParser().parse(new FileReader("src/main/resources/response.json"));
		jo=(JSONObject) responseMessage.get("UserController");
	}


	//This API allows user to login into the system with his/her credentials.
	@RequestMapping(value="/LoginUser",method=RequestMethod.GET)
	public ResponseEntity<StringResponse> LoginUser(@RequestParam String username,String password ) throws Exception
	{
		LoginUserRequest loginUserRequest=new LoginUserRequest();
		loginUserRequest.setPassword(password);
		loginUserRequest.setUsername(username);
		System.out.println(loginUserRequest.getUsername()+" "+loginUserRequest.getPassword());
		StringResponse response=ValidatorService.validate(loginUserRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		Login login=loginRepository.getByUsername(loginUserRequest.getUsername());
		User user=userRepository.getByUsername(loginUserRequest.getUsername());
		if(login==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else if(user.getUserstatus().equals("INACTIVE"))
			response=new StringResponse(jo.get("RES2").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			loginUserRequest.setPassword(EncryptService.encrypt(loginUserRequest.getPassword()));
			if(!login.getPassword().equals(loginUserRequest.getPassword()))
				response=new StringResponse(jo.get("RES3").toString(),HttpStatus.BAD_REQUEST);
			else
				response=new StringResponse(user.getMobileno(),HttpStatus.OK);
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","LoginUser");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",loginUserRequest.getUsername());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(login.getUserid(),"null",json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API allows user to register to the wallet system by providing the details of the user requested by the system.
	@RequestMapping(value="/CreateUser",method=RequestMethod.PUT)
	public ResponseEntity<StringResponse> CreateUser(@RequestBody CreateUserRequest createUserRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(createUserRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		if(!createUserRequest.getDob().equals(""))
		{
			try 
			{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				sdf.setLenient(false);
				sdf.parse(createUserRequest.getDob());
			}
			catch(Exception e)
			{
				response=new StringResponse(jo.get("RES4").toString(),HttpStatus.BAD_REQUEST);
				return new ResponseEntity<StringResponse>(response,response.getResponseCode());
			}
		}
		else
			createUserRequest.setDob(null);
		Login login=loginRepository.getByUsername(createUserRequest.getUsername());
		UserDetails userDetails=userDetailsRepository.getByEmail(createUserRequest.getEmail());
		User user=userRepository.getByMobileNo(createUserRequest.getMobileno());
		if(login!=null)
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else if(userDetails!=null)
			response=new StringResponse(jo.get("RES6").toString(),HttpStatus.BAD_REQUEST);
		else if(user!=null)
			response=new StringResponse(jo.get("RES7").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			Integer loginid=loginRepository.getMaxLoginId()+1;
			Integer userid=userRepository.getMaxUserId()+1;
			createUserRequest.setPassword(EncryptService.encrypt(createUserRequest.getPassword()));
			userRepository.insert(userid,"CUST",createUserRequest.getMobileno(),"ACTIVE");
			loginRepository.insert(loginid,userid,createUserRequest.getUsername(),createUserRequest.getPassword());
			userDetailsRepository.insert(userid,createUserRequest.getFirst_name(),createUserRequest.getMiddle_name(),createUserRequest.getLast_name(),createUserRequest.getEmail(),createUserRequest.getCountrycode(),createUserRequest.getDob());
			response=new StringResponse(jo.get("RES8").toString(),HttpStatus.OK);
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","CreateUser");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",createUserRequest.getUsername());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(userid,"null",json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	
	//This API is an alternative approach of registering a user into the system with the help of OTP sent to the user's mobile number and the OTP is validated before the user registers in the system.
	@RequestMapping(value="/CreateUserwithMobileNo",method=RequestMethod.PUT)
	public ResponseEntity<StringResponse> CreateUserwithMobileNo(@RequestBody CreateUserWithMobileNoRequest createUserWithMobileNoRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(createUserWithMobileNoRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(createUserWithMobileNoRequest.getMobileno());
		if(user!=null && user.getUserstatus().equals("ACTIVE"))
			response=new StringResponse(jo.get("RES9").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			if(user==null)
			{	
				Integer loginid=loginRepository.getMaxLoginId()+1;
				Integer userid=userRepository.getMaxUserId()+1;
				String password=EncryptService.encrypt(createUserWithMobileNoRequest.getMobileno());
				userRepository.insert(userid,"CUST",createUserWithMobileNoRequest.getMobileno(),"INACTIVE");
				loginRepository.insert(loginid,userid,createUserWithMobileNoRequest.getMobileno(),password);
				user=userRepository.getByMobileNo(createUserWithMobileNoRequest.getMobileno());
			}
			Integer otpid=otpRepository.getMaxOtpId()+1;
			int otp=(int)(Math.random()*9000+1000);
			JSONObject ot=(JSONObject)responseMessage.get("OTP");
			otpRepository.insert(otpid,otp,"REGISTER",null,ot.get("attempts").toString());
			userRepository.updateOtpId(createUserWithMobileNoRequest.getMobileno(),otpid);
			EmailService.send_mail("Mobile Wallet Registeration OTP","Your OTP is "+otp);
			response=new StringResponse(jo.get("RES10").toString(),HttpStatus.OK);
		}
		HashMap<String,String> map=new LinkedHashMap<>();
		map.put("DeviceId",createUserWithMobileNoRequest.getDeviceid());
		map.put("ActivityType","CreateUserwithMobileNo");
		map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
		map.put("Description",response.getResponse());
		map.put("CreatedBy",createUserWithMobileNoRequest.getMobileno());
		String json=objectMapper.writeValueAsString(map);
		userActivityLogRepository.insert(user.getUserid(),"null",json);
        return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API allows user to add their details after creating an account with mobile number
	@RequestMapping(value="/AddUserDetails",method=RequestMethod.PUT)
	public ResponseEntity<StringResponse> AddUserDetails(@RequestBody CreateUserRequest createUserRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(createUserRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(createUserRequest.getMobileno());
		if(user==null) {
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		}
		int userid=user.getUserid();
		UserDetails chck=userDetailsRepository.getByMobileNo(createUserRequest.getMobileno());
		if(chck!=null) {
			response=new StringResponse(jo.get("RES24").toString(),HttpStatus.BAD_REQUEST);
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		}
		
		if(!createUserRequest.getDob().equals(""))
		{
			try 
			{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				sdf.setLenient(false);
				sdf.parse(createUserRequest.getDob());
			}
			catch(Exception e)
			{
				response=new StringResponse(jo.get("RES4").toString(),HttpStatus.BAD_REQUEST);
				return new ResponseEntity<StringResponse>(response,response.getResponseCode());
			}
		}
		else
			createUserRequest.setDob(null);
		Login login=loginRepository.getByUsername(createUserRequest.getUsername());
		UserDetails userDetails=userDetailsRepository.getByEmail(createUserRequest.getEmail());
		if(login!=null)
			response=new StringResponse(jo.get("RES5").toString(),HttpStatus.BAD_REQUEST);
		else if(userDetails!=null)
			response=new StringResponse(jo.get("RES6").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			int loginid=loginRepository.getIdByUserId(userid);
			createUserRequest.setPassword(EncryptService.encrypt(createUserRequest.getPassword()));
			userRepository.add(userid,"CUST",createUserRequest.getMobileno(),"ACTIVE");
			loginRepository.add(loginid,userid,createUserRequest.getUsername(),createUserRequest.getPassword());
			userDetailsRepository.insert(userid,createUserRequest.getFirst_name(),createUserRequest.getMiddle_name(),createUserRequest.getLast_name(),createUserRequest.getEmail(),createUserRequest.getCountrycode(),createUserRequest.getDob());
			response=new StringResponse(jo.get("RES25").toString(),HttpStatus.OK);
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","AddUserDetails");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",createUserRequest.getUsername());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(userid,"null",json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API is an alternative way of logging into the system through the OTP sent to the user's mobile number and only if the OTP is valid the user is logged into the system. 
	@RequestMapping(value="/LoginUserwithMobileNo",method=RequestMethod.GET)
	public ResponseEntity<StringResponse> LoginUserwithMobileNo(@RequestParam String mobileno,String deviceid) throws Exception
	{
		LoginUserWithMobileNoRequest loginUserWithMobileNoRequest=new LoginUserWithMobileNoRequest();
		loginUserWithMobileNoRequest.setMobileno(mobileno);
		loginUserWithMobileNoRequest.setDeviceid(deviceid);
		StringResponse response=ValidatorService.validate(loginUserWithMobileNoRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(loginUserWithMobileNoRequest.getMobileno());
		if(user==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			if(user.getUserstatus().equals("INACTIVE"))
				response=new StringResponse(jo.get("RES2").toString(),HttpStatus.BAD_REQUEST);
			else
			{
				Integer otpid=otpRepository.getMaxOtpId()+1;
				int otp=(int)(Math.random()*9000+1000);
				JSONObject ot=(JSONObject)responseMessage.get("OTP");
				otpRepository.insert(otpid,otp,"LOGIN",null,ot.get("attempts").toString());
				userRepository.updateOtpId(loginUserWithMobileNoRequest.getMobileno(),otpid);
				EmailService.send_mail("Mobile Wallet Login OTP","Your OTP is "+otp);
				response=new StringResponse(jo.get("RES10").toString(),HttpStatus.OK);
			}
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("DeviceId",loginUserWithMobileNoRequest.getDeviceid());
			map.put("ActivityType","LoginUserwithMobileNo");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",loginUserWithMobileNoRequest.getMobileno());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(user.getUserid(),"null",json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API validates the OTP sent to the user at the time of registering,logging or changing the password in the system using user's mobile number
	@RequestMapping(value="/ValidateOTP",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> ValidateOTP(@RequestBody ValidateOtpRequest validateOtpRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(validateOtpRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(validateOtpRequest.getMobileno());
		if(user==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			Otp otp=otpRepository.getOtpByMobileNo(validateOtpRequest.getMobileno());
			if(otp==null)
				response=new StringResponse(jo.get("RES11").toString(),HttpStatus.BAD_REQUEST);
			else
			{
				boolean deleteotp=true;
				JSONObject ot=(JSONObject)responseMessage.get("OTP");
				if(otpRepository.checkExpiry(otp.getOtpid(),ot.get("expiry").toString())==null)
					response=new StringResponse(jo.get("RES12").toString(),HttpStatus.BAD_REQUEST);
				else if(otp.getAttempts()>0)
				{
					if(otp.getOtp()==Integer.parseInt(validateOtpRequest.getOtp()))
					{
						OTP_Action(validateOtpRequest.getMobileno(),otp);
						response=new StringResponse(jo.get("RES13").toString(),HttpStatus.OK);
					}
					else
					{
						otpRepository.updateAttempts(otp.getOtpid(),otp.getAttempts()-1);
						if(otp.getAttempts()==1)
							response=new StringResponse(jo.get("RES14").toString(),HttpStatus.BAD_REQUEST);
						else
						{
							deleteotp=false;
							response=new StringResponse(jo.get("RES15").toString(),HttpStatus.BAD_REQUEST);
						}
					}
				}
				if(deleteotp)
				{
					otpRepository.deleteByOtp(otp.getOtpid());
					userRepository.updateOtpId(validateOtpRequest.getMobileno(),null);
				}
			}
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","ValidateOTP");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",validateOtpRequest.getMobileno());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(user.getUserid(),"null",json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This method updates the OTP table to store the OTP sent  to the user
	public void OTP_Action(String mobileno,Otp otp)
	{
		if(otp.getReason().equals("REGISTER"))
			userRepository.updateStatus(mobileno,"ACTIVE");
		else if(otp.getReason().equals("PASSWORD"))
			loginRepository.updatePassword(loginRepository.getByMobileNo(mobileno).getUsername(),otp.getOtpkey());
		else if(otp.getReason().equals("WALLET"))
		{
			String otpkey[]=otp.getOtpkey().split(",");
			walletRepository.updateStatus(otpkey[0],otpkey[1]);
		}
	}
	
	//This API allows user to change the password of the wallet system incase the user forgets the password and after changing the password, the login table storing the credentials is also updated. 
	@RequestMapping(value="/ForgetPassword",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> ForgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(forgetPasswordRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		Login login=loginRepository.getByUsername(forgetPasswordRequest.getUsername());
		if(login==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			String password = RandomStringUtils.randomAlphanumeric(10);
			loginRepository.updatePassword(forgetPasswordRequest.getUsername(),EncryptService.encrypt(password));
			EmailService.send_mail("Mobile Wallet Password Reset","Your new Password is "+password);
			response=new StringResponse(jo.get("RES16").toString(),HttpStatus.OK);
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","ForgetPassword");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",forgetPasswordRequest.getUsername());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(login.getUserid(),"null",json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API allows the user to change the password of the wallet accompanied with process of OTP verification to improve security. 
	@RequestMapping(value="/ChangePassword",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> ChangePassword(@RequestBody ChangePasswordRequest changePasswordRequest)throws Exception
	{
		StringResponse response=ValidatorService.validate(changePasswordRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		Login login=loginRepository.getByUsername(changePasswordRequest.getUsername());
		if(login==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			changePasswordRequest.setCurrentpassword(EncryptService.encrypt(changePasswordRequest.getCurrentpassword()));
			changePasswordRequest.setNewpassword(EncryptService.encrypt(changePasswordRequest.getNewpassword()));
			if(!login.getPassword().equals(changePasswordRequest.getCurrentpassword()))
				response=new StringResponse(jo.get("RES17").toString(),HttpStatus.BAD_REQUEST);
			else if(changePasswordRequest.getCurrentpassword().equals(changePasswordRequest.getNewpassword()))
				response=new StringResponse(jo.get("RES18").toString(),HttpStatus.BAD_REQUEST);
			else
			{
				Integer otpid=otpRepository.getMaxOtpId()+1;
				int otp=(int)(Math.random()*9000+1000);
				JSONObject ot=(JSONObject)responseMessage.get("OTP");
				otpRepository.insert(otpid,otp,"PASSWORD",changePasswordRequest.getNewpassword(),ot.get("attempts").toString());
				userRepository.updateOtpId(userRepository.getByUsername(changePasswordRequest.getUsername()).getMobileno(),otpid);
				EmailService.send_mail("Mobile Wallet Change Password OTP","Your OTP is "+otp);
				response=new StringResponse(jo.get("RES10").toString(),HttpStatus.OK);
			}
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","ChangePassword");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",changePasswordRequest.getUsername());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(login.getUserid(),"null",json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	//This API is used to view the account details of the user logged into the system. 
	@RequestMapping(value="/UserDetails",method=RequestMethod.GET)
	public ResponseEntity<?> UserDetails(@RequestParam String mobileno ) throws Exception
	{
		UserDetailsRequest userDetailsRequest=new UserDetailsRequest();
		userDetailsRequest.setMobileno(mobileno); 
		StringResponse response=ValidatorService.validate(userDetailsRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		User user=userRepository.getByMobileNo(userDetailsRequest.getMobileno());
		UserDetails userDetails=userDetailsRepository.getByMobileNo(userDetailsRequest.getMobileno());
		if(user==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else if(userDetails==null)
			response=new StringResponse(jo.get("RES19").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			JsonResponse jsonResponse=new JsonResponse(userDetails,HttpStatus.OK);
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","UserDetails");
			map.put("ResponseCode",Integer.toString(jsonResponse.getResponseCode().value()));
			map.put("Description",jsonResponse.getResponse().toString());
			map.put("CreatedBy",userDetailsRequest.getMobileno());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(user.getUserid(),"null",json);
			return new ResponseEntity<JsonResponse>(jsonResponse,jsonResponse.getResponseCode());
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}

	//This API is used by the user if the user wishes to update the details in the wallet system and the changes are updated in the User table in Database 
	@RequestMapping(value="/ModifyUserDetails",method=RequestMethod.POST)
	public ResponseEntity<StringResponse> ModifyUserDetails(@RequestBody ModifyUserDetailsRequest modifyUserDetailsRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(modifyUserDetailsRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		UserDetails userdetails=null;
		boolean modify=true;
		if(!modifyUserDetailsRequest.getDob().equals(""))
		{
			try
			{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				sdf.setLenient(false);
				sdf.parse(modifyUserDetailsRequest.getDob());
			}
			catch(Exception e)
			{
				response=new StringResponse(jo.get("RES4").toString(),HttpStatus.BAD_REQUEST);
				return new ResponseEntity<StringResponse>(response,response.getResponseCode());
			}
		}
		else
			modifyUserDetailsRequest.setDob(null);
		if(modifyUserDetailsRequest.getKeyname().equals("MobileNo"))
		{
			userdetails=userDetailsRepository.getByMobileNo(modifyUserDetailsRequest.getSearchkey());
			if(userdetails==null)
			{
				modify=false;
				response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
			}
		}
		else if(modifyUserDetailsRequest.getKeyname().equals("Email"))
		{
			userdetails=userDetailsRepository.getByEmail(modifyUserDetailsRequest.getSearchkey());
			if(userdetails==null)
			{
				modify=false;
				response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
			}
		}
		if(modifyUserDetailsRequest.getKeyname().equals("Username"))
		{
			userdetails=userDetailsRepository.getByUsername(modifyUserDetailsRequest.getSearchkey());
			if(userdetails==null)
			{
				modify=false;
				response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
			}
		}
		if(modify)
		{
			response=new StringResponse(jo.get("RES20").toString(),HttpStatus.OK);
			userDetailsRepository.update(userdetails.getUserid(),modifyUserDetailsRequest.getFirst_name(),modifyUserDetailsRequest.getMiddle_name(),modifyUserDetailsRequest.getLast_name(),modifyUserDetailsRequest.getDob());
			HashMap<String,String> map=new LinkedHashMap<>();
			map.put("ActivityType","UserDetails");
			map.put("ResponseCode",Integer.toString(response.getResponseCode().value()));
			map.put("Description",response.getResponse());
			map.put("CreatedBy",modifyUserDetailsRequest.getFirst_name());
			String json=objectMapper.writeValueAsString(map);
			userActivityLogRepository.insert(userdetails.getUserid(),"null",json);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
	@RequestMapping(value="/InviteUser",method=RequestMethod.GET)
	public ResponseEntity<StringResponse> InviteUser(@RequestBody InviteUserRequest inviteUserRequest) throws Exception
	{
		StringResponse response=ValidatorService.validate(inviteUserRequest);
		if(response!=null)
			return new ResponseEntity<StringResponse>(response,response.getResponseCode());
		if(userRepository.getByMobileNo(inviteUserRequest.getMobileno())==null)
			response=new StringResponse(jo.get("RES1").toString(),HttpStatus.BAD_REQUEST);
		else
		{
			EmailService.send_mail("Mobile Wallet Invite",inviteUserRequest.getMobileno()+" wants to send you money. Install Mobile Wallet App to receive the amount.");
			response=new StringResponse(jo.get("RES21").toString(),HttpStatus.OK);
		}
		return new ResponseEntity<StringResponse>(response,response.getResponseCode());
	}
	
}