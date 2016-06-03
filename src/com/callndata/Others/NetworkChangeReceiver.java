package com.callndata.Others;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(final Context context, final Intent intent) {       
        
        Intent i = new Intent(context, SyncService.class);
		
        int networkStatus = NetworkUtil.isNetworkAvail(context);
		if(networkStatus==1)
		{
			context.startService(i);
		}
		else
		{
			context.stopService(i);
		}
    }
}