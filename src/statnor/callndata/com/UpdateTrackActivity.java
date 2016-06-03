package statnor.callndata.com;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.callndata.Others.Constant;
import com.callndata.Others.MVCLog;
import com.callndata.Others.ServiceHandler;
import com.callndata.item.TrackDetailsItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import statnor.callndata.com.R;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class UpdateTrackActivity extends FragmentActivity {

	String trackid;
	GoogleMap googleMap;
	ProgressDialog pDialog;
	TrackDetailsItem trackdetailsitem;
	ArrayList<TrackDetailsItem> TrackDetailsAL = new ArrayList<TrackDetailsItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_update);
		setTitle("Location Tracking");

		trackid = getIntent().getExtras().getString("trackid");
		MVCLog.doLog("UPDATE ACTIVITY", "track id and user id is- "+trackid+"-userid-"+Constant.UserId);
		initilizeMap();
		new GetLocationPointsJSON().execute();

	}

	private void initilizeMap() {
		if (googleMap == null) {
			// googleMap = ((MapFragment)
			// this.getFragmentManager().findFragmentById(R.id.map)).getMap();
			googleMap = ((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			// new GetLocationPointsJSON().execute();
		}
	}

	class GetLocationPointsJSON extends AsyncTask<Void, Void, Void> {

		String status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(UpdateTrackActivity.this);
			pDialog.setMessage("Updating Locations...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			ServiceHandler servicehandler = new ServiceHandler();
			TrackDetailsAL.clear();

			try {
				String URL = Constant.ViewRouteDetailJSON + Constant.UserId + "&trackID=" + trackid;
				//String URL = Constant.ViewRouteDetailJSON + "43" + "&trackID=98";
				URL = URL.replace(" ", "%20");
				String Data = servicehandler.makeServiceCall(URL, ServiceHandler.GET);
				
				JSONArray arr = new JSONArray(Data);
				JSONObject obj = new JSONObject();

				for (int i = 0; i < arr.length(); i++) {
					if (i == 0) {
						obj = arr.getJSONObject(i);
						status = obj.getString("status");
					} else {
						if (status.equals("1")) {
							obj = arr.getJSONObject(i);
							trackdetailsitem = new TrackDetailsItem();
							trackdetailsitem.setCurrentLocation(obj.getString("currentLocationName"));
							trackdetailsitem.setLatitude(obj.getString("currentLatitude"));
							trackdetailsitem.setLongitude(obj.getString("currentLongitude"));
							trackdetailsitem.setInsertDate(obj.getString("insertDate"));
							TrackDetailsAL.add(trackdetailsitem);
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

			try {
				if (status.equals("1")) {

					int size = TrackDetailsAL.size() - 1;
					for ( int i = 0 ; i < TrackDetailsAL.size() ; i++ ) {
						double latitude = Double.parseDouble(TrackDetailsAL.get(i).getLatitude());
						double longitude = Double.parseDouble(TrackDetailsAL.get(i).getLongitude());
						
						MVCLog.doLog("UpdateTrackActivity", "lat and lng is- "+latitude+" "+longitude);
	
						LatLng point = new LatLng(latitude, longitude);
	
						CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
								.zoom(15).build();
												
						googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	
						MarkerOptions options = new MarkerOptions();
						options.position(point);
						options.title(Constant.checkInPlace);
						options.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_point));
						
						if( i == TrackDetailsAL.size()-1 ) {
							options.icon(BitmapDescriptorFactory.fromResource(R.drawable.chekin));
						}
	
						googleMap.addMarker(options);
					
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void drawMarker(LatLng point) {
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting latitude and longitude for the marker
		markerOptions.position(point);

		// Adding marker on the Google Map
		googleMap.addMarker(markerOptions);
	}
}
