package statnor.callndata.com;

import com.callndata.Others.Constant;

import statnor.callndata.com.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	String LoginFlag;
	SharedPreferences pref;
	private static int SPLASH_TIME_OUT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		pref = getApplicationContext().getSharedPreferences("MyPref", 0);
		LoginFlag = pref.getString("LoginFlag", null);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				if (LoginFlag == null || LoginFlag.equals("0")) {
					Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
					startActivity(intent);
				} else {
					Constant.UserId = pref.getString("userid", null);
					Constant.CustId = pref.getString("custid", null);
					Constant.UName = pref.getString("username", null);
					Constant.Password = pref.getString("password", null);
					Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
					startActivity(intent);
				}
				finish();
			}
		}, SPLASH_TIME_OUT);

	}

}
