package com.bioinformaticsapp;

import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.bioinformaticsapp.blastservices.BLASTQueryPoller;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.EMBLEBIBLASTService;
import com.bioinformaticsapp.blastservices.NCBIBLASTService;
import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;


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
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(this);
		int[] vendors = new int[]{ BLASTVendor.EMBL_EBI, BLASTVendor.NCBI };
		BLASTSearchEngine ncbiBLASTService = new NCBIBLASTService();
		BLASTSearchEngine emblBLASTService = new EMBLEBIBLASTService();
		
		for(int vendor: vendors){
			List<BLASTQuery> sentQueries = labBook.findPendingBLASTQueriesFor(vendor);
			BLASTQueryPoller poller = new BLASTQueryPoller(this, ncbiBLASTService, emblBLASTService);
			BLASTQuery[] sent = new BLASTQuery[sentQueries.size()];
			poller.execute(sentQueries.toArray(sent));
		}
	}
}
