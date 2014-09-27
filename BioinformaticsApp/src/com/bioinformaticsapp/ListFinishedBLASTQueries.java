package com.bioinformaticsapp;

import java.io.FileNotFoundException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bioinformaticsapp.blastservices.BLASTHitsDownloadingTask;
import com.bioinformaticsapp.blastservices.BLASTSearchEngine;
import com.bioinformaticsapp.blastservices.BLASTSearchEngineFactory;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;

public class ListFinishedBLASTQueries extends ListBLASTQueries {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	status = Status.FINISHED;
    	getLoaderManager().initLoader(BLAST_QUERIES_LOADER, null, this);
        registerForContextMenu(getListView());
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		MenuItem item = menu.add(0, REFRESH_MENU_ITEM, 0, "Refresh");
		item.setIcon(android.R.drawable.ic_popup_sync);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		boolean itemSelectionHandled = false;
		
		int itemId = item.getItemId();
		switch(itemId){
		case REFRESH_MENU_ITEM:
			getLoaderManager().restartLoader(BLAST_QUERIES_LOADER, null, this);
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
		case R.id.delete_menu_item: {
			BLASTQuery selected = queryAdapter.getItem(menuinfo.position);
			doDeleteAction(selected.getPrimaryKey());
			itemSelectionHandled = true;
		}
		break;
		
		case R.id.view_parameters_menu_item: {	
			BLASTQuery selected = queryAdapter.getItem(menuinfo.position);
			selected = labBook.findQueryById(selected.getPrimaryKey());
			Intent viewParameters = new Intent(this, ViewBLASTQuerySearchParameters.class);
			viewParameters.putExtra("query", selected);
			startActivity(viewParameters);
			itemSelectionHandled = true;
		}
		break;
		
		default:
			itemSelectionHandled = super.onContextItemSelected(item);
			break;
		}
		
		return itemSelectionHandled;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		selected = queryAdapter.getItem(position);
		selected = labBook.findQueryById(selected.getPrimaryKey());
		
		if(!fileExists(selected.getJobIdentifier()+".xml")){
			BLASTSearchEngine searchEngine = BLASTSearchEngineFactory.getBLASTSearchEngineFor(selected.getVendorID());
			BLASTHitsDownloader downloader = new BLASTHitsDownloader(this, searchEngine);
			downloader.execute(selected);
		}else{
			Intent viewResults = new Intent(this, ViewBLASTHits.class);
			viewResults.putExtra("query", selected);
			startActivity(viewResults);
		}
	}

	private boolean fileExists(String blastHitsFile){
		boolean fileExists = false;
		try {
			openFileInput(blastHitsFile);
			fileExists = true;
		} catch (FileNotFoundException e) {
			// No need to do anything as fileExists is set to false initially
			// anyway
		}
		
		return fileExists;
	}
	
	private void doDeleteAction(final long id){
		AlertDialog.Builder builder = new Builder(this);
		builder = builder.setTitle("Deleting");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder = builder.setMessage(R.string.delete_query_message);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				deleteQuery(id);
				getLoaderManager().restartLoader(BLAST_QUERIES_LOADER, null, ListFinishedBLASTQueries.this);
			}
		});
		
		builder.setNegativeButton("Cancel", null);
		Dialog dialog = builder.create();
		dialog.show();
	}
	
	private BLASTQuery selected;	
	private final static int REFRESH_MENU_ITEM = 0;
	private class BLASTHitsDownloader extends BLASTHitsDownloadingTask {

		private ProgressDialog mProgressDialog;
		
		public BLASTHitsDownloader(Context context, BLASTSearchEngine engine) {
			super(context, engine);
			mProgressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
		}

		@Override
		protected void onPostExecute(String fileName) {
			super.onPostExecute(fileName);
			mProgressDialog.dismiss();
			
			if(fileName != null){
				Intent viewResults = new Intent(ListFinishedBLASTQueries.this, ViewBLASTHits.class);
				viewResults.putExtra("query", selected);
				
				startActivity(viewResults);
				
			}else {
				if(!connectedToWeb()){
					Toast webConnectionMessage = Toast.makeText(ListFinishedBLASTQueries.this, "A web connection is needed to download the results", Toast.LENGTH_SHORT);
					webConnectionMessage.show();
				}
			}			
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog.setTitle("Downloading BLAST Hits");
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.show();
		}		
	}
}
