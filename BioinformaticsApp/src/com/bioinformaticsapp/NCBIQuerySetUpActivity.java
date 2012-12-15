package com.bioinformaticsapp;

import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQueryValidator;
import com.bioinformaticsapp.text.DNASymbolFilter;

public class NCBIQuerySetUpActivity extends SetUpBLASTQueryActivity {

	private EditText mSequenceEditor;
	private Spinner mProgramSpinner;
	private Spinner mDatabaseSpinner;
	private Spinner mWordSizeSpinner;
	private EditText mExpThresholdEditText;
	private Spinner mMatchMismatchScoreSpinner;
	private ProgressDialog mProgressDialog;
	
	private static final String TAG = "NCBIQuerySetUpActivity";
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ncbi_blast_query_form);
		
		mProgramSpinner = (Spinner)findViewById(R.id.ncbi_program_spinner);
		mSequenceEditor = (EditText)findViewById(R.id.ncbi_sequence_edittext);
		mSequenceEditor.setFilters(new InputFilter[]{ new DNASymbolFilter() });
		mDatabaseSpinner = (Spinner)findViewById(R.id.ncbi_database_spinner);
		mWordSizeSpinner = (Spinner)findViewById(R.id.ncbi_wordsize_spinner);
		mExpThresholdEditText = (EditText)findViewById(R.id.ncbi_exp_threshold_edittext);
		mMatchMismatchScoreSpinner = (Spinner)findViewById(R.id.ncbi_match_mismatch_spinner);
		mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		attachListeners();
		controller = new BLASTQueryController(this);
		optionalParametersController = new OptionalParameterController(this);
		
		query = (BLASTQuery)getIntent().getSerializableExtra("query");
		
		
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
		switch(query.getStatus()){
		case DRAFT:
			storeQueryInDatabase();
			Toast saved = Toast.makeText(this, R.string.blastquerysaved_text, Toast.LENGTH_LONG);
			saved.show();
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
		
		String program = query.getBLASTProgram();
		String[] blastProgramsOptions = appResources.getStringArray(R.array.blast_program_options);
		List<String> listOfBLASTPrograms = Arrays.asList(blastProgramsOptions);
		int programPosition = listOfBLASTPrograms.indexOf(program);
		mProgramSpinner.setSelection(programPosition);
		
		String database = query.getSearchParameter("database").getValue();
		String[] databaseOptions = appResources.getStringArray(R.array.ncbi_database_options);
		List<String> listOfNCBIDatabases = Arrays.asList(databaseOptions);
		int databasePosition = listOfNCBIDatabases.indexOf(database);
		mDatabaseSpinner.setSelection(databasePosition);
		
		String wordSize = query.getSearchParameter("word_size").getValue();
		String[] wordSizeOptions = appResources.getStringArray(R.array.ncbi_word_size_options);
		List<String> listOfWordSizes = Arrays.asList(wordSizeOptions);
		int wordSizePosition = listOfWordSizes.indexOf(wordSize);
		mWordSizeSpinner.setSelection(wordSizePosition);
		
		String expThreshold = query.getSearchParameter("exp_threshold").getValue();
		mExpThresholdEditText.setText(expThreshold);
		
		String matchMisMatchScore = query.getSearchParameter("match_mismatch_score").getValue();
		String[] matchMisMatchScoreOptions = appResources.getStringArray(R.array.ncbi_match_mismatch_score_options);
		List<String> matchMisMatchScores = Arrays.asList(matchMisMatchScoreOptions);
		int matchMismatchScorePosition = matchMisMatchScores.indexOf(matchMisMatchScore);
		mMatchMismatchScoreSpinner.setSelection(matchMismatchScorePosition);
		
		if(query.getSequence() == null || query.getSequence().isEmpty()){
			
			mSequenceEditor.setHint("Enter a sequence");
		
		}else{
			
			mSequenceEditor.setText(query.getSequence());
		
		}
		
	}
	
	private void attachListeners(){
		mProgramSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setBLASTProgram(mProgramSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				//Do nothing
			}
			
		});
		
		mDatabaseSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("database", mDatabaseSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				//Do nothing
			}
			
		});
		
		mMatchMismatchScoreSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("match_mismatch_score", mMatchMismatchScoreSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
				
			}
			
		});
		
		mWordSizeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("word_size", mWordSizeSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
				
			}
			
		});
		
		mSequenceEditor.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//Do nothing
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {				
				//Do nothing
			}
			
			public void afterTextChanged(Editable s) {
				query.setSequence(s.toString());
			}
		});
		
		mExpThresholdEditText.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Do nothing
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Do nothing
				
			}
			
			public void afterTextChanged(Editable s) {
				
				query.setSearchParameter("exp_threshold", s.toString());
				
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int selectedMenuItemId = item.getItemId();
		
		switch(selectedMenuItemId){
		
		case R.id.save_query:
					
			storeQueryInDatabase();
			
			Toast querySavedMessage = Toast.makeText(this, R.string.blastquerysaved_text, Toast.LENGTH_LONG);
			
			//Now show it
			querySavedMessage.show();
			break;
		
		case R.id.send_query:
			NCBIBLASTQueryValidator sender = new NCBIBLASTQueryValidator();
			sender.execute(query);
			break;
			
		default:
			break;
		
		}
			
		return true;
	}

	

	private class NCBIBLASTQueryValidator extends BLASTQueryValidator {

		@Override
		protected void onPostExecute(Boolean isValid) {
			
			if(isValid.booleanValue()){
				query.setStatus(BLASTQuery.Status.PENDING);
				storeQueryInDatabase();
				setResult(DraftBLASTQueriesActivity.READY_TO_SEND);
				Toast sentMessage = Toast.makeText(NCBIQuerySetUpActivity.this, "Sending query", Toast.LENGTH_LONG);
				sentMessage.show();
				finish();
			}else{
				Toast message = Toast.makeText(NCBIQuerySetUpActivity.this, "Query could not be sent as it is invalid", Toast.LENGTH_LONG);
				message.show();
			}
			
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {			
			super.onPreExecute();
			mProgressDialog.setTitle("Validating BLAST query");
			mProgressDialog.setMessage("Please wait");
			mProgressDialog.show();			
		}
		
		
	}
	
}
