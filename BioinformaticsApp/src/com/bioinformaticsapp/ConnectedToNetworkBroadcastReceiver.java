package com.bioinformaticsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * A class to listen out for when the user establishes a network connection
 * @author Hemal N Varambhia
 *
 */
public class ConnectedToNetworkBroadcastReceiver extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String receivedAction = intent.getAction();
		
		//If there was a change in the network connectivity...
		if(receivedAction.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			
			//...check whether there is a network connection, WiFi or mobile...
			boolean haveNetworkConnection = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			
			if(haveNetworkConnection){
				//Get the primary key ids for the RUNNING jobs
				Intent poller = new Intent(context, PollQueryService.class);
				context.startService(poller);
				
				Intent pendingJobsSubmitter = new Intent(context, SubmitQueryService.class);
				context.startService(pendingJobsSubmitter);
				
			}
			
		}
		
	}

}
