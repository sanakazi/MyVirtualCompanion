package statnor.callndata.com;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.GPSTracker;
import com.callndata.Others.ServiceHandler;
import com.callndata.Others.ServiceTest;
import statnor.callndata.com.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TrackConfirmActivity extends Activity {

	String Num, Name;
	TextView txtName;
	Button btnConfirm;
	SharedPreferences pref;
	ProgressDialog pDialog;
	Double latitude, longitude;
	Double FromLat, FromLong, ToLat, ToLong;
	String Bodyguard_Id = "null", Current_Location, temp;
	String Tracker_Name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_confirm);

		pref = getApplicationContext().getSharedPreferences("MyPref", 0);

		txtName = (TextView) findViewById(R.id.txtName);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);

		if (Constant.BodyguardOpt.equals("0")) {
			Num = getIntent().getExtras().getString("Friend_Num");
			Name = getIntent().getExtras().getString("Friend_Name");
			txtName.setText(Name);
			Bodyguard_Id = Num;
		} else {
			Num = "0";
			Name = "Bodyguard Plus";
			txtName.setText("Bodyguard Plus");
			Bodyguard_Id = "0";
		}

		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				new TrackConfirmJSON().execute();

			}
		});

		FromLat = GetLatitude(Constant.TrackFrom);
		FromLong = GetLongitude(Constant.TrackFrom);

		ToLat = GetLatitude(Constant.TrackTo);
		ToLong = GetLongitude(Constant.TrackTo);

		try {
			GPSTracker gps = new GPSTracker(TrackConfirmActivity.this);
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

		Current_Location = address1 + ", " + city + ", " + state + ", " + country;

	}

	class TrackConfirmJSON extends AsyncTask<Void, Void, Void> {

		String status = "null";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(TrackConfirmActivity.this);
			pDialog.setMessage("Please Wait");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler servicehandler = new ServiceHandler();

			try {
				String URL = Constant.TrackConfirmJSON + Constant.UserId + "&BodyGaurdID=" + Bodyguard_Id
						+ "&fromLocationName=" + Constant.TrackFrom + "&fromlongitude=" + FromLong + "&fromlatitude="
						+ FromLat + "&toLocationName=" + Constant.TrackTo + "&tolongitude=" + ToLong + "&tolatitude="
						+ ToLat + "&currentLocationName=" + Current_Location + "&currentlongitude=" + latitude
						+ "&currentlatitude=" + longitude;

				URL = URL.replace(" ", "%20");
				String Data = servicehandler.makeServiceCall(URL, ServiceHandler.GET);

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

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (pDialog.isShowing()) {
				pDialog.dismiss();
			}

			if (status.equals("1")) {
				Toast.makeText(TrackConfirmActivity.this, "Confirmed ! Track Id is " + temp, Toast.LENGTH_SHORT).show();

				Editor editor = pref.edit();
				editor.putString("Track_Id", temp);
				editor.putString("Tracker_Name", Name);
				editor.commit();

				//start service to update location after every two minute of interval
				startService(new Intent(TrackConfirmActivity.this, ServiceTest.class));
				finish();
				
			} else if (status.equals("0")) {
				Toast.makeText(TrackConfirmActivity.this, "Server Error !!!", Toast.LENGTH_SHORT).show();
			} else if (status.equals("null")) {
				Toast.makeText(TrackConfirmActivity.this, "Oops! Try again later...", Toast.LENGTH_SHORT).show();
			}
		}

	}

	public Double GetLatitude(String ReceviedAddress) {
		Geocoder coder = new Geocoder(this);
		List<Address> address;
		Double lat = 0.0;
		try {
			address = coder.getFromLocationName(ReceviedAddress, 5);
			if (address == null) {
				return null;
			}
			Address location = address.get(0);
			lat = location.getLatitude();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lat;
	}

	public Double GetLongitude(String ReceviedAddress) {
		Geocoder coder = new Geocoder(this);
		List<Address> address;
		Double longi = 0.0;
		try {
			address = coder.getFromLocationName(ReceviedAddress, 5);
			if (address == null) {
				return null;
			}
			Address location = address.get(0);
			longi = location.getLongitude();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return longi;
	}
}
