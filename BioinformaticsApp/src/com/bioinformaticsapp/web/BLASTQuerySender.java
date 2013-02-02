package com.bioinformaticsapp.web;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;

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
		Integer numberOfQueriesSent = null;
		
		if(connectedToWeb()){
			int numberSent = 0;
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
			numberOfQueriesSent = new Integer(numberSent);	
			close();
			
		}
		
		return numberOfQueriesSent;
	}
	
	@Override
	protected void onPostExecute(Integer numberOfQueriesSent) {
		
		super.onPostExecute(numberOfQueriesSent);
		
		if(numberOfQueriesSent == null){
			return;
		}
		if(numberToSend > 0){
			if(numberOfQueriesSent.intValue() == 0){
				Toast t = Toast.makeText(context, "Queries could not be sent. Please check that they're valid", Toast.LENGTH_SHORT);
				t.show();
			}else if(numberOfQueriesSent.intValue() == numberToSend){
				//If all were sent:
				Toast t = Toast.makeText(context, "Queries sent", Toast.LENGTH_SHORT);
				t.show();
			}else{
				//If some queries were sent:
				Toast t = Toast.makeText(context, "Some queries could not be sent. Please check that they're valid", Toast.LENGTH_SHORT);
				t.show();
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
		BLASTQueryController queryController = new BLASTQueryController(this.context);
		SearchParameterController parametersController = new SearchParameterController(this.context);
		
		List<SearchParameter> parameters = query.getAllParameters();
		
		queryController.update(query.getPrimaryKey(), query);
		
		for(SearchParameter parameter: parameters){
			parametersController.update(parameter.getPrimaryKey(), parameter);
		}

		queryController.close();
		parametersController.close();
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
