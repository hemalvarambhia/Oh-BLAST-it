package com.bioinformaticsapp.data;

import java.util.List;

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
		BLASTQuery savedQuery = (BLASTQuery)aQuery.clone();
		if(aQuery.getPrimaryKey() == null){
			long queryPrimaryKey = blastQueryController.save(aQuery);
			savedQuery.setPrimaryKeyId(queryPrimaryKey);
		}else{
			blastQueryController.update(aQuery);
			searchParameterController.deleteParametersFor(aQuery.getPrimaryKey());
		}
		blastQueryController.close();
		
		for(SearchParameter parameter: aQuery.getAllParameters()){
			searchParameterController.saveFor(savedQuery.getPrimaryKey(), parameter);
		}
		searchParameterController.close();
		
		return savedQuery;
	}
	
	public BLASTQuery findQueryById(Long id) {
		blastQueryController = new BLASTQueryController(context);
		searchParameterController = new SearchParameterController(context);
		BLASTQuery queryWithID = blastQueryController.findBLASTQueryById(id);
		List<SearchParameter> parameters = searchParameterController.getParametersForQuery(id);
		queryWithID.updateAllParameters(parameters);
		return queryWithID;
	}
	
	private Context context;
	private BLASTQueryController blastQueryController;
	private SearchParameterController searchParameterController;
	
	
}
