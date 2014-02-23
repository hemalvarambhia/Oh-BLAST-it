package com.bioinformaticsapp.blastservices;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.bioinformaticsapp.AppPreferences;
import com.bioinformaticsapp.BLASTQueriesFinishedReceiver;
import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.helpers.StatusTranslator;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;

public class BLASTQueryPoller extends AsyncTask<BLASTQuery, Void, Integer> {

	private Context context;
	
	public static final int JOB_FINISHED_NOTI_ID = 2;

	private BLASTSearchEngine ncbiService;
	private BLASTSearchEngine emblService;
	private StatusTranslator translator;
	
	public BLASTQueryPoller(Context context, BLASTSearchEngine ncbiBLASTService, 
			BLASTSearchEngine emblBLASTService){
		this.context = context;
		ncbiService = ncbiBLASTService;
		emblService = emblBLASTService;
		translator = new StatusTranslator();
	}
	
	@Override
	protected Integer doInBackground(BLASTQuery... queries) {
		int numberOfQueriesFinished = 0;
		if(connectedToWeb()){
			for(int i = 0; i < queries.length; i++){				
				BLASTSearchEngine service = getServiceFor(queries[i].getVendorID());
				SearchStatus current = service.pollQuery(queries[i].getJobIdentifier());
				queries[i].setStatus(translator.translate(current));
				if(hasFinished(queries[i])){
					numberOfQueriesFinished++;
				}
				save(queries[i]);
			}
		}
		close();
		
		return numberOfQueriesFinished;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Integer numberOfQueriesFinished) {
		// TODO Auto-generated method stub
		super.onPostExecute(numberOfQueriesFinished);
		if(numberOfQueriesFinished > 0){
			Intent queriesFinishedAnnouncement = new Intent(BLASTQueriesFinishedReceiver.QUERIES_FINISHED_ACTION);
			SharedPreferences preferences = context.getSharedPreferences(AppPreferences.OHBLASTIT_PREFERENCES_FILE, Context.MODE_PRIVATE);
			boolean notifyUser = preferences.getBoolean(AppPreferences.NOTIFICATIONS_SWITCH, false);
			if(notifyUser){
				context.sendBroadcast(queriesFinishedAnnouncement);
			}
		}
	}

	protected boolean connectedToWeb(){
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo == null){
			return false;
		}
		
		if(!activeNetworkInfo.isAvailable()){
			return false;
		}
		
		if(!activeNetworkInfo.isConnected()){
			return false;
		}
		
		return true;
	}
	
	private BLASTSearchEngine getServiceFor(int blastVendor){
		switch(blastVendor){
		case BLASTVendor.EMBL_EBI:
			return emblService;
		case BLASTVendor.NCBI:
			return ncbiService;
		default:
			return null;
		}
	}
	
	private boolean hasFinished(BLASTQuery query){
		return query.getStatus().equals(BLASTQuery.Status.FINISHED);
	}

	private void save(BLASTQuery query){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(context);
		labBook.save(query);
	}
	
	private void close(){
		ncbiService.close();
		emblService.close();	
	}	
}
