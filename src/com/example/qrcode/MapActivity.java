package com.example.qrcode;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {
	private GoogleMap googleMap;
	GPSTracker gps;
	double latitude, longitude;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	TextToSpeech textToSpeech;
Button go;
EditText entermap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		go=(Button)findViewById(R.id.go);
		entermap=(EditText)findViewById(R.id.entermap);
		
		go.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MapActivity.this,BluetoothActivity.class);
				startActivity(intent);
			}
		});
		entermap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				promptSpeechInput();
			}
		});
		
		try {
			// Loading map
			initilizeMap();

			// Changing map type
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			// Showing / hiding your current location
			googleMap.setMyLocationEnabled(true);

			// Enable / Disable zooming controls
			googleMap.getUiSettings().setZoomControlsEnabled(false);

			// Enable / Disable my location button
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);

			// Enable / Disable Compass icon
			googleMap.getUiSettings().setCompassEnabled(true);

			// Enable / Disable Rotate gesture
			googleMap.getUiSettings().setRotateGesturesEnabled(true);

			// Enable / Disable zooming functionality
			googleMap.getUiSettings().setZoomGesturesEnabled(true);
			gps = new GPSTracker(getApplicationContext());
			if (gps.canGetLocation()) {
				Log.d("Your Location", "latitude:" + gps.getLatitude()
						+ ", longitude: " + gps.getLongitude());
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();

			}

			// Adding a marker
			MarkerOptions marker = new MarkerOptions().position(
					new LatLng(latitude, longitude)).title("Current Location ");

			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
			//
			googleMap.addMarker(marker);

			// Move the camera to last position with a zoom level

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(latitude, longitude)).zoom(15).build();

			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		initilizeMap();
	}

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}
	

private void promptSpeechInput() {
	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
			RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
	intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
			getString(R.string.speech_prompt));
	try {
		startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
	} catch (ActivityNotFoundException a) {
		Toast.makeText(getApplicationContext(),
				getString(R.string.speech_not_supported),
				Toast.LENGTH_SHORT).show();
	}
}

/**
 * Receiving speech input
 * */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	switch (requestCode) {
	case REQ_CODE_SPEECH_INPUT: {
		if (resultCode == RESULT_OK && null != data) {

			ArrayList<String> result = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			entermap.setText(result.get(0));
			
		}
		break;
	}

	}
}
}
