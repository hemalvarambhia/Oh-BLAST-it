package com.bioinformaticsapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bioinformaticsapp.blastservices.BLASTQueryPoller;

public class BLASTQueriesFinishedReceiver extends BroadcastReceiver {

	public static final String QUERIES_FINISHED_ACTION = "com.bioinformaticsapp.QUERIES_FINISHED_ACTION";
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		announceBLASTQueriesFinished(context);
	}
	
	private void announceBLASTQueriesFinished(Context context){
		NotificationManager mgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder builder = new Notification.Builder(context);
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(true);
		builder.setSmallIcon(com.bioinformaticsapp.R.drawable.ic_dna);
		builder.setTicker("BLAST queries finished");
		builder.setContentText("Click to view the results");
		builder.setContentTitle("BLAST Queries Finished");
		Intent finishedQueriesActivity = new Intent(context, ListFinishedBLASTQueries.class);
		PendingIntent finishedQueries = PendingIntent.getActivity(context, 0, finishedQueriesActivity, Intent.FLAG_ACTIVITY_CLEAR_TOP);
		builder.setContentIntent(finishedQueries);
		Notification notification = builder.getNotification();
		mgr.notify(BLASTQueryPoller.JOB_FINISHED_NOTI_ID, notification);
	}
}
