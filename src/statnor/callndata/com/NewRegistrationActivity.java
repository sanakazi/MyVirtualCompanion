package statnor.callndata.com;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.GPSTracker;
import com.callndata.Others.ServiceHandler;

import statnor.callndata.com.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewRegistrationActivity extends Activity {

	Button btn_login;
	ProgressDialog pdialog;
	String custId, name, street, area, city, code, phonemum, email, password, longt, lat;
	TextView etCustId, etName, etStreet, etAreaName, etCity, etPostCode, etPhoneNum, etEmail, etPassword;

	JSONArray NewRegJA;
	Double latitude = 0.0, longitude = 0.0;

	String status = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_registration);

		etCustId = (EditText) findViewById(R.id.etCustId);
		etName = (EditText) findViewById(R.id.etName);
		etStreet = (EditText) findViewById(R.id.etStreet);
		etAreaName = (EditText) findViewById(R.id.etAreaName);
		etCity = (EditText) findViewById(R.id.etCity);
		etPostCode = (EditText) findViewById(R.id.etPostCode);
		etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etPassword = (EditText) findViewById(R.id.etPassword);
		btn_login = (Button) findViewById(R.id.btn_login);

		try {
			GPSTracker gps = new GPSTracker(NewRegistrationActivity.this);
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

		btn_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				custId = etCustId.getText().toString();
				name = etName.getText().toString();
				street = etStreet.getText().toString();
				area = etAreaName.getText().toString();
				city = etCity.getText().toString();
				code = etPostCode.getText().toString();
				phonemum = etPhoneNum.getText().toString();
				email = etEmail.getText().toString();
				password = etPassword.getText().toString();

				if (name.equals("") || name.equals(null) || name.isEmpty()) {
					Toast.makeText(NewRegistrationActivity.this, "Enter your Name", Toast.LENGTH_SHORT).show();
				} else if (phonemum.equals("")) {
					Toast.makeText(NewRegistrationActivity.this, "Enter valid Phone Number", Toast.LENGTH_SHORT).show();
				} else if (email.equals("") || !isValidEmail(email)) {
					Toast.makeText(NewRegistrationActivity.this, "Enter valid Email-Id", Toast.LENGTH_SHORT)
							.show();
				} else if (password.equals("") || password.equals(null) || password.isEmpty()) {
					Toast.makeText(NewRegistrationActivity.this, "Enter your Password", Toast.LENGTH_SHORT).show();
				} else {
					new RegisterJSON().execute();
				}
				// register();
			}
		});

	}

	class RegisterJSON extends AsyncTask<Void, Void, Void> {

		String status = "0", URL;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new ProgressDialog(NewRegistrationActivity.this);
			pdialog.setMessage("Please Wait...");
			pdialog.setCancelable(false);
			pdialog.show();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler serviceHandler = new ServiceHandler();

			try {
				URL = Constant.RegisterJSON + URLEncoder.encode(name, "utf-8") + "&CustomerID="
						+ URLEncoder.encode(custId, "utf-8") + "&address=" + URLEncoder.encode(street, "utf-8")
						+ "&areaName=" + URLEncoder.encode(area, "utf-8") + "&cityID="
						+ URLEncoder.encode(city, "utf-8") + "&zipCode=" + URLEncoder.encode(code, "utf-8")
						+ "&longitude=" + URLEncoder.encode("7717.88", "utf-8") + "&latitude="
						+ URLEncoder.encode("7776373.88", "utf-8") + "&phoneNumbers="
						+ URLEncoder.encode(phonemum, "utf-8") + "&emailID=" + URLEncoder.encode(email, "utf-8")
						+ "&upassword=" + password + "&ServiceType=" + URLEncoder.encode("Free", "utf-8");

				URL = URL.replace(" ", "%20");

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			String Data = serviceHandler.makeServiceCall(URL, ServiceHandler.GET);

			try {

				if (Data != null) {
					NewRegJA = new JSONArray(Data);

					for (int i = 0; i < NewRegJA.length(); i++) {

						JSONObject obj = new JSONObject();
						obj = NewRegJA.getJSONObject(i);

						if (i == 0) {
							status = obj.getString("status");
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
			if (pdialog.isShowing()) {
				pdialog.dismiss();
			}

			if (status.equals("1")) {
				Toast.makeText(NewRegistrationActivity.this, "User Registered Successfully...", Toast.LENGTH_SHORT)
						.show();
				finish();
			} else {
				Toast.makeText(NewRegistrationActivity.this, "Oops! Try again later...", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	public static final boolean isValidPhoneNumber(CharSequence target) {
		if (target.length() != 10) {
			return false;
		} else {
			return android.util.Patterns.PHONE.matcher(target).matches();
		}
	}
}
