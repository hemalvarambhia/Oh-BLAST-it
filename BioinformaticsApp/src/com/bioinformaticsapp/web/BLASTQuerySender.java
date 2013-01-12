package com.bioinformaticsapp.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		AsyncTask<BLASTQuery, Void, BLASTQuerySender.Report> {
	

	private static final String TAG = "BLASTQuerySender";
	private NCBIBLASTService ncbiService;
	private EMBLEBIBLASTService emblService;
	protected Context context;
	
	public BLASTQuerySender(Context context){
		
		this.context = context;
		ncbiService = new NCBIBLASTService();
		emblService = new EMBLEBIBLASTService();
		
	}
	@Override
	protected Report doInBackground(BLASTQuery...pendingQueries) {
		
		Report sendReport = new Report();
		
		if(connectedToWeb()){
			
			for(int i = 0; i < pendingQueries.length; i++){
				BLASTSequenceQueryingService service = getServiceFor(pendingQueries[i].getVendorID());
				try {
					
					String jobIdentifier = service.submit(pendingQueries[i]);
					if(jobIdentifier != null){
						pendingQueries[i].setJobIdentifier(jobIdentifier);
						pendingQueries[i].setStatus(BLASTQuery.Status.SUBMITTED);
						sendReport.addOutcome(pendingQueries[i].getPrimaryKey(), BLASTQuery.Status.SUBMITTED);
					}
				
				} catch(IllegalBLASTQueryException e){
					
					Log.i(TAG, "Could not assign query with ID "+pendingQueries[0].getPrimaryKey()+" a job identifier");
					pendingQueries[i].setStatus(BLASTQuery.Status.DRAFT);
					sendReport.addOutcome(pendingQueries[i].getPrimaryKey(), BLASTQuery.Status.DRAFT);
				} finally {
					save(pendingQueries[i]);				
				}
			}

			close();
			
		}
		
		return sendReport;
	}
	
	@Override
	protected void onPostExecute(Report result) {
		
		super.onPostExecute(result);
		
		if(result.getQueriesThatWereNotSent().length > 0){
			Toast t = Toast.makeText(context, "Some queries could not be sent. Please check them", Toast.LENGTH_SHORT);
			t.show();
		}else{
			Toast t = Toast.makeText(context, "Queries sent", Toast.LENGTH_SHORT);
			t.show();
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
		
		emblService.close();
		ncbiService.close();
	
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
		
		public Long[] getQueriesThatWereNotSent(){
			
			List<Long> erroneous = new ArrayList<Long>();
			
			for(Long queryID: report.keySet()){
				if(report.get(queryID).equals(BLASTQuery.Status.DRAFT)){
					erroneous.add(queryID);
				}
			}
			
			return erroneous.toArray(new Long[erroneous.size()]);
		}
		
	}
	
}
