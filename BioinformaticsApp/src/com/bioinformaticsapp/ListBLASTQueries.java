package com.bioinformaticsapp;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.content.BLASTQueryLoader;
import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.Status;
import com.bioinformaticsapp.widget.BLASTQueryAdapter;

public class ListBLASTQueries extends ListActivity implements LoaderCallbacks<BLASTQuery[]> {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    	mQueryAdapter = new BLASTQueryAdapter(this, new ArrayList<BLASTQuery>());
    	setListAdapter(mQueryAdapter);
	}
	
	public Loader<BLASTQuery[]> onCreateLoader(int id, Bundle args) {
		Loader<BLASTQuery[]> cursorLoader = new BLASTQueryLoader(this, mStatus);
		
		return cursorLoader;
	}

	public void onLoadFinished(Loader<BLASTQuery[]> cursorLoader, BLASTQuery[] queries) {
		mQueryAdapter.clear();
		mQueryAdapter.addAll(queries);
		mQueryAdapter.notifyDataSetChanged();
	}

	public void onLoaderReset(Loader<BLASTQuery[]> cursorLoader) {
		
	}
	
	protected boolean deleteQuery(long id){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(this);
		BLASTQuery queryToDelete = labBook.findQueryById(id);
		mQueryAdapter.remove(queryToDelete);
		mQueryAdapter.notifyDataSetChanged();
		int numberOfQueriesDeleted = labBook.remove(id);
		
		return numberOfQueriesDeleted == 1;
	}
	
	protected BLASTQueryAdapter mQueryAdapter;
	protected Status mStatus;
	
}
