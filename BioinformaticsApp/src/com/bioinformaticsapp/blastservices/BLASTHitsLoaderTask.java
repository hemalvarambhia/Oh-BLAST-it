package com.bioinformaticsapp.blastservices;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTVendor;

public class BLASTHitsLoaderTask extends AsyncTask<BLASTQuery, Void, String> {

	public BLASTHitsLoaderTask(Context context, BLASTSearchEngine engine){
		this.context = context;
		blastSearchEngine = engine;
	}
	
	@Override
	protected String doInBackground(BLASTQuery... params) {
		String jobIdentifier = params[0].getJobIdentifier();
		if(!connectedToWeb()){
			return null;
		}
		
		String format = "xml";
		
		switch(params[0].getVendorID()){
		case BLASTVendor.NCBI:
			format = format.toUpperCase();
			break;
		}
		
		String blastResultsAsXml = blastSearchEngine.retrieveBLASTResults(jobIdentifier, format);
		blastResultsAsXml = cleanupXmlOutput(blastResultsAsXml);
		
		String nameOfResultsFile = null;
		try {
			nameOfResultsFile = String.format("%s.xml", jobIdentifier);
			FileOutputStream resultsFile = context.openFileOutput(nameOfResultsFile, Context.MODE_PRIVATE);
			PrintWriter writer = new PrintWriter(new BufferedOutputStream(resultsFile));
			writer.println(blastResultsAsXml);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			//Do nothing as we check only if results file was created on disk
			nameOfResultsFile = null;
		}
		
		return nameOfResultsFile;
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
	
	private BLASTSearchEngine blastSearchEngine;
	private Context context;
}
