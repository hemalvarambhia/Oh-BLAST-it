package com.bioinformaticsapp;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.content.BLASTQueryLoader;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.widget.BLASTQueryAdapter;

public abstract class ListBLASTQueries extends ListActivity implements LoaderCallbacks<BLASTQuery[]> {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		labBook = new BLASTQueryLabBook(this);
		queryAdapter = new BLASTQueryAdapter(this, new ArrayList<BLASTQuery>());
    	setListAdapter(queryAdapter);
	}
	
	public Loader<BLASTQuery[]> onCreateLoader(int id, Bundle args) {
		Loader<BLASTQuery[]> cursorLoader = new BLASTQueryLoader(this, status);
		
		return cursorLoader;
	}

	public void onLoadFinished(Loader<BLASTQuery[]> cursorLoader, BLASTQuery[] queries) {
		queryAdapter.clear();
		queryAdapter.addAll(queries);
		queryAdapter.notifyDataSetChanged();
	}

	public void onLoaderReset(Loader<BLASTQuery[]> cursorLoader) {
		
	}
	
	protected boolean deleteQuery(long id){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(this);
		BLASTQuery queryToDelete = labBook.findQueryById(id);
		queryAdapter.remove(queryToDelete);
		queryAdapter.notifyDataSetChanged();
		int numberOfQueriesDeleted = labBook.remove(id);
		
		return numberOfQueriesDeleted == 1;
	}
	
	protected AlertDialog.Builder deleteDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder = builder.setTitle("Deleting");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder = builder.setMessage(R.string.delete_query_message);
		builder.setCancelable(false);
		builder.setNegativeButton("Cancel", null);
		return builder;
	}
	
	protected abstract void doDeleteAction(final long id);

	protected BLASTQueryAdapter queryAdapter;
	protected Status status;
	protected BLASTQueryLabBook labBook;
	protected static final int BLAST_QUERIES_LOADER = 0x01;
}
