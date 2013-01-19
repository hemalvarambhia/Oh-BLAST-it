package com.bioinformaticsapp;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;

import com.bioinformaticsapp.content.BLASTQueryLoader;
import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.widget.BLASTQueryAdapter;

public class BLASTQueryListingActivity extends ListActivity implements LoaderCallbacks<BLASTQuery[]> {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    	queryController = new BLASTQueryController(this); 
    	parametersController = new SearchParameterController(this);

    	Intent intent = getIntent();
        
        if(intent.getData() == null){
        	intent.setData(BLASTQuery.BLASTJob.CONTENT_URI);
        }
        
	}
	
	protected void onPause(){
		super.onPause();
		
		if(isFinishing()){
			queryController.close();
			parametersController.close();
		}
		
	}
	
	public Loader<BLASTQuery[]> onCreateLoader(int id, Bundle args) {
		
		Loader<BLASTQuery[]> cursorLoader = new BLASTQueryLoader(this, mStatus);
		
		return cursorLoader;
	}

	public void onLoadFinished(Loader<BLASTQuery[]> cursorLoader, BLASTQuery[] queries) {
		
		mQueryAdapter = new BLASTQueryAdapter(this, queries);
		
		setListAdapter(mQueryAdapter);
	
	}

	public void onLoaderReset(Loader<BLASTQuery[]> cursorLoader) {
		
	}
	
	protected boolean deleteQuery(long id){
		
		int numberOfParametersForQuery = parametersController.getParametersForQuery(id).size();
	
		int numberOfParametersDeleted = parametersController.deleteParametersFor(id);
		
		int numberOfQueriesDeleted = queryController.delete(id);
		
		return (numberOfQueriesDeleted == 1) && (numberOfParametersDeleted == numberOfParametersForQuery);
	
	}


	protected BLASTQueryAdapter mQueryAdapter;
	
	protected BLASTQueryController queryController;
	
	protected SearchParameterController parametersController;

	protected Status mStatus;
	
}
