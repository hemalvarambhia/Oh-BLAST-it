package com.bioinformaticsapp;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.SearchParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;

public class BLASTQueryListingActivity extends ListActivity implements LoaderCallbacks<Cursor> {

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
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = getIntent().getData();
		
		CursorLoader cursorLoader = new CursorLoader(this, uri, BLASTJob.LIST_PROJECTIONS, filterCondition, values, null);
		
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		mCursorAdapter.swapCursor(cursor);
	
	}

	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		
		mCursorAdapter.swapCursor(null);
		
	}

	protected SimpleCursorAdapter mCursorAdapter;
	
	protected BLASTQueryController queryController;
	
	protected SearchParameterController parametersController;

	protected String filterCondition;
	
	protected String[] values;
	
}
