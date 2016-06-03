package statnor.callndata.com;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.GPSTracker;
import com.callndata.Others.ServiceHandler;
/*import com.google.android.gms.internal.fo;*/

import statnor.callndata.com.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmActivity extends Activity {

	Button btnConfirm;
	TextView txtConfirmType;
	boolean netCheck = false;
	Double latitude, longitude;
	String type, cellNo, msg, dateTime;

	ProgressDialog pDialog, pDialog1;
	JSONArray NewRegJA;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm);

		new getMobileNumberJSON().execute();

		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		txtConfirmType = (TextView) findViewById(R.id.txtConfirmType);

		try {
			GPSTracker gps = new GPSTracker(ConfirmActivity.this);
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

		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		Date now = new Date();
		dateTime = fmt.format(now);

		Intent i = getIntent();

		type = i.getStringExtra("type");

		String mPhoneNumber = null;
		try {
			TelephonyManager tMgr = (TelephonyManager) getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			mPhoneNumber = tMgr.getLine1Number();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// if (mPhoneNumber.equals(null) || mPhoneNumber.isEmpty() ||
		// mPhoneNumber.equals("")) {
		// cellNo = "8767477454";
		// } else {
		// cellNo = mPhoneNumber;
		// }
		msg = "This is Emergency.. : " + type + "\nUsername : " + Constant.FullName;

		txtConfirmType.setText(type);

		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// sendSMS();

				boolean isInternet = haveNetworkConnection();
				if (isInternet) {
					new ConfirmJSON().execute();
				} else {
					sendSMS();
				}

			}
		});

	}

	class ConfirmJSON extends AsyncTask<Void, Void, Void> {

		String Status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ConfirmActivity.this);
			pDialog.setMessage("Please Wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String currentDateandTime = sdf.format(new Date());

			ServiceHandler servicehandler = new ServiceHandler();
			String URL = Constant.ConfirmJSON + Constant.UserId + "&CustomerID=" + Constant.CustId + "&supportType="
					+ type + "&longitude=" + latitude + "&latitude=" + longitude + "&localDateTime="
					+ currentDateandTime;
			URL = URL.replace(" ", "%20");
			String Data = servicehandler.makeServiceCall(URL, ServiceHandler.GET);

			try {
				if (Data != null) {
					NewRegJA = new JSONArray(Data);

					for (int i = 0; i < NewRegJA.length(); i++) {

						JSONObject obj = new JSONObject();
						obj = NewRegJA.getJSONObject(i);

						if (i == 0) {
							Status = obj.getString("status");
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

			if (Status.equals("1")) {
				Toast.makeText(ConfirmActivity.this, "Message sent successfully...", Toast.LENGTH_SHORT).show();
				sendSMS();
			} else {
				Toast.makeText(ConfirmActivity.this, "Oops! Try again later...", Toast.LENGTH_SHORT).show();
			}

		}

	}

	public void sendSMS() {

		String smsSent = "SMS_SENT";
		String smsDelivered = "SMS_DELIVERED";

		msg = msg + "\n\n Latitude : " + latitude + "\n Longitude : " + longitude;

		PendingIntent sendPi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(smsSent), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(smsDelivered), 0);

		// Receiver for Sent SMS.
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(arg0, "SMS sent", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(arg0, "Generic failure", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(arg0, "No service", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(arg0, "Null PDU", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(arg0, "Radio off", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(smsSent));

		// Receiver for Delivered SMS.
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(arg0, "SMS delivered", Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(arg0, "SMS not delivered", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(smsDelivered));

		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(cellNo, null, msg, sendPi, deliveredPI);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finish();
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

	class getMobileNumberJSON extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog1 = new ProgressDialog(ConfirmActivity.this);
			pDialog1.setMessage("Please wait...");
			pDialog1.setCancelable(false);
			pDialog1.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler serviceHandler = new ServiceHandler();

			try {
				String Data = serviceHandler.makeServiceCall(Constant.getPhoneNumberJSON, ServiceHandler.GET);
				JSONArray arr = new JSONArray(Data);

				for (int i = 0; i < arr.length(); i++) {
					if (i == 1) {
						JSONObject obj = new JSONObject();
						obj = arr.getJSONObject(i);
						cellNo = obj.getString("phoneNumbers");
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
		}
	}
}
