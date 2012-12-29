package com.bioinformaticsapp;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.Filter;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;
import com.bioinformaticsapp.models.BLASTQuery.Status;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class BLASTQueryListingActivity extends ListActivity implements LoaderCallbacks<Cursor> {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    	queryController = new BLASTQueryController(this); 
    	parametersController = new OptionalParameterController(this);

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
		
		CursorLoader cursorLoader = new CursorLoader(this, uri, BLASTJob.LIST_PROJECTIONS, filter.condition(), filter.arguments(), null);
		
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
	
	protected OptionalParameterController parametersController;

	protected Filter filter;
	
}
