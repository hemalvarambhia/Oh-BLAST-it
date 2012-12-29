package com.bioinformaticsapp;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;
import com.bioinformaticsapp.models.BLASTQuery.Status;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class BLASTQueryListingActivity extends ListActivity implements LoaderCallbacks<Cursor> {

	
	
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = getIntent().getData();
		
		String where = BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS +" = ?";
		
		String[] whereArgs = new String[]{status.toString()};
		
		CursorLoader cursorLoader = new CursorLoader(this, uri, BLASTJob.LIST_PROJECTIONS, where, whereArgs, null);
		
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

	protected Status status;
	
}
