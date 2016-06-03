package statnor.callndata.com;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.ServiceHandler;
import com.callndata.adapter.MyBodyguardAdapter;
import com.callndata.adapter.MyBodyguardApproveRejectAdapter;
import com.callndata.item.MyBodyguardApproveRejectItem;
import com.callndata.item.MyBodyguardItem;

import statnor.callndata.com.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MyBodyguardActivity extends Activity {

	LinearLayout llActive, llPending;
	public static ListView lstMyActiveBodyguard, lstMyPendingBodyguard;

	ProgressDialog pDialog;
	ImageView imgAddBodyguard;
	MyBodyguardAdapter adapter;
	MyBodyguardItem mybodyguarditem;
	MyBodyguardApproveRejectAdapter adapterBAR;
	MyBodyguardApproveRejectItem myBodyguardApproveRejectItem;
	ArrayList<MyBodyguardItem> mybodyguardAL = new ArrayList<MyBodyguardItem>();
	ArrayList<MyBodyguardApproveRejectItem> myPendingbodyguardAL = new ArrayList<MyBodyguardApproveRejectItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_bodyguard);

		setTitle("My Bodyguard");

		llActive = (LinearLayout) findViewById(R.id.llActive);
		llPending = (LinearLayout) findViewById(R.id.llPending);
		imgAddBodyguard = (ImageView) findViewById(R.id.imgAddBodyguard);
		lstMyActiveBodyguard = (ListView) findViewById(R.id.lstMyActiveBodyguard);
		lstMyPendingBodyguard = (ListView) findViewById(R.id.lstMyPendingBodyguard);

		lstMyActiveBodyguard.setVisibility(View.VISIBLE);
		lstMyPendingBodyguard.setVisibility(View.GONE);

		imgAddBodyguard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MyBodyguardActivity.this, AddNewBodyGuardActivity.class);
				startActivity(intent);
			}
		});

		llActive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mybodyguardAL.clear();
				myPendingbodyguardAL.clear();
				new MyActiveBodyguardJSON().execute();

				lstMyActiveBodyguard.setVisibility(View.VISIBLE);
				lstMyPendingBodyguard.setVisibility(View.GONE);

				llActive.setBackgroundColor(getResources().getColor(R.color.header_green));
				llPending.setBackgroundColor(getResources().getColor(R.color.header_blue));
			}
		});

		llPending.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				lstMyActiveBodyguard.setVisibility(View.GONE);
				lstMyPendingBodyguard.setVisibility(View.VISIBLE);

				llActive.setBackgroundColor(getResources().getColor(R.color.header_blue));
				llPending.setBackgroundColor(getResources().getColor(R.color.header_green));
			}
		});

		new MyActiveBodyguardJSON().execute();

	}

	class MyActiveBodyguardJSON extends AsyncTask<Void, Void, Void> {

		String status = "null";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(MyBodyguardActivity.this);
			pDialog.setMessage("Please Wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler serviceHandler = new ServiceHandler();

			try {
				String URL = Constant.MyBodyguardListJSON + Constant.UserId;
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
							mybodyguarditem.setTrackId(obj.getString("num"));
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
			if (pDialog.isShowing()) {
				pDialog.dismiss();
			}

			if (status.equals("1")) {
				adapter = new MyBodyguardAdapter(MyBodyguardActivity.this, mybodyguardAL);
				lstMyActiveBodyguard.setAdapter(adapter);
			} else if (status.equals("0")) {
				Toast.makeText(MyBodyguardActivity.this, "No active bodyguard...", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MyBodyguardActivity.this, "Server Error...", Toast.LENGTH_SHORT).show();
			}

			new MyPendingBodyguardJSON().execute();

		}
	}

	class MyPendingBodyguardJSON extends AsyncTask<Void, Void, Void> {

		String status = "null";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(MyBodyguardActivity.this);
			pDialog.setMessage("Please Wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler serviceHandler = new ServiceHandler();

			try {
				String URL = Constant.MyPendingBodyguardJSON + Constant.UserId;
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

							myBodyguardApproveRejectItem = new MyBodyguardApproveRejectItem();

							myBodyguardApproveRejectItem.setReqId(obj.getString("num"));
							myBodyguardApproveRejectItem.setReqBy(obj.getString("requestedBy"));
							myBodyguardApproveRejectItem.setName(obj.getString("fullName"));
							myBodyguardApproveRejectItem.setNumber(obj.getString("phoneNumbers"));

							myPendingbodyguardAL.add(myBodyguardApproveRejectItem);
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
				adapterBAR = new MyBodyguardApproveRejectAdapter(MyBodyguardActivity.this, myPendingbodyguardAL);
				lstMyPendingBodyguard.setAdapter(adapterBAR);
			} else if (status.equals("0")) {
				Toast.makeText(MyBodyguardActivity.this, "No pending bodyguard...", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MyBodyguardActivity.this, "Server Error...", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
