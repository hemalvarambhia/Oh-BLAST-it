package com.bioinformaticsapp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;
import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.web.EMBLEBIBLASTService;

public class QueryParametersActivity extends Activity implements OnClickListener{

	
	private Uri mQueryUri;
	
	private TextView mProgramTextView;
	
	private TextView mDatabaseTextView;
	
	private TextView mScoreTextView;
	
	private TextView mExpThresholdTextView;
	
	private TextView mSequenceTextView;
	
	private Cursor mQueryCursor;
	
	private Button mRetrieverButton;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent launchingIntent = getIntent();
		mQueryUri = launchingIntent.getData();
		long id = ContentUris.parseId(mQueryUri);
		String whereClause = BLASTJob.COLUMN_NAME_PRIMARY_KEY + "= ?";
		String[] whereArgs = new String[]{new Long(id).toString()};
		
		mQueryCursor = getContentResolver().query(mQueryUri, BLASTJob.PARENT_TABLE_FULL_PROJECTIONS, whereClause, whereArgs, null);
		
		setContentView(R.layout.query_parameters_screen);
		mProgramTextView = (TextView)findViewById(R.id.query_program_value);
		
		mDatabaseTextView = (TextView)findViewById(R.id.query_database_value);
		
		mScoreTextView = (TextView)findViewById(R.id.query_score_value);

		mExpThresholdTextView = (TextView)findViewById(R.id.query_expthreshold_value);
		
		mSequenceTextView = (TextView)findViewById(R.id.query_sequence_value);
		
		mRetrieverButton = (Button)findViewById(R.id.retrieve_results_button);
		
		mRetrieverButton.setOnClickListener(this);
		
		setupScreenWithQueryParameters();
	}

	private void setupScreenWithQueryParameters(){
		
		if(mQueryCursor.moveToFirst()){
			int programIndex = mQueryCursor.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM);
			mProgramTextView.setText(mQueryCursor.getString(programIndex));
			
			int sequenceIndex = mQueryCursor.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE);
			String entireSequence = mQueryCursor.getString(sequenceIndex);
			mSequenceTextView.setText(entireSequence);
			mSequenceTextView.setSelected(true);
			
			int statusIndex = mQueryCursor.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS);
			String statusString = mQueryCursor.getString(statusIndex);
			Status status = Status.valueOf(statusString);
			if(!status.equals(Status.FINISHED)){
				mRetrieverButton.setVisibility(View.INVISIBLE);
			}
		}
		
	}
	
	public void onClick(View view) {
		
		int jobIdentifierIndex = mQueryCursor.getColumnIndex(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID);
		String jobIdentifier = mQueryCursor.getString(jobIdentifierIndex);
		
		File blastHitsXmlFile = getFileStreamPath(jobIdentifier+".xml");
		
		//Check if the file already exists...
		if(blastHitsXmlFile.exists()){

			Intent viewResults = new Intent(QueryParametersActivity.this, ViewBLASTHitsActivity.class);
			viewResults.setData(mQueryUri);
			//...we can view the results directly without downloading
			startActivity(viewResults);
			return;
		}else{
			//we need to download them off the web so first check for a network connection
			boolean connectedToNetwork = connectedToNetwork();
			if(!connectedToNetwork){
				AlertDialog.Builder dialogBuilder = new Builder(this);
				dialogBuilder.setMessage("Error retrieving BLAST hits as there is no network connection. Please set one up");
				dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
				dialogBuilder.setTitle(R.string.app_name);
				dialogBuilder.setPositiveButton("OK", null);
				dialogBuilder.setCancelable(false);
				Dialog noNetworkDialog = dialogBuilder.create();
				noNetworkDialog.show();
				
			}else{
				
				BLASTResultsDownloader retriever = new BLASTResultsDownloader(this);
				retriever.execute(new String[]{jobIdentifier});
			
			}
			
		}
		
	}
	
	public void onPause(){
		super.onPause();
		
		if(isFinishing()){
			mQueryCursor.close();
		}
	}
	
	private boolean connectedToNetwork(){
		
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo == null){
			return false;
		}
		
		if(!activeNetworkInfo.isAvailable()){
			return false;
		}
		
		if(!activeNetworkInfo.isConnected()){
			return false;
		}
		
		return true;
		
	}
	
	private class BLASTResultsDownloader extends AsyncTask<String, Void, Boolean> {
	
		private ProgressDialog mDownloadProgressDialog;
		
		public BLASTResultsDownloader(Context context){
			mDownloadProgressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
			mDownloadProgressDialog.setMessage("Downloading Results...");
			
		}
		
		protected void onPreExecute(){
			mDownloadProgressDialog.show();
		}
		
		protected void onPostExecute(Boolean fileDownloaded){
			if(mDownloadProgressDialog.isShowing()){
				mDownloadProgressDialog.dismiss();
			}
			if(fileDownloaded){
				Intent viewResults = new Intent(QueryParametersActivity.this, ViewBLASTHitsActivity.class);
				viewResults.setData(mQueryUri);
				startActivity(viewResults);
			}else{
				AlertDialog.Builder dialogBuilder = new Builder(QueryParametersActivity.this);
				dialogBuilder.setCancelable(true);
				dialogBuilder.setMessage("Could not download your results, please check for a valid network connection and tap again.");
				dialogBuilder.setTitle(R.string.app_name);
				AlertDialog couldNotDownload = dialogBuilder.create();
				couldNotDownload.show();
			}
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			String jobIdentifier = params[0];
			
			EMBLEBIBLASTService emblBLASTService = new EMBLEBIBLASTService();
			
			String blastResultsAsXml = emblBLASTService.retrieveBLASTResults(jobIdentifier, "xml");
			blastResultsAsXml = cleanupXmlOutput(blastResultsAsXml);
			FileOutputStream resultsFile = null;
			try {
				resultsFile = openFileOutput(jobIdentifier+".xml", MODE_PRIVATE);
				PrintWriter writer = new PrintWriter(new BufferedOutputStream(resultsFile));
				writer.println(blastResultsAsXml);
				writer.flush();
				writer.close();
				
			} catch (FileNotFoundException e) {
				//Do nothing as we check only if results file was created on disk
			}
			
			return resultsFile != null;
		}
		
		private String cleanupXmlOutput(String xmlBlastoutput){
			
			return xmlBlastoutput.replaceAll("<CAN>", "<![CDATA[<CAN>]]>");
			
		}
		
	}

}
