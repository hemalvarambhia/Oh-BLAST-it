package com.bioinformaticsapp.content;

import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.models.BLASTQuery;

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
		BLASTQueryController queryController = new BLASTQueryController(getContext());
		List<BLASTQuery> queries = queryController.findBLASTQueriesByStatus(mStatusOfQuery);
		queryController.close();
		
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