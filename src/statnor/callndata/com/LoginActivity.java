package statnor.callndata.com;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.ServiceHandler;

import statnor.callndata.com.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	Button btn_login;
	TextView txtNewRegistration;
	CheckBox chkRPass;
	EditText etUsername, etPassword;

	String Username, Password, RPassValue = "0";

	ProgressDialog pDialog;
	JSONArray LoginJA;

	SharedPreferences pref;
	String LoginFlag;
	
	String userid, custid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		pref = getApplicationContext().getSharedPreferences("MyPref", 0);
		LoginFlag = pref.getString("LoginFlag", null);

		if (LoginFlag == null || LoginFlag == "null") {
			Editor editor = pref.edit();
			editor.putString("LoginFlag", "0");
			editor.commit();
		}

		btn_login = (Button) findViewById(R.id.btn_login);
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		chkRPass = (CheckBox) findViewById(R.id.chkRPass);
		txtNewRegistration = (TextView) findViewById(R.id.txtNewRegistration);

		btn_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Username = etUsername.getText().toString().trim();
				Password = etPassword.getText().toString().trim();

				if (Username.equals("") || Username.equals(null) || Username.isEmpty()) {
					Toast.makeText(LoginActivity.this, "Enter your email-id", Toast.LENGTH_SHORT).show();
				} else if (Password.equals("") || Password.equals(null) || Password.isEmpty()) {
					Toast.makeText(LoginActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
				} else {

					boolean isInternet = haveNetworkConnection();
					if (isInternet) {
						if (chkRPass.isChecked()) {
							RPassValue = "1";
						}
						new LoginJSON().execute();
					} else {
						Toast.makeText(LoginActivity.this, "No Internet Connection !!!", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

		txtNewRegistration.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, NewRegistrationActivity.class);
				startActivity(intent);
			}
		});
	}

	class LoginJSON extends AsyncTask<Void, Void, Void> {

		String status = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Please Wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				ServiceHandler servicehandler = new ServiceHandler();
				String Data = servicehandler.makeServiceCall(Constant.LoginJSON + Username + "&upassword=" + Password,
						ServiceHandler.GET);

				if (Data != null) {
					LoginJA = new JSONArray(Data);

					for (int i = 0; i < LoginJA.length(); i++) {
						JSONObject obj = new JSONObject();
						obj = LoginJA.getJSONObject(i);
						if (i == 0) {
							status = obj.getString("status");
						} else if (i == 1) {
							if (status.equals("1")) {
								Constant.UserId = obj.getString("num");
								userid = obj.getString("num");
								Constant.UserType = obj.getString("userType");
								Constant.ServiceType = obj.getString("ServiceType");
								Constant.FullName = obj.getString("fullName");
								Constant.CustId = obj.getString("CustomerID");
								custid = obj.getString("CustomerID");
								Constant.EmailId = obj.getString("emailID");
								
								SharedPreferences accSharedPref;
								accSharedPref = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = accSharedPref.edit();
								
								editor.clear();
								editor.putString(ProfileUpdateActivity.NAME, obj.getString("fullName"));
								editor.putString(ProfileUpdateActivity.STREET, obj.getString("address"));
								editor.putString(ProfileUpdateActivity.AREA, obj.getString("areaName"));
								editor.putString(ProfileUpdateActivity.CITY, obj.getString("cityName"));
								editor.putString(ProfileUpdateActivity.POST, obj.getString("zipCode"));
								editor.putString(ProfileUpdateActivity.PHONE, obj.getString("phoneNumbers"));
								editor.commit();
								
							}
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
				Toast.makeText(LoginActivity.this, "Login Successfull !!!", Toast.LENGTH_SHORT).show();

				if (RPassValue.equals("1")) {
					if (LoginFlag == null || LoginFlag.equals("0")) {
						Editor editor = pref.edit();
						editor.putString("LoginFlag", "1");
						editor.putString("userid", userid);
						editor.putString("custid", custid);
						editor.putString("username", Username);
						editor.putString("password", Password);
						editor.commit();
					}
				}

				Constant.UName = Username;
				Constant.Password = Password;

				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();

			} else if (status.equals("0")) {
				Toast.makeText(LoginActivity.this, "Wrong Username or Password.", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(LoginActivity.this, "Oops! Try again later.", Toast.LENGTH_SHORT).show();
			}
		}
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

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}
}
