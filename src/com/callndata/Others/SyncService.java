package com.callndata.Others;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SyncService extends Service {

	public SyncService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onStart(Intent intent, int startId) {
		// Perform your long running operations here.
		//Intent i = new Intent(MyService.this, EntryActivity.class);
		// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(i);
		
		sendSMS();

	}

	@Override
	public void onDestroy() {
		stopSelf();
	}

	public void sendSMS() {

		SharedPreferences settings = getApplicationContext().getSharedPreferences("SAVE_MSG", 0);
		String cellNo = settings.getString("cellno", "");
		String msg = settings.getString("msg", "");
		
		String smsSent = "SMS_SENT";
		String smsDelivered = "SMS_DELIVERED";

		PendingIntent sendPi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(smsSent), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,new Intent(smsDelivered), 0);

		// Receiver for Sent SMS.
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(arg0, "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(arg0, "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(arg0, "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(arg0, "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(arg0, "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(smsSent));

		// Receiver for Delivered SMS.
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(arg0, "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(arg0, "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(smsDelivered));

		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(cellNo, null, msg, sendPi, deliveredPI);
	}
}