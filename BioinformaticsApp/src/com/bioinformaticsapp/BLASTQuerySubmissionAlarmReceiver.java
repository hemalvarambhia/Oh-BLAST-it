package com.bioinformaticsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BLASTQuerySubmissionAlarmReceiver extends BroadcastReceiver {

	public static final String SUBMISSION_ACTION = "com.bioinformaticsapp.SUBMIT_PENDING_QUERIES_ACTION";
	private static final String TAG = "BLASTQuerySubmissionAlarmReceiver";
	
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Received action "+SUBMISSION_ACTION);
		//arg0.startService(new Intent(arg0, SubmitQueryService.class));
	}

}
