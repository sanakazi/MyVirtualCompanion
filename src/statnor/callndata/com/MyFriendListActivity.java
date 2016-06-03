package statnor.callndata.com;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.ServiceHandler;
import com.callndata.adapter.MyFriendsListAdapter;
import com.callndata.item.MyFriendListItem;

import statnor.callndata.com.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MyFriendListActivity extends Activity {

	ListView lstMyFrndList;
	ProgressDialog pDialog;
	MyFriendListItem MyFriendItem;
	MyFriendsListAdapter MyFriendListAdapter;
	ArrayList<MyFriendListItem> MyFriendListAL = new ArrayList<MyFriendListItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myfriendlist);

		setTitle("My Friend List");

		lstMyFrndList = (ListView) findViewById(R.id.lstMyFrndList);
		new MyFriendListJSON().execute();

		lstMyFrndList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MyFriendListActivity.this, TrackConfirmActivity.class);
				intent.putExtra("Friend_Num", MyFriendListAL.get(position).getNum());
				intent.putExtra("Friend_Name", MyFriendListAL.get(position).getName());
				startActivity(intent);
				finish();
			}
		});

	}

	class MyFriendListJSON extends AsyncTask<Void, Void, Void> {

		String status = "null";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyFriendListActivity.this);
			pDialog.setMessage("Please Wait");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler servicehandler = new ServiceHandler();

			try {
				String Data = servicehandler.makeServiceCall(Constant.MyFriendListJSON + Constant.UserId,
						ServiceHandler.GET);

				JSONArray arr = new JSONArray(Data);
				JSONObject obj = new JSONObject();

				for (int i = 0; i < arr.length(); i++) {
					if (i == 0) {
						obj = arr.getJSONObject(i);
						status = obj.getString("status");
					} else {
						if (status.equals("1")) {
							obj = arr.getJSONObject(i);
							String num, name, number;
							num = obj.getString("num");
							name = obj.getString("fullName");
							number = obj.getString("phoneNumbers");

							MyFriendItem = new MyFriendListItem();
							MyFriendItem.setNum(num);
							MyFriendItem.setName(name);
							MyFriendItem.setNumber(number);

							MyFriendListAL.add(MyFriendItem);
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
				MyFriendListAdapter = new MyFriendsListAdapter(MyFriendListActivity.this, MyFriendListAL);
				lstMyFrndList.setAdapter(MyFriendListAdapter);
			} else if (status.equals("0")) {
				Toast.makeText(MyFriendListActivity.this, "Friendlist request rejected...", Toast.LENGTH_SHORT).show();
			} else if (status.equals("null")) {
				Toast.makeText(MyFriendListActivity.this, "Oops! Try again later...", Toast.LENGTH_SHORT).show();
			}
		}

	}
}
