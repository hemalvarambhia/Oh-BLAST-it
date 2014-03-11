package com.bioinformaticsapp.blastservices;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.models.BLASTQuery;

public class BLASTQuerySender extends
		AsyncTask<BLASTQuery, Void, Integer> {

	public BLASTQuerySender(Context context, BLASTSearchEngine service) {
		blastSearchEngineService = service;
		numberToSend = 0;
	}
	
	@Override
	protected Integer doInBackground(BLASTQuery...pendingQueries) {
		numberToSend = pendingQueries.length;
		int numberSent = 0;
		if(connectedToWeb()){
			for(int i = 0; i < pendingQueries.length; i++){
				BLASTQuery pending = pendingQueries[i];
				if(!pending.isValid()){
					pending.setStatus(BLASTQuery.Status.DRAFT);
				}else{
					String jobIdentifier = blastSearchEngineService.submit(pending);
					if(jobIdentifier != null){
						pending.setJobIdentifier(jobIdentifier);
						pending.setStatus(BLASTQuery.Status.SUBMITTED);
						numberSent++;
					}
				}
				save(pending);
			}
			blastSearchEngineService.close();
		}
		Integer numberOfQueriesSent = new Integer(numberSent);	
		return numberOfQueriesSent;
	}
	
	@Override
	protected void onPostExecute(Integer numberOfQueriesSent) {
		
		super.onPostExecute(numberOfQueriesSent);
		if(numberToSend == 0){
			return;
		}
		
		if(numberOfQueriesSent.intValue() == 0){
			if(connectedToWeb()){
				displayToastMessage("Queries could not be sent. Please check that they're valid");
			}else{
				displayToastMessage("Queries will be sent when a web connection is available");
			}
			return;
		}
		
		if(numberOfQueriesSent.intValue() == numberToSend){
			displayToastMessage("Queries sent");
		}else{
			displayToastMessage("Some queries could not be sent. Please check that they're valid");
		}
	}
	
	private void displayToastMessage(String content){
		Toast message = Toast.makeText(context, content, Toast.LENGTH_SHORT);
		message.show();
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

	
	private void save(BLASTQuery query){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(context);
		labBook.save(query);
	}
	
	private BLASTSearchEngine blastSearchEngineService;
	protected Context context;
	private int numberToSend;
}
