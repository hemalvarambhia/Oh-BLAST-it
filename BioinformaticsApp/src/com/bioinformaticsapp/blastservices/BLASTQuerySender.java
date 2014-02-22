package com.bioinformaticsapp.blastservices;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;

public class BLASTQuerySender extends
		AsyncTask<BLASTQuery, Void, Integer> {

	public BLASTQuerySender(Context context){
		
		this.context = context;
		ncbiService = new NCBIBLASTService();
		emblService = new EMBLEBIBLASTService();
		numberToSend = 0;
	}
	@Override
	protected Integer doInBackground(BLASTQuery...pendingQueries) {
		numberToSend = pendingQueries.length;
		int numberSent = 0;
		
		if(connectedToWeb()){
			for(int i = 0; i < pendingQueries.length; i++){
				BLASTSearchEngine service = getServiceFor(pendingQueries[i].getVendorID());
				try {
					
					String jobIdentifier = service.submit(pendingQueries[i]);
					if(jobIdentifier != null){
						pendingQueries[i].setJobIdentifier(jobIdentifier);
						pendingQueries[i].setStatus(BLASTQuery.Status.SUBMITTED);
						numberSent++;
					}
				
				} catch(IllegalBLASTQueryException e){
					
					Log.i(TAG, "Could not assign query with ID "+pendingQueries[0].getPrimaryKey()+" a job identifier");
					pendingQueries[i].setStatus(BLASTQuery.Status.DRAFT);
				} finally {
					save(pendingQueries[i]);				
				}
			}
			close();
			
		}
		Integer numberOfQueriesSent = new Integer(numberSent);	
		
		return numberOfQueriesSent;
	}
	
	@Override
	protected void onPostExecute(Integer numberOfQueriesSent) {
		
		super.onPostExecute(numberOfQueriesSent);
		
		if(numberToSend > 0){
			Toast message = null;
			if(numberOfQueriesSent.intValue() == 0){
				
				if(connectedToWeb()){
					message = Toast.makeText(context, "Queries could not be sent. Please check that they're valid", Toast.LENGTH_SHORT);
				}else{
					message = Toast.makeText(context, "Queries will be sent when a web connection is available", Toast.LENGTH_SHORT);
				}
				
			}else if(numberOfQueriesSent.intValue() == numberToSend){
				//If all were sent:
				message = Toast.makeText(context, "Queries sent", Toast.LENGTH_SHORT);
			}else{
				//If some queries were sent:
				message = Toast.makeText(context, "Some queries could not be sent. Please check that they're valid", Toast.LENGTH_SHORT);
			}
			
			if(message != null){
				message.show();
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
	
	private void save(BLASTQuery query){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(context);
		labBook.save(query);
	}
	
	private void close(){
		
		emblService.close();
		ncbiService.close();
	
	}

	private static final String TAG = "BLASTQuerySender";
	private NCBIBLASTService ncbiService;
	private EMBLEBIBLASTService emblService;
	protected Context context;
	private int numberToSend;
	
}
