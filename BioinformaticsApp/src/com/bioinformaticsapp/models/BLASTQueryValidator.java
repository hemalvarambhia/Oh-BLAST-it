package com.bioinformaticsapp.models;

import android.os.AsyncTask;

public class BLASTQueryValidator extends AsyncTask<BLASTQuery, Void, Boolean> {

	@Override
	protected Boolean doInBackground(BLASTQuery... query) {
		
		boolean isValid = query[0].isValid();
		return isValid;
	}
	
	

}
