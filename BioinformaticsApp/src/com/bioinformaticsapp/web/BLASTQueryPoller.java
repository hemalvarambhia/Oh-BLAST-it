package com.bioinformaticsapp.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.bioinformaticsapp.BLASTQueriesFinishedReceiver;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.helpers.StatusTranslator;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;

public class BLASTQueryPoller extends AsyncTask<BLASTQuery, Void, BLASTQueryPoller.Report> {

	private Context context;
	
	public static final int JOB_FINISHED_NOTI_ID = 2;

	private static final String TAG = "BLASTQueryPoller";
	
	private NCBIBLASTService ncbiService;
	
	private EMBLEBIBLASTService emblService;
	
	private StatusTranslator translator;
	
	public BLASTQueryPoller(Context context){
		this.context = context;
		ncbiService = new NCBIBLASTService();
		emblService = new EMBLEBIBLASTService();
		translator = new StatusTranslator();
	}
	
	@Override
	protected Report doInBackground(BLASTQuery... queries) {
		Report report = new Report();
		if(connectedToWeb()){
			for(int i = 0; i < queries.length; i++){
				
				BLASTSequenceQueryingService service = getServiceFor(queries[i].getVendorID());
				
				SearchStatus current = service.pollQuery(queries[i].getJobIdentifier());
				
				queries[i].setStatus(translator.translate(current));
				
				save(queries[i]);

				report.addOutcome(queries[i].getPrimaryKey(), translator.translate(current));
			}
		}
		close();
		
		return report;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Report result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result.anyQueriesFinished()){
			Intent queriesFinishedAnnouncement = new Intent(BLASTQueriesFinishedReceiver.QUERIES_FINISHED_ACTION);
			context.sendBroadcast(queriesFinishedAnnouncement);
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
	
	private BLASTSequenceQueryingService getServiceFor(int blastVendor){
		
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
		ncbiService.close();
		emblService.close();
		
	}
	
	public class Report {

		private Map<Long, BLASTQuery.Status> report;
		
		private Report(){
			report = new HashMap<Long, BLASTQuery.Status>();
		}
		
		public void addOutcome(Long queryID, BLASTQuery.Status status){
			report.put(queryID, status);
		}
		
		public BLASTQuery.Status getOutcomeFor(BLASTQuery query){
			return report.get(query.getPrimaryKey());
		}
		
		public boolean anyQueriesFinished(){
			
			List<Long> finished = new ArrayList<Long>();
			
			for(Long queryID: report.keySet()){
				if(report.get(queryID).equals(BLASTQuery.Status.FINISHED)){
					finished.add(queryID);
				}
			}
			
			return finished.size() > 0;
		}
		
		public String toString(){
			return report.toString();
		}
	}
	
	
}
