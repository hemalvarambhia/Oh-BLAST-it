package com.bioinformaticsapp;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
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

import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;

public class ListPendingBLASTQueries extends ListBLASTQueries {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		status = Status.SUBMITTED;
		getLoaderManager().initLoader(RUNNING_CURSOR_LOADER, null, this);
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
		
		getLoaderManager().restartLoader(RUNNING_CURSOR_LOADER, null, this);
		
		return itemSelectionHandled;
		
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
				getLoaderManager().restartLoader(RUNNING_CURSOR_LOADER, null, ListPendingBLASTQueries.this);
			}
		});
		
		builder.setNegativeButton("Cancel", null);
		
		Dialog dialog = builder.create();
		dialog.show();
		
	}
	
	private static final int RUNNING_CURSOR_LOADER = 0x03;
	private final static int REFRESH_MENU_ITEM = 0;
	
}
