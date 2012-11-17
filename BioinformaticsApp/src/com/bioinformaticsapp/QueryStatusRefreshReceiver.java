package com.bioinformaticsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class QueryStatusRefreshReceiver extends BroadcastReceiver {

	
	public static final String REFRESH_ACTION = "com.bioinformaticsapp.ACTION_QUERY_STATUS_REFRESH";
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		Toast pollingMessage = Toast.makeText(context, "Polling queries", Toast.LENGTH_SHORT);
		pollingMessage.show();
		Intent refreshIntent = new Intent(context, PollQueryService.class);
		context.startService(refreshIntent);
		
	}

}
