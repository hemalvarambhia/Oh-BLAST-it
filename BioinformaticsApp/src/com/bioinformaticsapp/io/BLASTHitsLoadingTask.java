package com.bioinformaticsapp.io;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;

import com.bioinformaticsapp.domain.BLASTHit;
import com.bioinformaticsapp.domain.BLASTVendor;

public class BLASTHitsLoadingTask extends AsyncTask<InputStream, Void, List<Map<String, String>>>{

	private static final String TAG = "BLASTHitsLoader";
	
	private int vendorID;
	
	public BLASTHitsLoadingTask(int vendorID){
		this.vendorID = vendorID;
	}
	
	@Override
	protected List<Map<String, String>> doInBackground(InputStream... params) {
		
		List<Map<String, String>> hitInformation = new ArrayList<Map<String,String>>();
		try {
			BLASTHitsParser parser = getParserForBLASTVendor(vendorID);
			
			if(parser != null){
				List<BLASTHit> blastHits = getParserForBLASTVendor(vendorID).parse(params[0]);
				for(int i = 0; i < blastHits.size(); i++){
					Map<String, String> hitProperties = new HashMap<String, String>();
					hitProperties.put("accessionNumber", blastHits.get(i).getAccessionNumber());
					hitProperties.put("description", blastHits.get(i).getDescription());
					hitInformation.add(hitProperties);
				}
			}
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Could not find the file");
		}catch (Exception e){
			Log.e(TAG, "Could not parse file");
		}
		return hitInformation;
	}

	private BLASTHitsParser getParserForBLASTVendor(int vendorID){
		switch(vendorID){
		case BLASTVendor.NCBI:
			return new NCBIBLASTHitsParser();
		case BLASTVendor.EMBL_EBI:
			return new EMBLEBIBLASTHitsParser();
		default:
			return null;
		}
	}
	
}
