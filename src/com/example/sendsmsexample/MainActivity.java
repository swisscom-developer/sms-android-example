package com.example.sendsmsexample;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button buttonSend = null;
	EditText editPhoneNumber = null, editMessage = null;
	TextView textNumber = null, textPhoneNumber = null, textMessage = null,
			textPhoneNumberUp = null;
	ImageView imgSwisscomLogo = null;
	String phoneNumber = "", message = "";
	Editor editor = null;
	LinearLayout layout = null;
	MarginLayoutParams params = null;
	int pixelButton = 0;

	HttpResponse serverResponse = null;
	HttpClient client = new DefaultHttpClient();
	Thread background = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		editPhoneNumber = (EditText) findViewById(R.id.edit_phone);
		editMessage = (EditText) findViewById(R.id.edit_message);
		textPhoneNumber = (TextView) findViewById(R.id.txt_phone);
		textPhoneNumberUp = (TextView) findViewById(R.id.txt_phonenumber);
		imgSwisscomLogo = (ImageView) findViewById(R.id.brand);
		editPhoneNumber.setText("+41");
		editPhoneNumber.setSelection(3);

		iniButton();

		params = (MarginLayoutParams) buttonSend.getLayoutParams();
		pixelButton = params.topMargin;

		editMessage.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					textPhoneNumberUp.setText(editPhoneNumber.getText()
							.toString());
					textPhoneNumberUp.setVisibility(View.VISIBLE);
					shiftLayoutUp();

					layout = (LinearLayout) findViewById(R.id.layout);
					layout.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View view, MotionEvent ev) {
							hideKeyboard(view);
							shiftLayoutDown();
							return false;
						}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		/**
		 *  if thread is still running -> stop it
		 */ 
		try {
			if (background != null) {
				background.join(10); 
				if (background.isAlive()) {
					Log.i("SendSMS", "Thread is still alive!");
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e("SendSMS", "ERROR:" + e.toString());
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (textPhoneNumberUp.getVisibility() == View.VISIBLE) {
				shiftLayoutDown();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean checkNumber(String number) {
		if (number.startsWith("+417") && number.length() > 11) {
			return true;
		}
		return false;
	}

	private void shiftLayoutUp() {
		editPhoneNumber.setVisibility(View.GONE);
		imgSwisscomLogo.setVisibility(View.GONE);
		textPhoneNumber.setVisibility(View.GONE);
		params.topMargin = 60;
		buttonSend.setLayoutParams(params);
	}

	private void shiftLayoutDown() {
		textPhoneNumberUp.setVisibility(View.GONE);
		editPhoneNumber.setVisibility(View.VISIBLE);
		imgSwisscomLogo.setVisibility(View.VISIBLE);
		textPhoneNumber.setVisibility(View.VISIBLE);
		editMessage.clearFocus();
		editPhoneNumber.setSelection(editPhoneNumber.length());
		params.topMargin = pixelButton;
		buttonSend.setLayoutParams(params);
	}

	private void iniButton() {
		buttonSend = (Button) findViewById(R.id.button_send);
		buttonSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if (editPhoneNumber != null) {
					phoneNumber = editPhoneNumber.getText().toString();
				} else {
					Toast.makeText(getBaseContext(),
							"Please enter a valid phone number",
							Toast.LENGTH_LONG).show();
				}
				message = editMessage.getText().toString();

				if (checkNumber(phoneNumber)) {
					background = new Thread(new Runnable() {
					public void run() {

						SmsDemo demo = new SmsDemo();
						demo.sendSMS("<YOUR_CLIENT_ID_GOES_HERE>","<YOUR_VERIFIED_PHONE_NUMBER_GOES_HERE>", phoneNumber, message);
						
					}});
					background.start();

					editMessage.setText("");
					editPhoneNumber.setText("+41");
					editPhoneNumber.requestFocus();
					editPhoneNumber.setSelection(3);
					hideKeyboard(view);
					Toast.makeText(getBaseContext(), "SMS send",
							Toast.LENGTH_LONG).show();
					shiftLayoutDown();
				} else {
					Toast.makeText(getBaseContext(),
							"Please enter a valid phone number",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void hideKeyboard(View view) {
		InputMethodManager in = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
}
