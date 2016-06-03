package statnor.callndata.com;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.ServiceHandler;

import statnor.callndata.com.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewBodyGuardActivity extends Activity {

	String strEmail;
	EditText etEmail;
	ProgressDialog pDialog;
	Button btnAdd, btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_bodyguard);

		etEmail = (EditText) findViewById(R.id.etEmail);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		btnAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				strEmail = etEmail.getText().toString();

				if (strEmail.equals(null) || strEmail.equals("") || strEmail.isEmpty()) {
					Toast.makeText(AddNewBodyGuardActivity.this, "Enter E-Mail Id", Toast.LENGTH_SHORT).show();
				} else if (!isValidEmail(strEmail)) {
					Toast.makeText(AddNewBodyGuardActivity.this, "Enter valid E-Mail Id", Toast.LENGTH_SHORT).show();
				} else {
					new MyBodyguardAddJSON().execute();
				}
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	class MyBodyguardAddJSON extends AsyncTask<Void, Void, Void> {

		String status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(AddNewBodyGuardActivity.this);
			pDialog.setMessage("Please Wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler serviceHandler = new ServiceHandler();

			try {
				String URL = Constant.NewBodyguardAdd + Constant.UserId + "&connectEmail=" + strEmail;
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
				Toast.makeText(AddNewBodyGuardActivity.this, "Bodyguard Added...", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(AddNewBodyGuardActivity.this, "Server Error...", Toast.LENGTH_SHORT).show();
			}

		}
	}
}
