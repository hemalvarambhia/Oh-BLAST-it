package com.bioinformaticsapp;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.OptionalParameter;

public class DraftBLASTQueriesActivity extends ListActivity implements LoaderCallbacks<Cursor>{

	private final static int DRAFT_QUERIES_LOADER = 0x01;
	
	private final static String TAG = "DraftBLASTQueriesActivity";
	
	private final static int CREATE_QUERY = 2;
	
	private CursorAdapter mCursorAdapter;
	
	private BLASTQueryController queryController;
	private OptionalParameterController parametersController;
	
	public static final int READY_TO_SEND = 1;
	
	
	/**
	 * Here we retrieve the draft queries from a separate thread 
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		//Get the path to the content
		Uri uri = getIntent().getData();
		
		String where = BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS+" = ?";
        
		//We want to retrieve DRAFT queries:
        String[] whereArgs = new String[]{BLASTQuery.Status.DRAFT.toString()};
		
        CursorLoader loader = new CursorLoader(this, uri, BLASTJob.LIST_PROJECTIONS, where, whereArgs, null);
		
		return loader;
	}

	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		mCursorAdapter.swapCursor(cursor);
	
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		
		mCursorAdapter.swapCursor(null);
	
	}

	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.create_query_menu, menu);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int selectedItemId = item.getItemId();
		boolean itemSelectionHandled = false;
		Intent setupNewQuery = null;
		
		switch(selectedItemId){
		
		case R.id.create_embl_query:
			setupNewQuery = new Intent(this, EMBLEBISetUpQueryActivity.class);
			setupNewQuery.putExtra("query", new BLASTQuery("blastn", BLASTVendor.EMBL_EBI));
			break;
		
		case R.id.create_ncbi_query:
			setupNewQuery = new Intent(this, NCBIQuerySetUpActivity.class);
			setupNewQuery.putExtra("query", new BLASTQuery("blastn", BLASTVendor.NCBI));
			break;
		
		default:
			itemSelectionHandled = super.onOptionsItemSelected(item);
			break;
		}
		
		if(setupNewQuery != null){
			startActivityForResult(setupNewQuery, CREATE_QUERY);
			itemSelectionHandled = true;
		}
		
		return itemSelectionHandled;
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = getMenuInflater();
		menu.setHeaderTitle("Select an option:");
		inflater.inflate(R.menu.general_context_menu, menu);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		boolean itemSelectionHandled = false;
		
		AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		
		int itemId = item.getItemId();
		
		switch(itemId){
		case R.id.delete_menu_item: {
			
			doDeleteAction(menuinfo.id);
			
			itemSelectionHandled = true;
		}
		
		break;
			
		default:
			itemSelectionHandled = super.onContextItemSelected(item);
			break;
		}
		
		getLoaderManager().restartLoader(DRAFT_QUERIES_LOADER, null, this);
		
		return itemSelectionHandled;
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		int[] viewId = new int[]{R.id.query_job_id_label, R.id.query_job_status_label};
        
	    Intent intent = getIntent();
        
        if(intent.getData() == null){
        	intent.setData(BLASTQuery.BLASTJob.CONTENT_URI);
        }
        
        String [] dataColumns = new String[]{BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS};
        
        //We wish to load data from the content provider asynchronously
        //and not on the UI thread.
        getLoaderManager().initLoader(DRAFT_QUERIES_LOADER, null, this);
        
        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.draft_query_list_item, null, dataColumns, viewId, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        

    	queryController = new BLASTQueryController(this); 
    	parametersController = new OptionalParameterController(this);
        
        //Register each list item for a context menu:
        registerForContextMenu(getListView());
        
        setListAdapter(mCursorAdapter);
	}
	
	protected void onPause(){
		super.onPause();
		
		if(isFinishing()){
			queryController.close();
			parametersController.close();
		}
		
	}
	
	protected void onResume(){
		super.onResume();
		
		getLoaderManager().restartLoader(DRAFT_QUERIES_LOADER, null, this);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		BLASTQuery selectedQuery = queryController.findBLASTQueryById(id);
		List<OptionalParameter> parameters = parametersController.getParametersForQuery(selectedQuery.getPrimaryKey());
		
		selectedQuery.updateAllParameters(parameters);
		Log.d(TAG, selectedQuery.toString());
		Intent setupExistingQuery = null;
		
		switch(selectedQuery.getVendorID()){
		case BLASTVendor.NCBI:
			setupExistingQuery = new Intent(this, NCBIQuerySetUpActivity.class);
			break;
		case BLASTVendor.EMBL_EBI:
			setupExistingQuery = new Intent(this, EMBLEBISetUpQueryActivity.class);
			break;
		default:
			break;
		}
		setupExistingQuery.putExtra("query", selectedQuery);
		
		//Launch the activity
		startActivityForResult(setupExistingQuery, CREATE_QUERY);
	}
	
	private void doDeleteAction(long id){
		final Uri uri = ContentUris.withAppendedId(BLASTJob.CONTENT_QUERY_ID_BASE_URI, id);
		
		AlertDialog.Builder builder = new Builder(this);
		builder = builder.setTitle("Deleting");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder = builder.setMessage(R.string.delete_query_message);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				deleteQuery(uri);
			}
		});
		
		builder.setNegativeButton("Cancel", null);
		
		Dialog dialog = builder.create();
		dialog.show();
		
	}

	private int deleteQuery(Uri uriToDelete){
		
		int numberOfRowsDeleted = getContentResolver().delete(uriToDelete, null, null);
		getLoaderManager().restartLoader(DRAFT_QUERIES_LOADER, null, this);
		
		return numberOfRowsDeleted;
	
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(resultCode){
		case READY_TO_SEND:
			Intent sendService = new Intent(this, SubmitQueryService.class);
			startService(sendService);
			break;
			
		default:
			break;
		}
		
	}
	

}
