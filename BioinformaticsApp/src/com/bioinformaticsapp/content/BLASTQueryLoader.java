package com.bioinformaticsapp.content;

import java.util.ArrayList;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;

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
		
		List<BLASTQuery> queries = new ArrayList<BLASTQuery>();
		
		switch(mStatusOfQuery){
		case SUBMITTED:
			queries = queryController.getSubmittedBLASTQueries();
			break;
		default:
			queries = queryController.findBLASTQueriesByStatus(mStatusOfQuery);
			break;
		}
		
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
