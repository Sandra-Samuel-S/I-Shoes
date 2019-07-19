package com.example.qrcode;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

	EditText email, phoneno;
	Button login;
	TextView register;
	String mail, pswd;
	private ProgressDialog pd;
	private Thread thread;
	HttpClient httpClient;
	HttpPost httpPost;
	HttpResponse httpResponse;
	private InputStream stream = null;
	private StringBuilder stringBuilder;
	private String error_response;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login1);

		email = (EditText) findViewById(R.id.email);
		phoneno = (EditText) findViewById(R.id.password);
		login = (Button) findViewById(R.id.btnLogin);
		register = (TextView) findViewById(R.id.link_to_register);
		
	login.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			SharedPreferences sharedPreferences=getSharedPreferences("login", MODE_PRIVATE);
			String name=sharedPreferences.getString("d1", "");
			String pass=sharedPreferences.getString("d3", "");
			if (email.getText().toString().isEmpty() && phoneno.getText().toString().isEmpty()) {
				Toast.makeText(Login.this,"please enter all fields",Toast.LENGTH_LONG).show();
			}
			else if (email.getText().toString().isEmpty()) {
				email.setError("enter phone no");
			}
			else if (phoneno.getText().toString().isEmpty()) {
				phoneno.setError("enter valid password");
			}
			else if (email.getText().toString().equals(name) && phoneno.getText().toString().equals(pass)) {
				Toast.makeText(Login.this,"Login success!",Toast.LENGTH_LONG).show();
				Intent i = new Intent(Login.this, MapActivity.class);
				startActivity(i);
			}
			else{
				Toast.makeText(Login.this,"Invallid password or phone number!",Toast.LENGTH_LONG).show();
			}
			
		}
	});

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Login.this, Registration.class);
				startActivity(i);
			}
		});
	}

}