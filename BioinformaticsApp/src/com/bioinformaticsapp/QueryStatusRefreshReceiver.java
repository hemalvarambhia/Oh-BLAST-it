package com.bioinformaticsapp;

import com.bioinformaticsapp.blastservices.PollQueryService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class QueryStatusRefreshReceiver extends BroadcastReceiver {

	
	public static final String REFRESH_ACTION = "com.bioinformaticsapp.ACTION_QUERY_STATUS_REFRESH";
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent refreshIntent = new Intent(context, PollQueryService.class);
		context.startService(refreshIntent);
		
	}

}
