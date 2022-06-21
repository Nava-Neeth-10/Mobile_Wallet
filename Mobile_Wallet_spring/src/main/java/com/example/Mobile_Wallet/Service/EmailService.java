package com.example.Mobile_Wallet.Service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {
	
	public static void send_mail(String subject,String body) throws Exception
	{
		Thread t = new Thread(new Runnable(){
		    @Override
		    public void run() {
		    	try {
		    		Properties props = System.getProperties();
		    		props.put("mail.smtp.starttls.enable", "true");
		            props.put("mail.smtp.auth", "true");
		            props.put("mail.smtp.host", "smtp.gmail.com");
		            props.put("mail.smtp.port", "587");
		            Session session = Session.getInstance(props,new javax.mail.Authenticator() {
		            	protected PasswordAuthentication getPasswordAuthentication() {
		            		return new PasswordAuthentication("noreplyreport455@gmail.com","Bat@123@Man");
		            	}
		            });
		            Message message = new MimeMessage(session);
		            message.setFrom(new InternetAddress("noreplyreport455@gmail.com"));
		            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("mathurrohan0511@yahoo.in"));
		            message.setSubject(subject);
		            message.setText(body);
		            Transport.send(message);
		    	}
		    	catch(Exception e) {
		    		System.out.println(e);
		    	}
		    }
		});
		t.start();
	}
	
}
