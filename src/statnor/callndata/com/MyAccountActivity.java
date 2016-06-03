package statnor.callndata.com;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.ServiceHandler;

import statnor.callndata.com.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyAccountActivity extends Activity {

	String LoginFlag;
	SharedPreferences pref;
	int Eday, Emonth, Eyear, Cday, Cmonth, Cyear;
	ImageView imgLogout, imgMyService, imgProfileUpdate, imgMyConnect;

	ProgressDialog pDialog, pDialog1;
	JSONArray LoginJA;

	String userId, custId, servicesEndDate;
	SimpleDateFormat sdf;
	String CDate, EDate;
	Date date1, date2;
	int activeFlag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);

		setTitle("My Account");
		pref = getApplicationContext().getSharedPreferences("MyPref", 0);

		imgLogout = (ImageView) findViewById(R.id.imgLogout);
		imgMyService = (ImageView) findViewById(R.id.imgMyService);
		imgMyConnect = (ImageView) findViewById(R.id.imgMyConnect);
		imgProfileUpdate = (ImageView) findViewById(R.id.imgProfileUpdate);

		imgMyConnect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyAccountActivity.this, MyBodyguardActivity.class);
				startActivity(intent);
			}
		});

		imgProfileUpdate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyAccountActivity.this, ProfileUpdateActivity.class);
				startActivity(intent);
			}
		});

		imgMyService.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyAccountActivity.this, MyServiceActivity.class);
				startActivity(intent);
			}
		});

		imgLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final Dialog dialog = new Dialog(MyAccountActivity.this);
				dialog.setCancelable(true);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				dialog.setContentView(R.layout.dialog_box_logout);

				Button yes = (Button) dialog.findViewById(R.id.logout_yes);
				Button no = (Button) dialog.findViewById(R.id.logout_no);

				yes.setText(R.string.yes);
				no.setText(R.string.no);

				yes.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						LoginFlag = pref.getString("LoginFlag", null);

						Editor editor = pref.edit();
						editor.putString("LoginFlag", "0");
						editor.commit();

						Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
						startActivity(intent);
						HomeActivity.HObj.finish();
						finish();
						dialog.dismiss();
					}
				});

				no.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				try {
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class LoginJSON extends AsyncTask<Void, Void, Void> {

		String status = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyAccountActivity.this);
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

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (pDialog.isShowing()) {
				pDialog.dismiss();
			}

			if (status.equals("1")) {
				final Dialog dialog = new Dialog(MyAccountActivity.this);
				dialog.setCancelable(true);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				dialog.setContentView(R.layout.dialogbox);

				Button ok = (Button) dialog.findViewById(R.id.okbutton);
				Button Cencelbutton = (Button) dialog.findViewById(R.id.Cencelbutton);
				TextView txtMsg = (TextView) dialog.findViewById(R.id.txtMsg);
				TextView txtMsgAD = (TextView) dialog.findViewById(R.id.txtMsgAD);

				txtMsg.setText(servicesEndDate);

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

				if (date1.compareTo(date2) == 0) {
					activeFlag = 1;
					txtMsgAD.setText("Your Account is Active");
					Cencelbutton.setVisibility(View.GONE);
					txtMsgAD.setTextColor(Color.parseColor("#006400"));
				} else if (date1.before(date2)) {
					activeFlag = 1;
					Cencelbutton.setVisibility(View.GONE);
					txtMsgAD.setText("Your Account is Active");
					txtMsgAD.setTextColor(Color.parseColor("#006400"));
				} else {
					activeFlag = 0;
					Cencelbutton.setVisibility(View.VISIBLE);
					txtMsgAD.setText("Your Account is Deactive.\nDo you want to activate ?");
					txtMsgAD.setTextColor(Color.parseColor("#ff0000"));
				}

				ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (activeFlag == 1) {
							dialog.dismiss();
						} else if (activeFlag == 0) {
							dialog.dismiss();
							new UpgradeRequestJSON().execute();
						}
					}
				});

				Cencelbutton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				try {
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(MyAccountActivity.this, "Oops! Try again later.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	class UpgradeRequestJSON extends AsyncTask<Void, Void, Void> {

		String status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog1 = new ProgressDialog(MyAccountActivity.this);
			pDialog1.setMessage("Please Wait...");
			pDialog1.setCancelable(false);
			pDialog1.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				ServiceHandler servicehandler = new ServiceHandler();
				String Data = servicehandler.makeServiceCall(Constant.UpgradeJSON + userId + "&CustomerID=" + custId,
						ServiceHandler.GET);

				if (Data != null) {
					LoginJA = new JSONArray(Data);

					for (int i = 0; i < LoginJA.length(); i++) {
						JSONObject obj = new JSONObject();
						obj = LoginJA.getJSONObject(i);
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

			if (pDialog1.isShowing()) {
				pDialog1.dismiss();
			}
			if (status.equals("1")) {
				final Dialog dialog = new Dialog(MyAccountActivity.this);
				dialog.setCancelable(true);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				dialog.setContentView(R.layout.dialogbox);

				Button ok = (Button) dialog.findViewById(R.id.okbutton);
				Button Cencelbutton = (Button) dialog.findViewById(R.id.Cencelbutton);
				TextView message_title_is = (TextView) dialog.findViewById(R.id.message_title_is);
				TextView txtMsg = (TextView) dialog.findViewById(R.id.txtMsg);
				TextView txtMsgAD = (TextView) dialog.findViewById(R.id.txtMsgAD);

				Cencelbutton.setVisibility(View.GONE);
				message_title_is.setVisibility(View.GONE);
				txtMsgAD.setVisibility(View.GONE);

				txtMsg.setText("You have successfully requested for activation.");

				ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				try {
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
