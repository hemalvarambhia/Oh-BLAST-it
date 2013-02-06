package com.bioinformaticsapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bioinformaticsapp.io.BLASTHitsLoadingTask;
import com.bioinformaticsapp.models.BLASTQuery;

public class ViewBLASTHitsActivity extends ListActivity {

	private static final String TAG = "ViewBLASTHitsActivity";

	private FileInputStream mBLASTHitsFile;
	
	private List<Map<String, String>> mBlastHits;
	
	private BLASTQuery mFinishedQuery;
	
	private TextView mNoHitsMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.blast_hits_list_view);
		mNoHitsMessage = (TextView)getListView().getEmptyView();
		mNoHitsMessage.setText("Loading...");
		Intent launchingIntent = getIntent();
		
		try {
			
			mFinishedQuery = (BLASTQuery)launchingIntent.getSerializableExtra("query");
			
			mBLASTHitsFile = openFileInput(mFinishedQuery.getJobIdentifier()+".xml");
		
		} catch (FileNotFoundException e) {
			
			Log.e(TAG, "Could not find the file for query with identifier "+mFinishedQuery.getJobIdentifier());
		
		}
				
	}
	
	
	public void onResume(){
		super.onResume();
		
		
		
		
		BLASTHitsLoader hitsLoader = new BLASTHitsLoader();
		
		hitsLoader.execute(mBLASTHitsFile);
		
	}
	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		
		loadSelectedHit(position);
		
	}
	
	private void loadSelectedHit(int selectedIndex){
		/**
		 * The task of retrieving a hit from the list in farmed to a separate thread 
		 * in case it takes a while to retrieve
		 */
		AsyncTask<Integer, Void, String> retriever = new AsyncTask<Integer, Void, String>(){


			@Override
			protected String doInBackground(Integer... params) {
				Map<String, String> selectedBlastHit = mBlastHits.get(params[0]); 
				String accessionNumber = selectedBlastHit.get("accessionNumber");
				
				return accessionNumber;
			}

			@Override
			protected void onPostExecute(String accessionNumber) {

				Intent taxonomyActivity = new Intent(ViewBLASTHitsActivity.this, ViewTaxonomyActivity.class);
				taxonomyActivity.putExtra("accessionNumber", accessionNumber);
			
				startActivity(taxonomyActivity);
			}
			
		};
		
		retriever = retriever.execute(selectedIndex);
		
	}

	
	private class BLASTHitsLoader extends BLASTHitsLoadingTask {

		private ProgressDialog loadingDialog;
		
		public BLASTHitsLoader(){
			super(mFinishedQuery.getVendorID());
			loadingDialog = new ProgressDialog(ViewBLASTHitsActivity.this, ProgressDialog.STYLE_SPINNER);
			
		}
		
		protected void onPreExecute(){
			loadingDialog.setMessage("Loading BLAST hits...");
			if(!loadingDialog.isShowing()){
				loadingDialog.show();
			}
		}
		
		protected void onPostExecute(List<Map<String, String>> result){
			
			if(loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			if(!result.isEmpty()){
				mBlastHits = result;
				
				int[] viewIds = new int[]{ R.id.accessionNumberTextView, R.id.organismTextView };
				
				String[] mapKeys = new String[]{"accessionNumber", "description"};
				
				SimpleAdapter arrayAdapter = new SimpleAdapter(ViewBLASTHitsActivity.this, mBlastHits, R.layout.blast_hit_item, mapKeys, viewIds);
				
				setListAdapter(arrayAdapter);
			}else{
				mNoHitsMessage.setText(R.string.no_hits_message);
			}
		}
		
	}

}
