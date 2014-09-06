package com.bioinformaticsapp.blastservices;

import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.bioinformaticsapp.QueryStatusRefreshReceiver;
import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;


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
	private static final int FIVE_MINUTES = 5*60*1000;
	private AlarmManager alarmManager;
	
	public PollQueryService(String name) {
		super(name);
	}
	
	public PollQueryService() {
		this("PollQueryService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(QueryStatusRefreshReceiver.REFRESH_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		long timeToRefresh = SystemClock.elapsedRealtime() + FIVE_MINUTES; //refreshing every 5 minutes
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, timeToRefresh, FIVE_MINUTES, pendingIntent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(this);
		int[] vendors = new int[]{ BLASTVendor.EMBL_EBI, BLASTVendor.NCBI };
		
		for(int vendor: vendors){
			List<BLASTQuery> sentQueries = labBook.submittedBLASTQueriesForVendor(vendor);
			BLASTSearchEngine searchEngine = BLASTSearchEngineFactory.getBLASTSearchEngineFor(vendor);
			BLASTQueryPoller poller = new BLASTQueryPoller(this, searchEngine);
			BLASTQuery[] sent = new BLASTQuery[sentQueries.size()];
			poller.execute(sentQueries.toArray(sent));
		}
	}
}
