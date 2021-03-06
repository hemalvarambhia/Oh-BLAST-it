package com.bioinformaticsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bioinformaticsapp.content.BLASTQueryLabBook;
import com.bioinformaticsapp.domain.BLASTQuery;

public abstract class SetUpBLASTQuery extends Activity {

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent launchingIntent = getIntent();
		query = (BLASTQuery)launchingIntent.getSerializableExtra("query");
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.blastqueryentry_menu, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int itemId = item.getItemId();
		
		switch(itemId){
		
		case R.id.send_query: {
			BLASTQueryValidation validator = new BLASTQueryValidation();
			validator.execute(new BLASTQuery[]{query});
		}
		//...and exit	
		break;
		
		case R.id.save_query: {
			storeQueryInDatabase();
			Toast querySavedMessage = Toast.makeText(this, R.string.blastquerysaved_text, Toast.LENGTH_LONG);
			querySavedMessage.show();
		}
		
		break;
		
		case R.id.settings: {
			storeQueryInDatabase();
			Intent settings = new Intent(this, AppPreferences.class);
			startActivity(settings);
		}
		break;
		
		default:
			break;
		}
		
		return true;
	}

	protected void onResume(){
		super.onResume();
		setUpScreenWithInitialValues();
	}
	
	protected abstract void setUpScreenWithInitialValues();
	
	protected void storeQueryInDatabase(){
		BLASTQueryLabBook labBook = new BLASTQueryLabBook(this);
		query = labBook.save(query);
	}
	
	protected class BLASTQueryValidation extends AsyncTask<BLASTQuery, Void, Boolean> {

		@Override
		protected Boolean doInBackground(BLASTQuery... query) {
			boolean isValid = query[0].isValid();
			return isValid;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			progressDialog.setTitle("Validating BLAST query");
			progressDialog.setMessage("Please wait...");
			progressDialog.show();
		}
		
		protected void onPostExecute(Boolean isValid){
			if(isValid.booleanValue()){
				//The query is ready to be sent
				query.setStatus(BLASTQuery.Status.PENDING);
				storeQueryInDatabase();
				setResult(ListDraftBLASTQueries.READY_TO_SEND);
				Toast t = Toast.makeText(SetUpBLASTQuery.this, "Sending query", Toast.LENGTH_LONG);
				t.show();
				//Start the polling service
				finish();
			}else{
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				Toast t = Toast.makeText(SetUpBLASTQuery.this, "Query could not be sent as it is invalid", Toast.LENGTH_LONG);
				t.show();
			}
		}
	}
	
	protected ProgressDialog progressDialog;
	
	protected BLASTQuery query;	
}
