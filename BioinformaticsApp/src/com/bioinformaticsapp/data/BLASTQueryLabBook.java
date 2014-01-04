package com.bioinformaticsapp.data;

import java.util.ArrayList;
import java.util.List;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.SearchParameter;

import android.content.Context;

public class BLASTQueryLabBook {

	public BLASTQueryLabBook(Context context) {
		this.context = context;
	}

	public BLASTQuery save(BLASTQuery aQuery) {
		blastQueryController = new BLASTQueryController(context);
		searchParameterController = new SearchParameterController(context);
		long queryPrimaryKey = blastQueryController.save(aQuery);
		blastQueryController.close();
		BLASTQuery savedQuery = (BLASTQuery)aQuery.clone();
		savedQuery.setPrimaryKeyId(queryPrimaryKey);
		
		List<SearchParameter> parameters = new ArrayList<SearchParameter>();
		for(SearchParameter parameter: aQuery.getAllParameters()){
			parameter.setBlastQueryId(queryPrimaryKey);
			searchParameterController.save(parameter);
			parameters.add(parameter);
		}
		aQuery.updateAllParameters(parameters);
		searchParameterController.close();
		return savedQuery;
	}
	
	private Context context;
	private BLASTQueryController blastQueryController;
	private SearchParameterController searchParameterController;
	
}
