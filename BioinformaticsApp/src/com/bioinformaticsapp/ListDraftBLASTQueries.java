package com.bioinformaticsapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.bioinformaticsapp.blastservices.SubmitQueryService;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.domain.BLASTVendor;

public class ListDraftBLASTQueries extends ListBLASTQueries {
	public static final int READY_TO_SEND = 1;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.create_query_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int selectedItemId = item.getItemId();
		boolean itemSelectionHandled = false;
		Intent setupNewQuery = null;
		
		BLASTQuery newQuery = null;
		switch(selectedItemId){
		case R.id.create_embl_query:
			setupNewQuery = new Intent(this, SetUpEMBLEBIBLASTQuery.class);
			newQuery = BLASTQuery.emblBLASTQuery("blastn");
			break;
		
		case R.id.create_ncbi_query:
			setupNewQuery = new Intent(this, SetUpNCBIBLASTQuery.class);
			newQuery = BLASTQuery.ncbiBLASTQuery("blastn");
			break;
		
		default:
			itemSelectionHandled = super.onOptionsItemSelected(item);
			break;
		}
		
		if(setupNewQuery != null){
			setupNewQuery.putExtra("query", newQuery);
			startActivityForResult(setupNewQuery, CREATE_QUERY);
			itemSelectionHandled = true;
		}
		
		return itemSelectionHandled;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		status = Status.DRAFT;
        getLoaderManager().initLoader(BLAST_QUERIES_LOADER, null, this);
        registerForContextMenu(getListView());
	}
	
	protected void onResume(){
		super.onResume();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		BLASTQuery selectedQuery = queryAdapter.getItem(position);
		selectedQuery = labBook.findQueryById(selectedQuery.getPrimaryKey());
		Intent setupExistingQuery = null;
		switch(selectedQuery.getVendorID()){
		case BLASTVendor.NCBI:
			setupExistingQuery = new Intent(this, SetUpNCBIBLASTQuery.class);
			break;
			
		case BLASTVendor.EMBL_EBI:
			setupExistingQuery = new Intent(this, SetUpEMBLEBIBLASTQuery.class);
			break;
			
		default:
			break;
		}
		
		setupExistingQuery.putExtra("query", selectedQuery);
		
		startActivityForResult(setupExistingQuery, CREATE_QUERY);
	}
	
	protected void doDeleteAction(final long id){
		AlertDialog.Builder builder = deleteDialog();	
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				deleteQuery(id);
				getLoaderManager().restartLoader(BLAST_QUERIES_LOADER, null, ListDraftBLASTQueries.this);
			}
		});
		
		Dialog dialog = builder.create();
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode){
		case READY_TO_SEND:
			Intent sendService = new Intent(this, SubmitQueryService.class);
			startService(sendService);
			break;
		case CREATE_QUERY:
			getLoaderManager().restartLoader(BLAST_QUERIES_LOADER, null, this);
		default:
			break;
		}		
	}
	
	private final static int CREATE_QUERY = 2;
	
}
