package statnor.callndata.com;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.callndata.Others.GPSTracker;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.CountDownTimer;
import android.os.IBinder;

public class UserLocationUpdate extends Service {
	
	private Double latitude, longitude;
	private String currentAddress;
	CounterClass timer;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//update user location after every 2 minutes
		timer = new CounterClass(120000,1000);  
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		
		getLocation();
		
	}
	
	private void getLocation() {
		// TODO Auto-generated method stub
		
		try {
			GPSTracker gps = new GPSTracker(getApplicationContext());
			if (gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
			} else {
				gps.showSettingsAlert();
			}
		} catch (Exception e) {
			latitude = 0.0;
			longitude = 0.0;
		}
		
		Geocoder geocoder;
		List<Address> addresses = null;
		geocoder = new Geocoder(this, Locale.getDefault());

		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String address1 = addresses.get(0).getAddressLine(1);
		String city = addresses.get(0).getLocality();
		String state = addresses.get(0).getAdminArea();
		String country = addresses.get(0).getCountryName();
		
		currentAddress = address1 + ", " + city + ", " + state + ", " + country;
		
	}

	@Override
	public void onDestroy() {
		
	}
	
	 public class CounterClass extends CountDownTimer {

		public CounterClass(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			
		}  
		 
	 }

}
