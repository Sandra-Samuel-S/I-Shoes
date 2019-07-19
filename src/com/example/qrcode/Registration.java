package com.example.qrcode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Registration extends Activity {

	EditText phoneno, email, name;
	Button register;
	TextView loginhere;
	String ph, mail, custname;
	HttpClient httpClient;
	HttpPost httpPost;
	HttpResponse httpResponse;
	private ProgressDialog pd;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);

		phoneno = (EditText) findViewById(R.id.reg_fullname);
		email = (EditText) findViewById(R.id.reg_email);
		name = (EditText) findViewById(R.id.reg_password);
		loginhere = (TextView) findViewById(R.id.link_to_login);

	}

	public void Register(View v) {

		ph = phoneno.getText().toString();
		mail = email.getText().toString();
		custname = name.getText().toString();

		pd = ProgressDialog.show(Registration.this, "",
				"Registering you in...", false, false);

		if (!isValidPhone(ph)) {
			phoneno.setError("This not valid phone number");
			pd.dismiss();
		} else if (!isValidEmail(mail)) {
			email.setError("This not Valid EMail Id");
			pd.dismiss();
		} else if (!isValidName(custname)) {
			name.setError("Name should be minimum 6 letters");
			pd.dismiss();
		} else {	
			SharedPreferences sharedPreferences=getSharedPreferences("login", MODE_PRIVATE);
			SharedPreferences.Editor editor=sharedPreferences.edit();
			editor.putString("d1",ph);
			editor.putString("d2",mail);
			editor.putString("d3",custname);
			editor.commit();
			Toast.makeText(Registration.this,"Registration Success", Toast.LENGTH_LONG).show();
			Intent intent=new Intent(Registration.this,Login.class);
			startActivity(intent);
			
		}

	}

	private boolean isValidEmail(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	private boolean isValidName(String name) {
		if (name != null && name.length() >= 6) {
			return true;
		}
		return false;
	}

	private boolean isValidPhone(String phone) {
		if (phone != null && phone.length() >= 10) {
			return true;
		}
		return false;
	}

}
