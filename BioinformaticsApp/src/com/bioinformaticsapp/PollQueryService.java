package com.bioinformaticsapp;

import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.web.BLASTQueryPoller;


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
		SearchParameterController parametersController = new SearchParameterController(this);
		List<BLASTQuery> sentQueries = queryController.getSubmittedBLASTQueries();
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
