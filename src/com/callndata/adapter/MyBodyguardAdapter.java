package com.callndata.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import statnor.callndata.com.MyBodyguardActivity;

import com.callndata.Others.Constant;
import com.callndata.Others.ServiceHandler;
import com.callndata.item.MyBodyguardItem;

import statnor.callndata.com.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyBodyguardAdapter extends BaseAdapter {

	String DelBG;
	int ALPosition;
	Context context;
	ProgressDialog pDialog;
	ArrayList<MyBodyguardItem> mybodyguardAL = new ArrayList<MyBodyguardItem>();

	public MyBodyguardAdapter(Context context, ArrayList<MyBodyguardItem> mybodyguardAL) {
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
			view = View.inflate(context, R.layout.adapter_my_bodyguard, null);
		} else {
			view = convertView;
		}

		Holder holder = new Holder();

		holder.txtName = (TextView) view.findViewById(R.id.txtName);
		holder.txtNumber = (TextView) view.findViewById(R.id.txtNumber);
		holder.imgDelete = (ImageView) view.findViewById(R.id.imgDelete);

		holder.txtName.setText(mybodyguardAL.get(position).getName());
		holder.txtNumber.setText(mybodyguardAL.get(position).getNumber());

		holder.imgDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ALPosition = position;
				DelBG = mybodyguardAL.get(position).getTrackId();
				new MyBodyguardDeleteJSON().execute();
			}
		});

		return view;
	}

	class Holder {
		ImageView imgDelete;
		TextView txtName, txtNumber;
	}

	class MyBodyguardDeleteJSON extends AsyncTask<Void, Void, Void> {

		String status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Please Wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler serviceHandler = new ServiceHandler();

			try {
				String URL = Constant.MyBodyguardDelete + Constant.UserId + "&BodyGaurdID=" + DelBG;
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
				Toast.makeText(context, "Bodyguard Deleted...", Toast.LENGTH_SHORT).show();
				mybodyguardAL.remove(ALPosition);
				notifyDataSetChanged();
			} else {
				Toast.makeText(context, "Server Error...", Toast.LENGTH_SHORT).show();
			}

		}
	}
}
