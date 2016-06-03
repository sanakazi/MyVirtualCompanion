package statnor.callndata.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import statnor.callndata.com.LoginActivity.LoginJSON;

import com.callndata.Others.Constant;
import com.callndata.Others.GPSTracker;
import com.callndata.Others.JSONParser;
import com.callndata.Others.MVCLog;
import com.callndata.Others.PlaceDetailsJSONParser;
import com.callndata.Others.PlaceJSONParser;
//import com.google.android.gms.internal.en;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import statnor.callndata.com.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class LocationActivity extends FragmentActivity {

	GoogleMap googleMap;
	Double latitude, longitude, latitudeD, longitudeD;
	DownloadTask placesDownloadTask;
	DownloadTask1 placesDownloadTask1;
	AutoCompleteTextView actxtSPoint, actxtSearch;
	ParserTask placesParserTask;
	ParserTask1 placesParserTask1;

	final int PLACES = 0;
	final int PLACES_DETAILS = 1;

	ParserTask placeDetailsParserTask;
	ParserTask placeDetailsParserTask1;
	DownloadTask placeDetailsDownloadTask;
	DownloadTask1 placeDetailsDownloadTask1;

	Button btnDrawPath, btnNext;

	public static final String TAG = "LocationActivity"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		btnDrawPath = (Button) findViewById(R.id.btnDrawPath);
		btnNext = (Button) findViewById(R.id.btnNext);

		actxtSearch = (AutoCompleteTextView) findViewById(R.id.actxtSearch);
		actxtSearch.setThreshold(1);
		actxtSearch.setDropDownBackgroundResource(R.color.battleship_grey);

		actxtSPoint = (AutoCompleteTextView) findViewById(R.id.actxtSPoint);
		actxtSPoint.setThreshold(1);
		actxtSPoint.setDropDownBackgroundResource(R.color.battleship_grey);

		actxtSearch.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				return false;
			}
		});

		actxtSPoint.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				return false;
			}
		});

		actxtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Creating a DownloadTask to download Google Places matching
				// "s"

				if (placesDownloadTask != null) {
					if (placesDownloadTask.getStatus() == AsyncTask.Status.PENDING
							|| placesDownloadTask.getStatus() == AsyncTask.Status.RUNNING
							|| placesDownloadTask.getStatus() == AsyncTask.Status.FINISHED) {
						Log.i("--placesDownloadTask--", "progress_status : " + placesDownloadTask.getStatus());
						placesDownloadTask.cancel(true);
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String chterm;
				chterm = actxtSearch.getText().toString();
				placesDownloadTask = new DownloadTask(PLACES);

				// Getting url to the Google Places Autocomplete api
				String url = getAutoCompleteUrl(s.toString());

				// Start downloading Google Places
				// This causes to execute doInBackground() of DownloadTask class
				placesDownloadTask.execute(url);
			}
		});

		actxtSPoint.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Creating a DownloadTask to download Google Places matching
				// "s"

				if (placesDownloadTask1 != null) {
					if (placesDownloadTask1.getStatus() == AsyncTask.Status.PENDING
							|| placesDownloadTask1.getStatus() == AsyncTask.Status.RUNNING
							|| placesDownloadTask1.getStatus() == AsyncTask.Status.FINISHED) {
						Log.i("--placesDownloadTask--", "progress_status : " + placesDownloadTask1.getStatus());
						placesDownloadTask1.cancel(true);
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String chterm;
				chterm = actxtSPoint.getText().toString();
				placesDownloadTask1 = new DownloadTask1(PLACES);

				// Getting url to the Google Places Autocomplete api
				String url = getAutoCompleteUrl(chterm.toString());

				// Start downloading Google Places
				// This causes to execute doInBackground() of DownloadTask class
				placesDownloadTask1.execute(url);
			}
		});

		actxtSearch.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {

				ListView lv = (ListView) arg0;
				SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

				HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

				Constant.checkInPlace = hm.get("description");
				MVCLog.doLog(TAG, "actxtSearch onItemClick");
				placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);
				String url = getPlaceDetailsUrl(hm.get("reference"));
				placeDetailsDownloadTask.execute(url);
			}
		});

		actxtSPoint.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id) {

				ListView lv = (ListView) arg0;
				SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

				HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

				Constant.checkInPlace1 = hm.get("description");
				placeDetailsDownloadTask1 = new DownloadTask1(PLACES_DETAILS);
				String url = getPlaceDetailsUrl(hm.get("reference"));
				placeDetailsDownloadTask1.execute(url);
			}
		});

		try {
			GPSTracker gps = new GPSTracker(LocationActivity.this);
			if (gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
			} else {
				gps.showSettingsAlert();
			}
		} catch (Exception e) {
			latitude = 0.0;
			longitude = 0.0;			
			backToHome();
		}
		
		/*if( latitude == 0.0 ){
			Intent backIntent = new Intent(LocationActivity.this, HomeActivity.class);
			startActivity(backIntent);
			finish();
		}*/
		
		Geocoder geocoder;
		List<Address> addresses = null;
		geocoder = new Geocoder(this, Locale.getDefault());

		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Here 1 represent max location result to returned, by documents it
			// recommended 1 to 5
		
		if( addresses != null) {
			
			if (latitude != 0.0 && longitude != 0.0) {
				String locality = addresses.get(0).getLocality();
				String address = addresses.get(0).getAddressLine(0);
				String address1 = addresses.get(0).getAddressLine(1);
				String address2 = addresses.get(0).getAddressLine(2);
				String city = addresses.get(0).getLocality();
				String state = addresses.get(0).getAdminArea();
				String country = addresses.get(0).getCountryName();
				String postalCode = addresses.get(0).getPostalCode();
				String knownName = addresses.get(0).getFeatureName();
	
				actxtSPoint.setText(address1 + ", " + city + ", " + state + ", " + country);
			} else {
				Toast.makeText(LocationActivity.this, "GPS Error...", Toast.LENGTH_SHORT).show();
			}
			
		}else {
			backToHome();	
		}

		try {
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

		btnDrawPath.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String start, end;

				start = actxtSPoint.getText().toString();
				end = actxtSearch.getText().toString();

				if (start.equals("") || start.equals(null) || start.isEmpty()) {
					Toast.makeText(LocationActivity.this, "Enter Start Point", Toast.LENGTH_SHORT).show();
				} else if (end.equals("") || end.equals(null) || end.isEmpty()) {
					Toast.makeText(LocationActivity.this, "Enter End Point", Toast.LENGTH_SHORT).show();
				} else {

					boolean isInternet = haveNetworkConnection();
					if (isInternet) {
						MVCLog.doLog(TAG, "latS- "+latitude+" lanS- "+longitude+" latD- "+latitudeD+" lanD- "+longitudeD);
						if( latitudeD != null ) { 
							new connectAsyncTask(makeURL(latitude, longitude, latitudeD, longitudeD)).execute();
						}else {
							Toast.makeText(getApplicationContext(), "Something went wrong, please try again", 
									Toast.LENGTH_LONG).show();			
							actxtSearch.setText("");
						}
					}
				}
			}
		});

		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String start, end;

				start = actxtSPoint.getText().toString();
				end = actxtSearch.getText().toString();

				if (start.equals("") || start.equals(null) || start.isEmpty()) {
					Toast.makeText(LocationActivity.this, "Enter Start Point", Toast.LENGTH_SHORT).show();
				} else if (end.equals("") || end.equals(null) || end.isEmpty()) {
					Toast.makeText(LocationActivity.this, "Enter End Point", Toast.LENGTH_SHORT).show();
				} else {

					boolean isInternet = haveNetworkConnection();
					if (isInternet) {
						if (Constant.BodyguardOpt.equals("1")) {
							Intent intent = new Intent(LocationActivity.this, TrackConfirmActivity.class);
							Constant.TrackFrom = actxtSPoint.getText().toString();
							Constant.TrackTo = actxtSearch.getText().toString();
							startActivity(intent);
							finish();
						} else {
							Intent intent = new Intent(LocationActivity.this, MyFriendListActivity.class);
							Constant.TrackFrom = actxtSPoint.getText().toString();
							Constant.TrackTo = actxtSearch.getText().toString();
							startActivity(intent);
							finish();
						}
					}
				}
			}
		});

		// showLocation();
	}

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Start Point"));

			LatLng coordinate = new LatLng(latitude, longitude);
			CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15);
			googleMap.animateCamera(yourLocation);

			if (googleMap == null) {
				Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class DownloadTask extends AsyncTask<String, Void, String> {

		private int downloadType = 0;

		// Constructor
		public DownloadTask(int type) {
			this.downloadType = type;
		}

		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				Log.d("LocationActivity", "url is"+url[0]);
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			switch (downloadType) {
			case PLACES:
				// Creating ParserTask for parsing Google Places
				placesParserTask = new ParserTask(PLACES);

				// Start parsing google places json data
				// This causes to execute doInBackground() of ParserTask class
				placesParserTask.execute(result);

				break;

			case PLACES_DETAILS:
				// Creating ParserTask for parsing Google Places
				placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

				// Starting Parsing the JSON string
				// This causes to execute doInBackground() of ParserTask class
				placeDetailsParserTask.execute(result);
			}
		}
	}

	// For Source
	private class DownloadTask1 extends AsyncTask<String, Void, String> {

		private int downloadType = 0;

		// Constructor
		public DownloadTask1(int type) {
			this.downloadType = type;
		}

		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl1(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			switch (downloadType) {
			case PLACES:
				// Creating ParserTask for parsing Google Places
				placesParserTask = new ParserTask(PLACES);

				// Start parsing google places json data
				// This causes to execute doInBackground() of ParserTask class
				placesParserTask.execute(result);

				break;

			case PLACES_DETAILS:
				// Creating ParserTask for parsing Google Places
				placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

				// Starting Parsing the JSON string
				// This causes to execute doInBackground() of ParserTask class
				placeDetailsParserTask.execute(result);
			}
		}
	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	private String downloadUrl1(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

		int parserType = 0;

		public ParserTask(int type) {
			this.parserType = type;
		}

		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<HashMap<String, String>> list = null;

			try {
				jObject = new JSONObject(jsonData[0]);

				switch (parserType) {
				case PLACES:
					PlaceJSONParser placeJsonParser = new PlaceJSONParser();
					// Getting the parsed data as a List construct
					list = placeJsonParser.parse(jObject);
					break;
				case PLACES_DETAILS:
					PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
					// Getting the parsed data as a List construct
					list = placeDetailsJsonParser.parse(jObject);
				}

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			switch (parserType) {
			case PLACES:
				String[] from = new String[] { "description" };
				int[] to = new int[] { android.R.id.text1 };

				// Creating a SimpleAdapter for the AutoCompleteTextView
				SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1,
						from, to);

				// Setting the adapter
				actxtSearch.setAdapter(adapter);
				break;

			case PLACES_DETAILS:
				HashMap<String, String> hm = result.get(0);

				// Getting latitude from the parsed data
				double latitude = Double.parseDouble(hm.get("lat"));

				// Getting longitude from the parsed data
				double longitude = Double.parseDouble(hm.get("lng"));

				latitudeD = latitude;
				longitudeD = longitude;

				LatLng point = new LatLng(latitude, longitude);

				CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
						.zoom(15).build();
				googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

				MarkerOptions options = new MarkerOptions();
				options.position(point);
				options.title(Constant.checkInPlace);
				// options.snippet("Latitude:" + latitude + ",Longitude:" +
				// longitude);

				// Adding the marker in the Google Map
				googleMap.addMarker(options);

				break;
			}
		}
	}

	private class ParserTask1 extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

		int parserType1 = 0;

		public ParserTask1(int type) {
			this.parserType1 = type;
		}

		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<HashMap<String, String>> list = null;

			try {
				jObject = new JSONObject(jsonData[0]);

				switch (parserType1) {
				case PLACES:
					PlaceJSONParser placeJsonParser = new PlaceJSONParser();
					// Getting the parsed data as a List construct
					list = placeJsonParser.parse(jObject);
					break;
				case PLACES_DETAILS:
					PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
					// Getting the parsed data as a List construct
					list = placeDetailsJsonParser.parse(jObject);
				}

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			switch (parserType1) {
			case PLACES:
				String[] from = new String[] { "description" };
				int[] to = new int[] { android.R.id.text1 };

				// Creating a SimpleAdapter for the AutoCompleteTextView
				SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1,
						from, to);

				// Setting the adapter
				actxtSPoint.setAdapter(adapter);
				break;

			case PLACES_DETAILS:
				HashMap<String, String> hm = result.get(0);

				// Getting latitude from the parsed data
				double latitude = Double.parseDouble(hm.get("lat"));

				// Getting longitude from the parsed data
				double longitude = Double.parseDouble(hm.get("lng"));

				latitudeD = latitude;
				longitudeD = longitude;

				LatLng point = new LatLng(latitude, longitude);

				CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
						.zoom(15).build();
				googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

				MarkerOptions options = new MarkerOptions();
				options.position(point);
				options.title(Constant.checkInPlace);
				// options.snippet("Latitude:" + latitude + ",Longitude:" +
				// longitude);

				// Adding the marker in the Google Map
				googleMap.addMarker(options);

				break;
			}
		}
	}

	private String getAutoCompleteUrl(String place) {

		// Obtain browser key from https://code.google.com/apis/console
		/*OLD API*/
		//String key = "key=AIzaSyCu4z3dvOumTEqdsPhxD_ktEDbsCMMsFrY";
		
		String key = "key="+Constant.GOOGLE_MAP_API_KEY;

		// place to be be searched
		String input = "input=" + place;

		// place type to be searched
		String types = "types=geocode";

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = input + "&" + types + "&" + sensor + "&" + key;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

		return url;
	}

	private String getPlaceDetailsUrl(String ref) {

		// Obtain browser key from https://code.google.com/apis/console
		/*OLD key
		String key = "key=AIzaSyCu4z3dvOumTEqdsPhxD_ktEDbsCMMsFrY";*/
		String key = "key="+Constant.GOOGLE_MAP_API_KEY;
		

		// reference of place
		String reference = "reference=" + ref;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = reference + "&" + sensor + "&" + key;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;

		return url;
	}

	public void showLocation() {
		MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("");
		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		googleMap.addMarker(marker);

		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(13)
				.build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		googleMap.getUiSettings().setCompassEnabled(true);
		googleMap.getUiSettings().setRotateGesturesEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		googleMap.setMyLocationEnabled(true);

		// Set the Current Location Button
		View locationButton = ((View) findViewById(R.id.map).getParent()).findViewById(2);
		RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
		rlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		rlp.setMargins(20, 20, 20, 20);

		Geocoder geocoder;
		List<Address> addresses = null;
		geocoder = new Geocoder(this, Locale.getDefault());

		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Here 1 represent max location result to returned, by documents it
			// recommended 1 to 5

		String locality = addresses.get(0).getLocality();
		String address = addresses.get(0).getAddressLine(0);
		String address1 = addresses.get(0).getAddressLine(1);
		String address2 = addresses.get(0).getAddressLine(2);
		String city = addresses.get(0).getLocality();
		String state = addresses.get(0).getAdminArea();
		String country = addresses.get(0).getCountryName();
		String postalCode = addresses.get(0).getPostalCode();
		String knownName = addresses.get(0).getFeatureName(); // Only if
																// available
																// else return
																// NULL

		// Toast.makeText(getApplicationContext(), locality + address + address1
		// + address2 + city, Toast.LENGTH_LONG)
		// .show();

		// System.out.println("locality: " + locality);
		// System.out.println("address: " + address);
		// System.out.println("address1: " + address1);
		// System.out.println("address2: " + address2);
		// System.out.println("city: " + city);
		// System.out.println("state: " + state);
		// System.out.println("country: " + country);
		// System.out.println("postalCode: " + postalCode);
		// System.out.println("knownName: " + knownName);

		Constant.checkInPlace = address1;
		actxtSearch.setText(address1);
	}

	public void showLocation1() {
		MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("");
		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		googleMap.addMarker(marker);

		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(13)
				.build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		googleMap.getUiSettings().setCompassEnabled(true);
		googleMap.getUiSettings().setRotateGesturesEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		googleMap.setMyLocationEnabled(true);

		// Set the Current Location Button
		View locationButton = ((View) findViewById(R.id.map).getParent()).findViewById(2);
		RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
		rlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		rlp.setMargins(20, 20, 20, 20);

		Geocoder geocoder;
		List<Address> addresses = null;
		geocoder = new Geocoder(this, Locale.getDefault());

		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Here 1 represent max location result to returned, by documents it
			// recommended 1 to 5

		String locality = addresses.get(0).getLocality();
		String address = addresses.get(0).getAddressLine(0);
		String address1 = addresses.get(0).getAddressLine(1);
		String address2 = addresses.get(0).getAddressLine(2);
		String city = addresses.get(0).getLocality();
		String state = addresses.get(0).getAdminArea();
		String country = addresses.get(0).getCountryName();
		String postalCode = addresses.get(0).getPostalCode();
		String knownName = addresses.get(0).getFeatureName(); // Only if
																// available
																// else return
																// NULL

		// Toast.makeText(getApplicationContext(), locality + address + address1
		// + address2 + city, Toast.LENGTH_LONG)
		// .show();

		// System.out.println("locality: " + locality);
		// System.out.println("address: " + address);
		// System.out.println("address1: " + address1);
		// System.out.println("address2: " + address2);
		// System.out.println("city: " + city);
		// System.out.println("state: " + state);
		// System.out.println("country: " + country);
		// System.out.println("postalCode: " + postalCode);
		// System.out.println("knownName: " + knownName);

		Constant.checkInPlace = address1;
		actxtSPoint.setText(address1);
	}

	public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
		StringBuilder urlString = new StringBuilder();
		urlString.append("https://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(sourcelat));
		urlString.append(",");
		urlString.append(Double.toString(sourcelog));
		urlString.append("&destination=");// to
		urlString.append(Double.toString(destlat));
		urlString.append(",");
		urlString.append(Double.toString(destlog));
		urlString.append("&sensor=false&mode=driving&alternatives=true");
		urlString.append("&key=" + Constant.GOOGLE_MAP_API_KEY);
		MVCLog.doLog(TAG, urlString.toString() );
		return urlString.toString();
	}

	public void drawPath(String result) {

		try {
			// Tranform the string into a json object
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);
			
			/*Abhay*/
			//fit the route on screen
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			 for(int i = 0; i < list.size();i++){
			        builder.include(list.get(i));
			    }
			LatLngBounds bounds = builder.build();
			int padding = 20;
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
			
			Polyline line = googleMap
					.addPolyline(new PolylineOptions().addAll(list).width(12).color(Color.parseColor("#05b1fb"))// Google
																												// maps
																												// blue
																												// color
							.geodesic(true));
			googleMap.animateCamera(cu);

		} catch (JSONException e) {

		}
	}

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

	public class connectAsyncTask extends AsyncTask<Void, Void, String> {
		private ProgressDialog progressDialog;
		String url;

		connectAsyncTask(String urlPass) {
			url = urlPass;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(LocationActivity.this);
			progressDialog.setMessage("Fetching route, Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getJSONFromUrl(url);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (result != null) {
				drawPath(result);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
	
	//if something goes wrong go back to home activity
	public void backToHome(){
		Toast.makeText(getApplicationContext(), "Something went wrong, please try again..", Toast.LENGTH_LONG).show();
		Intent backIntent = new Intent(LocationActivity.this, HomeActivity.class);
		startActivity(backIntent);
		finish();
	}

}
