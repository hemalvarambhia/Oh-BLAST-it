package com.bioinformaticsapp;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;

import com.bioinformaticsapp.blastservices.BLASTQuerySender;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.EMBLEBIBLASTService;
import com.bioinformaticsapp.blastservices.NCBIBLASTService;
import com.bioinformaticsapp.data.BLASTQueryLabBook;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;

public class SubmitQueryService extends IntentService {

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
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(this);
		int[] vendors = new int[]{ BLASTVendor.EMBL_EBI, BLASTVendor.NCBI };
		for(int vendor: vendors){
			List<BLASTQuery> pendingQueries = labBook.findPendingBLASTQueriesFor(vendor);
			BLASTQuerySender sender = new BLASTQuerySender(this, blastSearchEngineFor(vendor));
			sender.execute(asArray(pendingQueries));
		}
		sendBroadcast(new Intent(QueryStatusRefreshReceiver.REFRESH_ACTION));
	}
	
	private BLASTQuery[] asArray(List<BLASTQuery> queries){
		BLASTQuery[] queryArray = new BLASTQuery[queries.size()];
		return queries.toArray(queryArray);
		
	}
	
	private BLASTSearchEngine blastSearchEngineFor(int vendor){
		BLASTSearchEngine engine = null;
		switch(vendor){
		case BLASTVendor.EMBL_EBI:
			engine = new EMBLEBIBLASTService();
			break;
		case BLASTVendor.NCBI:
			engine = new NCBIBLASTService();
			break;
		}
		
		return engine;
	}
	
}
