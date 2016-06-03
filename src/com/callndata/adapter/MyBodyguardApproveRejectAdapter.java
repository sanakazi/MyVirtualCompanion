package com.callndata.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.ServiceHandler;
import com.callndata.item.MyBodyguardApproveRejectItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import statnor.callndata.com.R;

public class MyBodyguardApproveRejectAdapter extends BaseAdapter {

	String DelBG;
	Context context;
	ProgressDialog pDialog;
	ArrayList<MyBodyguardApproveRejectItem> mybodyguardAL = new ArrayList<MyBodyguardApproveRejectItem>();
	
	int ALPosition;

	public MyBodyguardApproveRejectAdapter() {

	}

	public MyBodyguardApproveRejectAdapter(Context context, ArrayList<MyBodyguardApproveRejectItem> mybodyguardAL) {
		this.context = context;
		this.mybodyguardAL = mybodyguardAL;
	}

	@Override
	public int getCount() {
		return mybodyguardAL.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = View.inflate(context, R.layout.item_app_rej_bodyguard_req, null);
		} else {
			view = convertView;
		}

		Holder holder = new Holder();

		holder.txtName = (TextView) view.findViewById(R.id.txtName);
		holder.txtNumber = (TextView) view.findViewById(R.id.txtNumber);
		holder.btnApprove = (Button) view.findViewById(R.id.btnApprove);
		holder.btnReject = (Button) view.findViewById(R.id.btnReject);

		holder.txtName.setText(mybodyguardAL.get(position).getName());
		holder.txtNumber.setText(mybodyguardAL.get(position).getNumber());

		holder.btnApprove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		holder.btnReject.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String reqId, reqBy, reqAction;
				reqId = mybodyguardAL.get(position).getReqId();
				reqBy = mybodyguardAL.get(position).getReqBy();
				reqAction = "Rejected";
				ALPosition = position;
				new MyBodyguardAppoveRejectJSON().execute(reqId, reqBy, reqAction);
			}
		});
		holder.btnApprove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String reqId, reqBy, reqAction;
				reqId = mybodyguardAL.get(position).getReqId();
				reqBy = mybodyguardAL.get(position).getReqBy();
				reqAction = "Approved";
				ALPosition = position;
				new MyBodyguardAppoveRejectJSON().execute(reqId, reqBy, reqAction);
			}
		});

		return view;
	}

	class Holder {
		Button btnApprove, btnReject;
		TextView txtName, txtNumber;
	}

	class MyBodyguardAppoveRejectJSON extends AsyncTask<String, Void, Void> {

		String status;
		String reqId, reqBy, reqAction;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Please Wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			reqId = params[0];
			reqBy = params[1];
			reqAction = params[2];

			ServiceHandler serviceHandler = new ServiceHandler();

			try {
				String URL = Constant.BodyguardApproveRejectJSON + Constant.UserId + "&requestID=" + reqId
						+ "&requestedBy=" + reqBy + "&updateStatus=" + reqAction;
				URL = URL.replace(" ", "%20");
				String Data = serviceHandler.makeServiceCall(URL, ServiceHandler.GET);

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

			if (status.equals("1")) {
				if (reqAction.equals("Rejected")) {
					Toast.makeText(context, "Bodyguard rejected...", Toast.LENGTH_SHORT).show();
				} else if (reqAction.equals("Approved")) {
					Toast.makeText(context, "Bodyguard approved...", Toast.LENGTH_SHORT).show();
				}
				mybodyguardAL.remove(ALPosition);
				notifyDataSetChanged();
			} else {
				Toast.makeText(context, "Server Error...", Toast.LENGTH_SHORT).show();
			}

		}
	}
}
