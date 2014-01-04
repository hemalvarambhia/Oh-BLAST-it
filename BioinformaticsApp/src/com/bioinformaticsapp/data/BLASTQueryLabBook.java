package com.bioinformaticsapp.data;

import android.content.Context;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.SearchParameter;

public class BLASTQueryLabBook {

	public BLASTQueryLabBook(Context context) {
		this.context = context;
	}

	public BLASTQuery save(BLASTQuery aQuery) {
		blastQueryController = new BLASTQueryController(context);
		searchParameterController = new SearchParameterController(context);
		long queryPrimaryKey = blastQueryController.save(aQuery);
		blastQueryController.close();
		
		for(SearchParameter parameter: aQuery.getAllParameters()){
			parameter.setBlastQueryId(queryPrimaryKey);
			searchParameterController.save(parameter);
		}
		searchParameterController.close();
		BLASTQuery savedQuery = (BLASTQuery)aQuery.clone();
		savedQuery.setPrimaryKeyId(queryPrimaryKey);
		
		return savedQuery;
	}
	
	private Context context;
	private BLASTQueryController blastQueryController;
	private SearchParameterController searchParameterController;
	
}
