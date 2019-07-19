package com.example.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
Intent intent;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ImageView scanBtn = (ImageView) findViewById(R.id.btnScan);

		scanBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				try {

					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
					startActivityForResult(intent, 0);
					

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "ERROR:" + e, 1)
							.show();

				}

			}
		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {

			if (resultCode == RESULT_OK) {
				//SharedPreferences preferences=getSharedPreferences("detais", MODE_PRIVATE);
				//SharedPreferences.Editor editor=preferences.edit();
				//editor.putString("data", intent.getStringExtra("SCAN_RESULT"));
				Toast.makeText(getBaseContext(),
						intent.getStringExtra("SCAN_RESULT"), Toast.LENGTH_LONG)
						.show();
				String scanres=intent.getStringExtra("SCAN_RESULT");
				Bundle b=new Bundle();
				b.putString("scan", scanres);
				Intent intent1=new Intent(MainActivity.this,BluetoothActivity.class);	
				intent1.putExtras(b);
				startActivity(intent1);


			}

		} else if (resultCode == RESULT_CANCELED) {

			Toast.makeText(this, "Scan cancelled...", Toast.LENGTH_LONG).show();
		}

	}

	
}
