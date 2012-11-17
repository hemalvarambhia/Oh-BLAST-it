package com.bioinformaticsapp.web;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;

public class BLASTHitsDownloadingTask extends AsyncTask<BLASTQuery, Void, String> {

	private static final String TAG = "BLASTHitsDownloader";
	private Context context;
	
	public BLASTHitsDownloadingTask(Context context){
		this.context = context;
	}
	
	
	@Override
	protected String doInBackground(BLASTQuery... params) {
		String jobIdentifier = params[0].getJobIdentifier();
		if(!connectedToWeb()){
			return null;
		}
		
		BLASTSequenceQueryingService service = getServiceFor(params[0].getVendorID());
		
		String format = "xml";
		
		switch(params[0].getVendorID()){
		case BLASTVendor.NCBI:
			format = format.toUpperCase();
			break;
		}
		
		String blastResultsAsXml = service.retrieveBLASTResults(jobIdentifier, format);
		blastResultsAsXml = cleanupXmlOutput(blastResultsAsXml);
		
		String nameOfResultsFile = null;
		try {
			FileOutputStream resultsFile = context.openFileOutput(jobIdentifier+".xml", Context.MODE_PRIVATE);
			PrintWriter writer = new PrintWriter(new BufferedOutputStream(resultsFile));
			writer.println(blastResultsAsXml);
			writer.flush();
			writer.close();
			nameOfResultsFile = jobIdentifier+".xml";
			
		} catch (FileNotFoundException e) {
			//Do nothing as we check only if results file was created on disk
		}
		
		return nameOfResultsFile;
	}
	
	private BLASTSequenceQueryingService getServiceFor(int blastVendor){
		switch(blastVendor){
		case BLASTVendor.EMBL_EBI:
			return new EMBLEBIBLASTService();
		case BLASTVendor.NCBI:
			return new NCBIBLASTService();
		default:
			return null;
		}
	}
	
	private String cleanupXmlOutput(String xmlBlastoutput){
		
		return xmlBlastoutput.replaceAll("<CAN>", "<![CDATA[<CAN>]]>");
		
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
	


}
