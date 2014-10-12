package com.bioinformaticsapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bioinformaticsapp.domain.BLASTQuery.Status;

public class ListPendingBLASTQueries extends ListBLASTQueries {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		status = Status.SUBMITTED;
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

	protected void doDeleteAction(final long id){
		AlertDialog.Builder builder = deleteDialog();
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				deleteQuery(id);
				getLoaderManager().restartLoader(BLAST_QUERIES_LOADER, null, ListPendingBLASTQueries.this);
			}
		});
		
		Dialog dialog = builder.create();
		dialog.show();
	}

	private final static int REFRESH_MENU_ITEM = 0;
	
}
