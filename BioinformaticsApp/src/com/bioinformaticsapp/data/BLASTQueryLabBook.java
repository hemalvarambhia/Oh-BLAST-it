package com.bioinformaticsapp.data;

import com.bioinformaticsapp.models.BLASTQuery;

import android.content.Context;

public class BLASTQueryLabBook {

	public BLASTQueryLabBook(Context context) {
		this.context = context;
		blastQueryController = new BLASTQueryController(this.context);
	}

	public BLASTQuery save(BLASTQuery aQuery) {
		long queryPrimaryKey = blastQueryController.save(aQuery);
		BLASTQuery savedQuery = (BLASTQuery)aQuery.clone();
		savedQuery.setPrimaryKeyId(queryPrimaryKey);
		
		return savedQuery;
	}
	
	private Context context;
	private BLASTQueryController blastQueryController;

}
