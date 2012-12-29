package com.bioinformaticsapp.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.SearchParameter;

public class BLASTQueryPoller extends AsyncTask<BLASTQuery, Void, BLASTQueryPoller.Report> {

	private Context context;
	
	private static final int JOB_FINISHED_NOTI_ID = 2;

	private static final String TAG = "BLASTQueryPoller";
	
	private NCBIBLASTService ncbiService;
	
	private EMBLEBIBLASTService emblService;
	
	public BLASTQueryPoller(Context context){
		this.context = context;
		ncbiService = new NCBIBLASTService();
		emblService = new EMBLEBIBLASTService();
	}
	
	@Override
	protected Report doInBackground(BLASTQuery... queries) {
		Report report = new Report();
		if(connectedToWeb()){
			for(int i = 0; i < queries.length; i++){
				
				BLASTSequenceQueryingService service = getServiceFor(queries[i].getVendorID());
				
				BLASTQuery.Status current = service.pollQuery(queries[i].getJobIdentifier());
				
				queries[i].setStatus(current);
				
				save(queries[i]);

				report.addOutcome(queries[i].getPrimaryKey(), current);
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
			NotificationManager mgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification.Builder builder = new Notification.Builder(context);
			builder.setWhen(System.currentTimeMillis());
			builder.setAutoCancel(true);
			builder.setSmallIcon(com.bioinformaticsapp.R.drawable.ic_dna);
			builder.setTicker("BLAST queries finished");
			builder.setContentText("Click to view the results");
			builder.setContentTitle("BLAST Queries Finished");
			Notification notification = builder.getNotification();
			mgr.notify(JOB_FINISHED_NOTI_ID, notification);
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
		OptionalParameterController parametersController = new OptionalParameterController(this.context);
		
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
