package com.callndata.Others;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ServiceTest extends Service {

	Date date1, date2;
	String title, desc;
	SimpleDateFormat sdf;
	Double latitude = 0.0, longitude = 0.0;
	int day, month, year, hour, minute;
	int Cday, Cmonth, Cyear, Chour, Cminute;
	String postfix, Cpostfix, date, time, Cdate, Ctime;
	ArrayList<String> ReminderRecord = new ArrayList<String>();

	String Track_Id;
	SharedPreferences pref;
	ReceiverCall receivercall;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("Service", "Started");
		mTimer = new Timer();
		mTimer.schedule(timerTask, 30000, 30000);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
		return START_STICKY;
	}

	private Timer mTimer;

	TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			// Log.e("Log", "Running");
			updateUI.sendEmptyMessage(0);
		}
	};

	private Handler updateUI = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			// Toast.makeText(getApplicationContext(), "MVC servicetoast",
			// Toast.LENGTH_SHORT).show();

			try {
				latitude = 0.0;
				longitude = 0.0;
				
				GPSTracker gps = new GPSTracker(getApplicationContext());

				for (int i = 0; i < 10; i++) {
					if (latitude == 0.0 || longitude == 0.0) {
						if (gps.canGetLocation()) {
							latitude = gps.getLatitude();
							longitude = gps.getLongitude();
						} else {
							gps.showSettingsAlert();
						}
					} else {
						break;
					}
				}
				if (latitude == 0.0 || longitude == 0.0) {
					Toast.makeText(getApplicationContext(), "GPS error...", Toast.LENGTH_SHORT).show();
				} else {
					new TrackJSON().execute();
				}

			} catch (Exception e) {
				latitude = 0.0;
				longitude = 0.0;
			}
		}
	};

	public void onDestroy() {
		try {
			mTimer.cancel();
			timerTask.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Intent intent = new Intent("com.android.inventory");
		// intent.putExtra("yourvalue", "torestore");
		// sendBroadcast(intent);

		receivercall = new ReceiverCall();
		try {
			unregisterReceiver(receivercall);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class TrackJSON extends AsyncTask<Void, Void, Void> {

		String status, temp;

		@Override
		protected Void doInBackground(Void... params) {
			ServiceHandler serviceHandler = new ServiceHandler();

			Geocoder geocoder;
			List<Address> addresses = null;
			geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			if( addresses != null ) {
				String address1 = addresses.get(0).getAddressLine(1);
				String city = addresses.get(0).getLocality();
				String state = addresses.get(0).getAdminArea();
				String country = addresses.get(0).getCountryName();
				String CurrentAddress = address1 + ", " + city + ", " + state + ", " + country;
	
				pref = getApplicationContext().getSharedPreferences("MyPref", 0);
				Track_Id = pref.getString("Track_Id", null);
	
				try {
					String URL = Constant.CaptureLocationJSON + Constant.UserId + "&trackID=" + Track_Id
							+ "&currentLocationName=" + CurrentAddress + "&currentlongitude=" + longitude
							+ "&currentlatitude=" + latitude;
					URL = URL.replace(" ", "%20");
					String Data = serviceHandler.makeServiceCall(URL, ServiceHandler.GET);
	
					JSONArray arr = new JSONArray(Data);
					JSONObject obj = new JSONObject();
	
					for (int i = 0; i < arr.length(); i++) {
						if (i == 0) {
							obj = arr.getJSONObject(i);
							status = obj.getString("status");
						} else {
							if (status.equals("1")) {
								obj = arr.getJSONObject(i);
								temp = obj.getString("num");
							}
						}
					}
	
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (status.equals("1")) {
				Toast.makeText(getApplicationContext(), "Location Updated", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
