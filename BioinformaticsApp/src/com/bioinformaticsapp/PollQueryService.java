package com.bioinformaticsapp;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.web.BLASTQueryPoller;
import com.bioinformaticsapp.web.EMBLEBIBLASTService;


/**
 * This class polls the BLAST query the user submitted to the service
 * The reason it is in its own class is that we can continue polling
 * from a broadcast receiver which listens out for a WI-FI connection.
 * The context is that the WI-FI connection was on when the user submitted
 * the query but switched it off afterwards and switches it on at a later time
 * 
 * @author Hemal N Varambhia
 *
 */
public class PollQueryService extends IntentService {

	private static final String TAG = "PollQueryService";
	
	private AlarmManager alarmManager;
	
	public PollQueryService(String name) {
		super(name);
	}
	
	public PollQueryService() {
		this("PollQueryService");
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(QueryStatusRefreshReceiver.REFRESH_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		long timeToRefresh = SystemClock.elapsedRealtime() + 5*60*1000; //refreshing every 5 minutes
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, timeToRefresh, 5*60*1000, pendingIntent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		BLASTQueryController queryController = new BLASTQueryController(this);
		OptionalParameterController parametersController = new OptionalParameterController(this);
		List<BLASTQuery> sentQueries = queryController.getSubmittedAndRunningQueries();
		BLASTQuery[] sent = new BLASTQuery[sentQueries.size()];
		
		for(int i = 0; i < sentQueries.size(); i++){
			BLASTQuery sentQuery = sentQueries.get(i);
			List<SearchParameter> parameters = parametersController.getParametersForQuery(sentQuery.getPrimaryKey());
			sentQuery.updateAllParameters(parameters);
			sent[i] = sentQuery;
		}
		
		parametersController.close();
		queryController.close();
		
		BLASTQueryPoller poller = new BLASTQueryPoller(this);
		poller.execute(sent);
		
	}
	
	
	
	

}
