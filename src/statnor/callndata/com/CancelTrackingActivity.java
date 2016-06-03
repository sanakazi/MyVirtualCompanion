package statnor.callndata.com;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.ServiceHandler;
import com.callndata.Others.ServiceTest;
import com.callndata.item.TrackDetailsItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import statnor.callndata.com.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CancelTrackingActivity extends Activity {

	Button btnConfirm;
	TextView txtTracker;
	SharedPreferences pref;
	ProgressDialog pDialog;
	String Track_Id, Tracker_Name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cancel_tracking);

		txtTracker = (TextView) findViewById(R.id.txtTracker);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);

		pref = getApplicationContext().getSharedPreferences("MyPref", 0);
		Track_Id = pref.getString("Track_Id", null);
		Tracker_Name = pref.getString("Tracker_Name", null);

		txtTracker.setText(Tracker_Name + " is tracking you.");

		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Track_Id = pref.getString("Track_Id", null);
				Tracker_Name = pref.getString("Tracker_Name", null);

				// Intent intent = new Intent(CancelTrackingActivity.this,
				// ServiceTest.class);
				// stopService(intent);

				new CompleteTrackingJSON().execute();
			}
		});

	}

	class CompleteTrackingJSON extends AsyncTask<Void, Void, Void> {

		String status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CancelTrackingActivity.this);
			pDialog.setMessage("Updating Locations...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler servicehandler = new ServiceHandler();

			try {
				String URL = Constant.CompleteTracking + Track_Id + "&updateStatus=Completed";
				URL = URL.replace(" ", "%20");
				String Data = servicehandler.makeServiceCall(URL, ServiceHandler.GET);

				JSONArray arr = new JSONArray(Data);
				JSONObject obj = new JSONObject();

				for (int i = 0; i < arr.length(); i++) {
					if (i == 0) {
						obj = arr.getJSONObject(i);
						status = obj.getString("status");
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

			try {

				if (status.equals("1")) {
					stopService(new Intent(getApplicationContext(), ServiceTest.class));
					Editor editor = pref.edit();
					editor.putString("Track_Id", null);
					editor.putString("Tracker_Name", null);
					editor.commit();
					Toast.makeText(CancelTrackingActivity.this, "Tracking Completed...", Toast.LENGTH_SHORT).show();
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
