package com.example.sendsmsexample;

import com.swisscom.api.sms.wrapper.SendSMS;

public class SmsDemo {

	public void sendSMS(String clientId, String from, String to, String text) {
		
		SendSMS.sendSms(clientId, from, to, text);
		
	}

}





























