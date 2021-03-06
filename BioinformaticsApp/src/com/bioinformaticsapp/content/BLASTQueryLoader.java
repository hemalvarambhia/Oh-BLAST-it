package com.bioinformaticsapp.content;

import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.bioinformaticsapp.domain.BLASTQuery;

public class BLASTQueryLoader extends AsyncTaskLoader<BLASTQuery[]> {

	public BLASTQueryLoader(Context context) {
		super(context);
	}
	
	public BLASTQueryLoader(Context context, BLASTQuery.Status status){
		super(context);
		queryStatus = status;
	}

	@Override
	public BLASTQuery[] loadInBackground() {
		BLASTQueryLabBook queryController = new BLASTQueryLabBook(getContext());
		List<BLASTQuery> queries = queryController.findBLASTQueriesByStatus(queryStatus);
		
		return queries.toArray(new BLASTQuery[queries.size()]);
	}
	
	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		forceLoad();
	}

	private BLASTQuery.Status queryStatus;
}
