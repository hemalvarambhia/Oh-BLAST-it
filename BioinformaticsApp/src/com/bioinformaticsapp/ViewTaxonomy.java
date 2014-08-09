package com.bioinformaticsapp;

import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.bioinformaticsapp.blastservices.EntryFetcher;

public class ViewTaxonomy extends Activity {
	
	private TextView mScientificNameTextView;
	
	private TextView mLineageTextView;
	
	private TextView mTaxIdTextView;
	
	private String mAccessionNumber;
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.view_taxonomy_info);
		
		mAccessionNumber = getIntent().getStringExtra("accessionNumber");
		
		setTitle(mAccessionNumber);
		
		mScientificNameTextView = (TextView)findViewById(R.id.blastquerystats_scientificName_textview);
		
		mLineageTextView = (TextView)findViewById(R.id.blastquerystats_lineage_textview);
		
		mTaxIdTextView = (TextView)findViewById(R.id.blastquerystats_taxId);
		
	}

	protected void onResume(){
		super.onResume();
		setupScreenWithParameters();
		
	}
	
	private void setupScreenWithParameters(){
		ProgressDialog loadingProgress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		EntryFetcherTask task = new EntryFetcherTask(loadingProgress);
		Void[] noArgs = new Void[]{};
		task.execute(noArgs);
		
	}
	
	public class EntryFetcherTask extends AsyncTask<Void, Void, Map<String, String>> {

		private ProgressDialog loadingDialog;
		
		public EntryFetcherTask(ProgressDialog progressDialog){
			loadingDialog = progressDialog;
		}
		
		
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			loadingDialog.setMessage("Rendering Information...");
			loadingDialog.show();
			
		}



		@Override
		protected Map<String, String> doInBackground(Void... arg0) {
			EntryFetcher fetcher = new EntryFetcher(mAccessionNumber);
			Map<String, String> organismInformation = fetcher.getOrganism();
			return organismInformation;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Map<String, String> organismInfo) {
			if(loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			if(organismInfo != null){
				mScientificNameTextView.setText(organismInfo.get("scientificName"));
				mLineageTextView.setText(organismInfo.get("lineage"));
				mTaxIdTextView.setText(organismInfo.get("taxId"));
				
			}else{
				mScientificNameTextView.setText("Unable to retrieve scientific information. Are you connected to the web?");
				mLineageTextView.setText("Unable to retrieve organism lineage. Are you connected to the web?");
				mTaxIdTextView.setText("Unable to retrieve taxonomy ID. Are you connected to the web?");
			
			}
		}
	}
}
