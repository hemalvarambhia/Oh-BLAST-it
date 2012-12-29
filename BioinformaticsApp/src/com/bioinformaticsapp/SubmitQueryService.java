package com.bioinformaticsapp;

import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Intent;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.models.SearchParameter;
import com.bioinformaticsapp.web.BLASTQuerySender;

public class SubmitQueryService extends IntentService {

	private static final String TAG = "SubmitQueryService";
	
	
	public SubmitQueryService(String name) {
		super(name);
	}
	
	public SubmitQueryService(){
		this("SubmitQueryService");
	}	
	
	public void onCreate(){
		super.onCreate();
		
	}
	
	@Override
	protected void onHandleIntent(Intent launchingIntent) {
		BLASTQueryController queryController = new BLASTQueryController(this);
		SearchParameterController parametersController = new SearchParameterController(this);
		List<BLASTQuery> pendingQueries = queryController.findBLASTQueriesByStatus(Status.PENDING);
		BLASTQuery[] pending = new BLASTQuery[pendingQueries.size()];
		
		for(int i = 0; i < pendingQueries.size(); i++){
			BLASTQuery pendingQuery = pendingQueries.get(i);
			List<SearchParameter> parameters = parametersController.getParametersForQuery(pendingQuery.getPrimaryKey());
			pendingQuery.updateAllParameters(parameters);
			pending[i] = pendingQuery;
		}
		
		parametersController.close();
		queryController.close();
		
		BLASTQuerySender sender = new BLASTQuerySender(this);
		sender.execute(pending);
		sendBroadcast(new Intent(QueryStatusRefreshReceiver.REFRESH_ACTION));
		
	}
	
	
}
