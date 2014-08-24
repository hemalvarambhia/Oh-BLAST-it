package com.bioinformaticsapp.content;

import java.util.ArrayList;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.persistence.BLASTQueryController;

public class BLASTQueryLoader extends AsyncTaskLoader<BLASTQuery[]> {

	public BLASTQueryLoader(Context context) {
		super(context);
		
	}
	
	public BLASTQueryLoader(Context context, BLASTQuery.Status status){
		super(context);
		mStatusOfQuery = status;
	}

	@Override
	public BLASTQuery[] loadInBackground() {
		BLASTQueryLabBook queryController = new BLASTQueryLabBook(getContext());
		List<BLASTQuery> queries = queryController.findBLASTQueriesByStatus(mStatusOfQuery);
		
		return queries.toArray(new BLASTQuery[queries.size()]);
	}
	
	/* (non-Javadoc)
	 * @see android.content.Loader#onStartLoading()
	 */
	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		forceLoad();
	}

	private BLASTQuery.Status mStatusOfQuery;

}
