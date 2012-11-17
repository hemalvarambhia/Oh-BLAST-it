package com.bioinformaticsapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQueryValidator;
import com.bioinformaticsapp.models.OptionalParameter;
import com.bioinformaticsapp.text.DNASymbolFilter;

public class EMBLEBISetUpQueryActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	private EditText mSequenceEditor;
	
	private Spinner mProgramSpinner;
	
	private Spinner mDatabaseSpinner;
	
	private Spinner mScoreSpinner;
	
	private Spinner mExpThresholdSpinner;
	
	private ProgressDialog mProgressDialog;
	
	private EditText mEmailEditor;
	
	private BLASTQueryController controller;
	private OptionalParameterController optionalParametersController; 
	private BLASTQuery draftQuery;
	
	
	@SuppressWarnings("unused")
	private static final String TAG = "SetupBLASTQueryActivity";

	
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
			
			EMBLEBIBLASTQueryValidator sender = new EMBLEBIBLASTQueryValidator();
			
			sender.execute(new BLASTQuery[]{draftQuery});
			
		}
		//...and exit	
		break;
		
		case R.id.save_query: {
			//Store into our database or update
			storeQueryInDatabase();
			
			//Create a toast message to tell the user the query was saved
			Toast querySavedMessage = Toast.makeText(EMBLEBISetUpQueryActivity.this, R.string.blastquerysaved_text, Toast.LENGTH_LONG);
			
			//Now show it
			querySavedMessage.show();
		
		}
		
		break;
		
		case R.id.settings: {
			Intent settings = new Intent(this, AppPreferences.class);
			settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(settings);
			break;
		}
			
		default:
			break;
		
		}
		
		return true;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.embl_ebi_blast_query_form);
		
		mSequenceEditor = (EditText)findViewById(R.id.embl_sequence_editor);
		mSequenceEditor.setFilters(new InputFilter[]{ new DNASymbolFilter() });
		mProgramSpinner = (Spinner)findViewById(R.id.blastqueryentry_program_spinner);
		mDatabaseSpinner = (Spinner)findViewById(R.id.blastqueryentry_database_spinner);
		mScoreSpinner = (Spinner)findViewById(R.id.blastqueryentry_score_spinner);
		mExpThresholdSpinner = (Spinner)findViewById(R.id.blastqueryentry_expthreshold_spinner);
		mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mEmailEditor = (EditText)findViewById(R.id.embl_send_to_email);
		attachListeners();
		Intent launchingIntent = getIntent();
		
		controller = new BLASTQueryController(this);
		optionalParametersController = new OptionalParameterController(this);
		
		draftQuery = (BLASTQuery)launchingIntent.getSerializableExtra("query");
		
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		
		super.onPause();
		if(mProgressDialog !=null){
			if(mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}
		}
		
		switch(draftQuery.getStatus()){
		case DRAFT:
			storeQueryInDatabase();
			Toast toast = Toast.makeText(this, R.string.blastquerysaved_text, Toast.LENGTH_LONG);
			toast.show();
			break;
		default:
			break;
		}
		
		if(isFinishing()){
			optionalParametersController.close();
			controller.close();
		}
		
	}
	
	protected void onResume(){
		
		super.onResume();
		
		setUpScreenWithInitialValues();
		
	}
	
	private void setUpScreenWithInitialValues(){
		Resources appResources = getResources();
		
		String program = draftQuery.getBLASTProgram();
		String[] blastProgramsOptions = appResources.getStringArray(R.array.blast_program_options);
		List<String> listOfBLASTPrograms = Arrays.asList(blastProgramsOptions);
		int programPosition = listOfBLASTPrograms.indexOf(program);
		mProgramSpinner.setSelection(programPosition);
		
		OptionalParameter database = draftQuery.getSearchParameter("database");
		String[] databaseOptions = appResources.getStringArray(R.array.blast_database_options);
		List<String> listOfDatabases = Arrays.asList(databaseOptions);
		int databasePosition = listOfDatabases.indexOf(database.getValue());
		mDatabaseSpinner.setSelection(databasePosition);
		
		OptionalParameter expThreshold = draftQuery.getSearchParameter("exp_threshold");
		String[] expThresholdOptions = appResources.getStringArray(R.array.exp_threshold_options);
		List<String> listOfExpThresholds = Arrays.asList(expThresholdOptions);
		int expThresholdPosition = listOfExpThresholds.indexOf(expThreshold.getValue());
		mExpThresholdSpinner.setSelection(expThresholdPosition);
		
		OptionalParameter score = draftQuery.getSearchParameter("score");
		String[] scoreOptions = appResources.getStringArray(R.array.blastqueryentry_score_options);
		List<String> listOfScores = Arrays.asList(scoreOptions);
		int scorePosition = listOfScores.indexOf(score.getValue());
		mScoreSpinner.setSelection(scorePosition);
		
		if(draftQuery.getSequence() == null || draftQuery.getSequence().isEmpty()){
			mSequenceEditor.setHint("Enter a sequence");
		}else{
			mSequenceEditor.setText(draftQuery.getSequence());
		}
		
		OptionalParameter email = draftQuery.getSearchParameter("email");
		
		if(email.getValue() == null || email.getValue().isEmpty()){
			
			mEmailEditor.setHint("Enter an e-mail address");
			
		}else{
			
			mEmailEditor.setText(email.getValue());
		
		}
		
	}
	
	private void attachListeners(){
		
		
		mProgramSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				draftQuery.setBLASTProgram(mProgramSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
			
		});
		mSequenceEditor.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				
			}
			
			public void afterTextChanged(Editable s) {
				
				draftQuery.setSequence(s.toString());
				
			}
		});
		
		mEmailEditor.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Do nothing
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Do nothing
				
			}
			
			public void afterTextChanged(Editable s) {
				draftQuery.setSearchParameter("email", s.toString());
				
			}
		});
		
		mDatabaseSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				draftQuery.setSearchParameter("database", mDatabaseSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		mScoreSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				draftQuery.setSearchParameter("score", mScoreSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
				
			}
			
		});
		
		mExpThresholdSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				draftQuery.setSearchParameter("exp_threshold", mExpThresholdSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
			
		});
	}
	
	private void storeQueryInDatabase(){
		
		if(draftQuery.getPrimaryKey() == null){
			//Add the job to our database of BLAST queries...
			long primaryKey = controller.save(draftQuery);
			draftQuery.setPrimaryKeyId(primaryKey);
			
			//Save the parameters:
			List<OptionalParameter> parameters = new ArrayList<OptionalParameter>();
			for(OptionalParameter parameter: draftQuery.getAllParameters()){
				parameter.setBlastQueryId(draftQuery.getPrimaryKey());
				long parameterPrimaryKey = optionalParametersController.save(parameter);
				parameter.setPrimaryKey(parameterPrimaryKey);
				parameters.add(parameter);
				
			}
			
			draftQuery.updateAllParameters(parameters);
			
		}else{ 
			//...or date the columns for the specified row:
			controller.update(draftQuery.getPrimaryKey(), draftQuery);
			for(OptionalParameter parameter: draftQuery.getAllParameters()){
				if(parameter.getBlastQueryId() == null){
					parameter.setBlastQueryId(draftQuery.getPrimaryKey());
				}
				if(parameter.getPrimaryKey() == null){
					
					optionalParametersController.save(parameter);
				
				}else{
					optionalParametersController.update(parameter.getPrimaryKey(), parameter);
				}
			}
			
		}
		
	}
		
	private class EMBLEBIBLASTQueryValidator extends BLASTQueryValidator {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			mProgressDialog.setTitle("Validating BLAST query");
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.show();
			
		}
		
		protected void onPostExecute(Boolean isValid){
			
			if(isValid.booleanValue()){
				
				//The query is ready to be sent
				draftQuery.setStatus(BLASTQuery.Status.PENDING);
				storeQueryInDatabase();
				setResult(DraftBLASTQueriesActivity.READY_TO_SEND);
				Toast t = Toast.makeText(EMBLEBISetUpQueryActivity.this, "Sending query", Toast.LENGTH_LONG);
				t.show();
				//Start the polling service
				finish();
			}else{
				Toast t = Toast.makeText(EMBLEBISetUpQueryActivity.this, "Query could not be sent as it is invalid", Toast.LENGTH_LONG);
				t.show();
				
			}

		}
		
	}
		
}
