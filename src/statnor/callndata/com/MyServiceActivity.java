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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyServiceActivity extends Activity {

	JSONArray LoginJA;
	ProgressDialog pDialog, pDialog1;
	String userId, custId, servicesEndDate;
	int Eday, Emonth, Eyear, Cday, Cmonth, Cyear;

	SimpleDateFormat sdf;
	String CDate, EDate;
	Date date1, date2;
	int activeFlag = 0;

	TextView txtReqDate, txtExpDate, txtStatus;
	Button btnUpgrade;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_services);
		setTitle("My Service");

		txtReqDate = (TextView) findViewById(R.id.txtReqDate);
		txtExpDate = (TextView) findViewById(R.id.txtExpDate);
		txtStatus = (TextView) findViewById(R.id.txtStatus);

		btnUpgrade = (Button) findViewById(R.id.btnUpgrade);

		new LoginJSON().execute();

		btnUpgrade.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new UpgradeRequestJSON().execute();
			}
		});

	}

	class LoginJSON extends AsyncTask<Void, Void, Void> {

		String status = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyServiceActivity.this);
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

				txtReqDate.setText(CDate);
				txtExpDate.setText(EDate);

				try {
					date1 = sdf.parse(CDate);
					date2 = sdf.parse(EDate);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (date1.compareTo(date2) == 0) {
					activeFlag = 1;
					txtStatus.setText("Active");
					txtStatus.setTextColor(Color.parseColor("#0D7801"));
				} else if (date1.before(date2)) {
					activeFlag = 1;
					txtStatus.setText("Active");
					txtStatus.setTextColor(Color.parseColor("#0D7801"));
				} else {
					activeFlag = 0;
					txtStatus.setText("Deactive");
					txtStatus.setTextColor(Color.parseColor("#ff0000"));
				}
			} else {
				Toast.makeText(MyServiceActivity.this, "Oops! Try again later.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	class UpgradeRequestJSON extends AsyncTask<Void, Void, Void> {

		String status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog1 = new ProgressDialog(MyServiceActivity.this);
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
				final Dialog dialog = new Dialog(MyServiceActivity.this);
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
						finish();
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
