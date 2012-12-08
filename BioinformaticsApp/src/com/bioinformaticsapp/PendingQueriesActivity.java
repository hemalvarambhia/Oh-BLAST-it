package com.bioinformaticsapp;

import java.util.List;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.fragments.BLASTQueryParametersDialog;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;
import com.bioinformaticsapp.models.OptionalParameter;

public class PendingQueriesActivity extends ListActivity implements LoaderCallbacks<Cursor>{


	private static final int RUNNING_CURSOR_LOADER = 0x03;
	
	private CursorAdapter mCursorAdapter;
	
	private final static int REFRESH_MENU_ITEM = 0;
	
	private BLASTQueryController queryController;
	
	private OptionalParameterController parameterController;
	
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
        
        registerForContextMenu(getListView());
     
        queryController = new BLASTQueryController(this);
        
        parameterController = new OptionalParameterController(this);
        
    }
	
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(RUNNING_CURSOR_LOADER, null, this);
	}


	public void onPause(){
		super.onPause();
		if(isFinishing()){
			queryController.close();
			parameterController.close();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater menuInflater = getMenuInflater();
		menu.setHeaderTitle("Select an option:");
		menuInflater.inflate(R.menu.general_context_menu, menu);
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		boolean itemSelectionHandled = false;
		
		AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		int itemId = item.getItemId();
		
		switch(itemId){
		
		case R.id.view_parameters_menu_item: {
			
			BLASTQueryParametersDialog dialog = new BLASTQueryParametersDialog();
			BLASTQuery selected = queryController.findBLASTQueryById(menuinfo.id);
			List<OptionalParameter> parameters = parameterController.getParametersForQuery(menuinfo.id);
			selected.updateAllParameters(parameters);
			Bundle bundle = new Bundle();
			bundle.putSerializable("query", selected);
			dialog.setArguments(bundle);
			dialog.show(getFragmentManager(), "dialog");
			
			itemSelectionHandled = true;
		}
		
		default:
			itemSelectionHandled = super.onContextItemSelected(item);
			break;
		}
		
		getLoaderManager().restartLoader(RUNNING_CURSOR_LOADER, null, this);
		
		return itemSelectionHandled;
		
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
