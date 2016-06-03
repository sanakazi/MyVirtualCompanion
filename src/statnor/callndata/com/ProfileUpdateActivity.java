package statnor.callndata.com;

import statnor.callndata.com.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class ProfileUpdateActivity extends Activity {

	private Button btnUpdate, btnCancel;
	
	private SharedPreferences accSharedPref;
	
	private EditText edtName, edtStreet, edtArea, edtCity, edtPostCode, edtPhone;
	
	public static final String NAME = "Full Name";
	public static final String STREET = "Street";
	public static final String AREA = "Area";
	public static final String CITY = "Town";
	public static final String POST = "Post Code";
	public static final String PHONE = "Phone Number";
	
	public static final int GETDATA = 0;
	public static final int SAVEDATA = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_update);
		setTitle("Profile Update");

		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		init();

		btnUpdate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(ProfileUpdateActivity.this, "Updated Succesfully", Toast.LENGTH_SHORT).show();
				initSharedPrefrences(SAVEDATA);
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		
		accSharedPref = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);
		edtName = (EditText) findViewById(R.id.etName);
		edtStreet = (EditText) findViewById(R.id.etStreet);
		edtArea = (EditText) findViewById(R.id.etAreaName);
		edtCity = (EditText) findViewById(R.id.etCity);
		edtPostCode = (EditText) findViewById(R.id.etPostCode);
		edtPhone = (EditText) findViewById(R.id.etPhoneNum);
		
		if( accSharedPref.contains(NAME) ){
			initSharedPrefrences(GETDATA);
		}
		
	}
	
	/*init shared prefrence and set the value if available*/
	private void initSharedPrefrences(int mode) {
		
		SharedPreferences.Editor editor = accSharedPref.edit();
		
		switch(mode) {
		
		case GETDATA :
			
			edtName.setText(accSharedPref.getString(NAME, null));
			edtStreet.setText(accSharedPref.getString(STREET, null));
			edtArea.setText(accSharedPref.getString(AREA, null));
			edtCity.setText(accSharedPref.getString(CITY, null));
			edtPostCode.setText(accSharedPref.getString(POST, null));
			edtPhone.setText(accSharedPref.getString(PHONE, null));
			
			break;
			
		case SAVEDATA :
			
			editor.clear();
			editor.putString(NAME, edtName.getText().toString());
			editor.putString(STREET, edtStreet.getText().toString());
			editor.putString(AREA, edtArea.getText().toString());
			editor.putString(CITY, edtCity.getText().toString());
			editor.putString(POST, edtPostCode.getText().toString());
			editor.putString(PHONE, edtPhone.getText().toString());
			editor.commit();
			
			break;
			
		default:
            throw new IllegalArgumentException("Invalid");
		}
		
		
		
	}
}
