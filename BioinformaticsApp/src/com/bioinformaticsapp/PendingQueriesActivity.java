package com.bioinformaticsapp;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class PendingQueriesActivity extends ListActivity implements LoaderCallbacks<Cursor>{


	private static final int RUNNING_CURSOR_LOADER = 0x03;
	
	private CursorAdapter mCursorAdapter;
	
	private final static int REFRESH_MENU_ITEM = 0;
	
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        
        int[] viewId = new int[]{R.id.query_job_id_label, R.id.query_job_status_label};
        
        Intent intent = getIntent();
        
        if(intent.getData() == null){
        	intent.setData(BLASTQuery.BLASTJob.CONTENT_URI);
        }
        
        //We only wish to show running and finished queries:
        String [] dataColumns = new String[]{BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS};
        
        getLoaderManager().initLoader(RUNNING_CURSOR_LOADER, null, this);
        
        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.blastquery_list_item, null, dataColumns, viewId);
        
        setListAdapter(mCursorAdapter);
        
    }
	
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getLoaderManager().restartLoader(RUNNING_CURSOR_LOADER, null, this);
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		MenuItem item = menu.add(0, REFRESH_MENU_ITEM, 0, "Refresh");
		
		item.setIcon(android.R.drawable.ic_popup_sync);
		
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}

	

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		boolean itemSelectionHandled = false;
		
		int itemId = item.getItemId();
		
		switch(itemId){
		case REFRESH_MENU_ITEM:
			getLoaderManager().restartLoader(RUNNING_CURSOR_LOADER, null, this);
			itemSelectionHandled = true;
			break;
			
		default:
			itemSelectionHandled = super.onOptionsItemSelected(item);
			break;
			
		}
		
		return itemSelectionHandled;
	}



	/* Event handling when the user taps a row in the list item
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		
		//Set up the intent to launch the setup screen
		Intent setupExistingQuery = new Intent(this, QueryParametersActivity.class);
		
		//Create the content uri for the selected list item
		Uri uri = ContentUris.withAppendedId(BLASTJob.CONTENT_QUERY_ID_BASE_URI, id);	
				
		//Attach the URI to the intent so the content resolver for the set up screen
		//can retrieve the corresponding content value
		setupExistingQuery.setData(uri);
		
		//Launch the activity
		startActivity(setupExistingQuery);
		
	}


	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = getIntent().getData();
		
		String where = BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS +" IN (?, ?, ?)";
		
		String[] whereArgs = new String[]{BLASTQuery.Status.RUNNING.toString(), BLASTQuery.Status.PENDING.toString(), BLASTQuery.Status.SUBMITTED.toString()};
		
		CursorLoader cursorLoader = new CursorLoader(this, uri, BLASTJob.LIST_PROJECTIONS, where, whereArgs, null);
		
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		mCursorAdapter.swapCursor(cursor);
		
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		mCursorAdapter.swapCursor(null);
	}


}
