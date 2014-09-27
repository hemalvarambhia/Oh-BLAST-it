package com.bioinformaticsapp.content;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.SearchParameter;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.persistence.BLASTQueryController;
import com.bioinformaticsapp.persistence.SearchParameterController;

public class BLASTQueryLabBook {

	public BLASTQueryLabBook(Context context) {
		this.context = context;
	}

	public BLASTQuery save(BLASTQuery aQuery) {
		initialiseControllers();
		BLASTQuery savedQuery = (BLASTQuery)aQuery.clone();
		if(aQuery.getPrimaryKey() == null){
			long queryPrimaryKey = blastQueryController.save(aQuery);
			savedQuery.setPrimaryKeyId(queryPrimaryKey);
		}else{
			blastQueryController.update(aQuery);
			searchParameterController.deleteParametersFor(aQuery.getPrimaryKey());
		}
		
		for(SearchParameter parameter: aQuery.getAllParameters()){
			searchParameterController.saveFor(savedQuery.getPrimaryKey(), parameter);
		}
		
		closeControllers();
		
		return savedQuery;
	}
	
	public BLASTQuery findQueryById(Long id) {
		initialiseControllers();
		BLASTQuery queryWithID = blastQueryController.findBLASTQueryById(id);
		List<SearchParameter> parameters = searchParameterController.getParametersForQuery(id);
		queryWithID.updateAllParameters(parameters);
		closeControllers();
		return queryWithID;
	}
	
	public List<BLASTQuery> findBLASTQueriesByStatus(Status status) {
		initialiseControllers();
		List<BLASTQuery> queriesWithStatus = blastQueryController.findBLASTQueriesByStatus(status);
		queriesWithStatus.addAll(blastQueryController.findBLASTQueriesByStatus(Status.RUNNING));
		for(BLASTQuery query: queriesWithStatus){
			List<SearchParameter> parameters = searchParameterController.getParametersForQuery(query.getPrimaryKey());
			query.updateAllParameters(parameters);
		}
		
		closeControllers();
		
		return queriesWithStatus;
	}
	
	public List<BLASTQuery> findPendingBLASTQueriesFor(int vendor){
		List<BLASTQuery> allPendingQueries = findBLASTQueriesByStatus(Status.PENDING);
		if(allPendingQueries.isEmpty()){
			return new ArrayList<BLASTQuery>();
		}
		
		List<BLASTQuery> queriesPendingForVendor = queriesForVendor(allPendingQueries, vendor);
		
		return queriesPendingForVendor;
	}
	
	public List<BLASTQuery> submittedBLASTQueriesForVendor(int vendor) {
		List<BLASTQuery> allSubmittedQueries = findBLASTQueriesByStatus(Status.SUBMITTED);
		if(allSubmittedQueries.isEmpty()){
			return new ArrayList<BLASTQuery>();
		}
		
		List<BLASTQuery> queriesSubmittedToVendor = queriesForVendor(allSubmittedQueries, vendor);
		
		return queriesSubmittedToVendor;
	}
	
	public int remove(Long primaryKey) {
		initialiseControllers();
		searchParameterController.deleteParametersFor(primaryKey);
		BLASTQuery queryToDelete = blastQueryController.findBLASTQueryById(primaryKey);
		deleteHitsFile(queryToDelete);
		int numberDeleted = blastQueryController.delete(primaryKey);
		closeControllers();
		return numberDeleted;
	}
	
	private void deleteHitsFile(BLASTQuery query) {
		String fileName = String.format("%s.xml", query.getJobIdentifier());
		File hitsFile = context.getFileStreamPath(fileName);
		
		if(hitsFile != null){
			if(hitsFile.exists()){
				hitsFile.delete();
			}
		}
	}

	private List<BLASTQuery> queriesForVendor(List<BLASTQuery> queries, int vendor){
		List<BLASTQuery> queriesForVendor = new ArrayList<BLASTQuery>();
		for(BLASTQuery query: queries){
			if(query.getVendorID() == vendor){
				queriesForVendor.add(query);
			}
		}
		
		return queriesForVendor;
	}
	
	private void closeControllers() {
		searchParameterController.close();
		blastQueryController.close();
	}

	private void initialiseControllers() {
		blastQueryController = new BLASTQueryController(context);
		searchParameterController = new SearchParameterController(context);
	}

	private Context context;
	private BLASTQueryController blastQueryController;
	private SearchParameterController searchParameterController;
}
