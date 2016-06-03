package statnor.callndata.com;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.ServiceHandler;
import com.callndata.item.MyBodyguardItem;

import statnor.callndata.com.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	ImageView imgThief, imgPolice, imgFire, imgAmbulance, imgBodyguard, imgMyAccount, imgBodyguardPlus;
	public static HomeActivity HObj = null;

	SimpleDateFormat sdf;
	String CDate, EDate;
	Date date1, date2;
	int activeFlag = 0;

	JSONArray LoginJA;
	ProgressDialog pDialog, pDialog1;
	String userId, custId, servicesEndDate;
	int Eday, Emonth, Eyear, Cday, Cmonth, Cyear;

	SharedPreferences pref;
	String Track_Id, Tracker_Name;

	LinearLayout llTrack;
	ListView lstMyConnects;

	ArrayList<String> tempAL = new ArrayList<String>();;
	MyBodyguardItem mybodyguarditem;
	ArrayAdapter<String> adapter;
	public static ArrayList<MyBodyguardItem> mybodyguardAL = new ArrayList<MyBodyguardItem>();
	
	public void showMyBodyGuards(View v){
		if( !tempAL.isEmpty() ) {
			Intent intent = new Intent(HomeActivity.this, BodyGuard.class);
			intent.putExtra("bodyGuardList", tempAL);
			startActivity(intent); 
		} else {
			Toast.makeText(this, "No tracking is available", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// startService(new Intent(this, ServiceTest.class));

		HObj = this;

		pref = getApplicationContext().getSharedPreferences("MyPref", 0);
		Track_Id = pref.getString("Track_Id", null);
		Tracker_Name = pref.getString("Tracker_Name", null);

		imgFire = (ImageView) findViewById(R.id.imgFire);
		llTrack = (LinearLayout) findViewById(R.id.llTrack);
		imgThief = (ImageView) findViewById(R.id.imgThief);
		imgPolice = (ImageView) findViewById(R.id.imgPolice);
		imgAmbulance = (ImageView) findViewById(R.id.imgAmbulance);
		imgBodyguard = (ImageView) findViewById(R.id.imgBodyguard);
		imgMyAccount = (ImageView) findViewById(R.id.imgMyAccount);
		lstMyConnects = (ListView) findViewById(R.id.lstMyConnects);
		imgBodyguardPlus = (ImageView) findViewById(R.id.imgBodyguardPlus);

		imgThief.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ConfirmActivity.class);
				intent.putExtra("type", "Burglary");
				startActivity(intent);
			}
		});
		imgPolice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ConfirmActivity.class);
				intent.putExtra("type", "Duress");
				startActivity(intent);
			}
		});
		imgFire.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ConfirmActivity.class);
				intent.putExtra("type", "Fire");
				startActivity(intent);
			}
		});
		imgAmbulance.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ConfirmActivity.class);
				intent.putExtra("type", "Ambulance");
				startActivity(intent);
			}
		});

		imgBodyguard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Track_Id = pref.getString("Track_Id", null);
				Tracker_Name = pref.getString("Tracker_Name", null);

				if (Track_Id != null && !Track_Id.isEmpty()) {
					Intent intent = new Intent(HomeActivity.this, CancelTrackingActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
					startActivity(intent);
					Constant.BodyguardOpt = "0";
				}
			}
		});

		imgMyAccount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, MyAccountActivity.class);
				startActivity(intent);
			}
		});

		imgBodyguardPlus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(HomeActivity.this,
				// AccountStatusBodyguard.class);
				// startActivity(intent);

				new LoginJSON().execute();

			}
		});

		llTrack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(HomeActivity.this,
				// UpdateTrackActivity.class);
				// startActivity(intent);
			}
		});

		lstMyConnects.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/*Intent intent = new Intent(HomeActivity.this, UpdateTrackActivity.class);
				intent.putExtra("trackid", mybodyguardAL.get(position-1).getTrackId());
				startActivity(intent);*/
				
			}
		});

		new MyMonitorListJSON().execute();
	}

	class LoginJSON extends AsyncTask<Void, Void, Void> {

		String status = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(HomeActivity.this);
			pDialog.setMessage("Please Wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				ServiceHandler servicehandler = new ServiceHandler();
				String Data = servicehandler.makeServiceCall(
						Constant.LoginJSON + Constant.UName + "&upassword=" + Constant.Password, ServiceHandler.GET);

				if (Data != null) {
					LoginJA = new JSONArray(Data);

					for (int i = 0; i < LoginJA.length(); i++) {
						JSONObject obj = new JSONObject();
						obj = LoginJA.getJSONObject(i);
						if (i == 0) {
							status = obj.getString("status");
						} else if (i == 1) {
							if (status.equals("1")) {
								userId = obj.getString("num");
								custId = obj.getString("CustomerID");
								servicesEndDate = obj.getString("serviceEndDate");
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (pDialog.isShowing()) {
				pDialog.dismiss();
			}

			if (status.equals("1")) {

				String[] DateParts = servicesEndDate.split("-");
				Emonth = Integer.parseInt(DateParts[0]);
				Eday = Integer.parseInt(DateParts[1]);
				Eyear = Integer.parseInt(DateParts[2]);

				Calendar cal = Calendar.getInstance();
				Cday = cal.get(Calendar.DAY_OF_MONTH);
				Cmonth = cal.get(Calendar.MONTH) + 1;
				Cyear = cal.get(Calendar.YEAR);

				sdf = new SimpleDateFormat("MM-dd-yyyy");

				CDate = Cmonth + "-" + Cday + "-" + Cyear;
				EDate = Emonth + "-" + Eday + "-" + Eyear;

				try {
					date1 = sdf.parse(CDate);
					date2 = sdf.parse(EDate);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Track_Id = pref.getString("Track_Id", null);
				Tracker_Name = pref.getString("Tracker_Name", null);

				if (Track_Id != null && !Track_Id.isEmpty()) {
					Intent intent = new Intent(HomeActivity.this, CancelTrackingActivity.class);
					startActivity(intent);
				} else {

					if (date1.compareTo(date2) == 0) {

						Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
						startActivity(intent);
						Constant.BodyguardOpt = "1";

					} else if (date1.before(date2)) {
						Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
						startActivity(intent);
						Constant.BodyguardOpt = "1";
					} else {
						Intent intent = new Intent(HomeActivity.this, AccountStatusBodyguard.class);
						startActivity(intent);
					}

				}
			} else {
				Toast.makeText(HomeActivity.this, "Oops! Try again later.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	class MyMonitorListJSON extends AsyncTask<Void, Void, Void> {

		String status= "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog1 = new ProgressDialog(HomeActivity.this);
			pDialog1.setMessage("Please Wait...");
			pDialog1.setCancelable(true);
			pDialog1.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			mybodyguardAL.clear();
			ServiceHandler serviceHandler = new ServiceHandler();
			try {
				String URL = Constant.MyMonitorListJSON + Constant.UserId;
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
							mybodyguarditem = new MyBodyguardItem();
							mybodyguarditem.setTrackId(obj.getString("TrackID"));
							mybodyguarditem.setName(obj.getString("fullName"));
							mybodyguarditem.setNumber(obj.getString("phoneNumbers"));

							mybodyguardAL.add(mybodyguarditem);
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
			if (pDialog1.isShowing()) {
				pDialog1.dismiss();
			}

			if (status.equals("1")) {
				
				for (int i = 0; i < mybodyguardAL.size(); i++) {
					tempAL.add(mybodyguardAL.get(i).getName() + " - " + mybodyguardAL.get(i).getNumber());
				}

				/*Abhay*/
				/*Created new activity to show companion track list*/
				/*adapter = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_list_item_1, tempAL);
				lstMyConnects.setAdapter(adapter);*/
			}

		}
	}

}
