package statnor.callndata.com;

import java.util.ArrayList;
import statnor.callndata.com.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BodyGuard extends Activity {
	
	private ListView bodyguardList;
	private ArrayList<String> myBodyGuards;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bodyguard);
		
		myBodyGuards = (ArrayList<String>) getIntent().getSerializableExtra("bodyGuardList");
		
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
	
		bodyguardList = (ListView) findViewById(R.id.bodyguard_list);
		
		ArrayAdapter adapter = new ArrayAdapter(BodyGuard.this, android.R.layout.simple_list_item_1, myBodyGuards);
		bodyguardList.setAdapter(adapter);
		
		bodyguardList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BodyGuard.this, UpdateTrackActivity.class);
				intent.putExtra("trackid", HomeActivity.mybodyguardAL.get(position).getTrackId());
				startActivity(intent);
				
			}
		});
		
	}

}
