package com.bioinformaticsapp.blastservices;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;

import com.bioinformaticsapp.QueryStatusRefreshReceiver;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;
import com.bioinformaticsapp.persistence.BLASTQueryLabBook;

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
