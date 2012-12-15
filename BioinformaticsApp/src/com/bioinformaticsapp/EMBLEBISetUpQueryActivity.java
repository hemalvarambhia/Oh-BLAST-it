package com.bioinformaticsapp;

import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bioinformaticsapp.models.OptionalParameter;
import com.bioinformaticsapp.text.DNASymbolFilter;

/**
 * This activity allows a user to set up a BLAST query for the EMBL EBI BLAST
 * web service
 * @author Hemal N Varambhia
 *
 */
public class EMBLEBISetUpQueryActivity extends SetUpBLASTQueryActivity {

	private EditText mSequenceEditor;
	
	private Spinner mProgramSpinner;
	
	private Spinner mDatabaseSpinner;
	
	private Spinner mScoreSpinner;
	
	private Spinner mExpThresholdSpinner;
	
	private EditText mEmailEditor;
	
	@SuppressWarnings("unused")
	private static final String TAG = "SetupBLASTQueryActivity";

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
	
	protected void setUpScreenWithInitialValues(){
		Resources appResources = getResources();
		
		String program = query.getBLASTProgram();
		String[] blastProgramsOptions = appResources.getStringArray(R.array.blast_program_options);
		List<String> listOfBLASTPrograms = Arrays.asList(blastProgramsOptions);
		int programPosition = listOfBLASTPrograms.indexOf(program);
		mProgramSpinner.setSelection(programPosition);
		
		OptionalParameter database = query.getSearchParameter("database");
		String[] databaseOptions = appResources.getStringArray(R.array.blast_database_options);
		List<String> listOfDatabases = Arrays.asList(databaseOptions);
		int databasePosition = listOfDatabases.indexOf(database.getValue());
		mDatabaseSpinner.setSelection(databasePosition);
		
		OptionalParameter expThreshold = query.getSearchParameter("exp_threshold");
		String[] expThresholdOptions = appResources.getStringArray(R.array.exp_threshold_options);
		List<String> listOfExpThresholds = Arrays.asList(expThresholdOptions);
		int expThresholdPosition = listOfExpThresholds.indexOf(expThreshold.getValue());
		mExpThresholdSpinner.setSelection(expThresholdPosition);
		
		OptionalParameter score = query.getSearchParameter("score");
		String[] scoreOptions = appResources.getStringArray(R.array.blastqueryentry_score_options);
		List<String> listOfScores = Arrays.asList(scoreOptions);
		int scorePosition = listOfScores.indexOf(score.getValue());
		mScoreSpinner.setSelection(scorePosition);
		
		if(query.getSequence() == null || query.getSequence().isEmpty()){
			mSequenceEditor.setHint("Enter a sequence");
		}else{
			mSequenceEditor.setText(query.getSequence());
		}
		
		OptionalParameter email = query.getSearchParameter("email");
		
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
				query.setBLASTProgram(mProgramSpinner.getSelectedItem().toString());
				
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
				
				query.setSequence(s.toString());
				
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
				query.setSearchParameter("email", s.toString());
				
			}
		});
		
		mDatabaseSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("database", mDatabaseSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		mScoreSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("score", mScoreSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
								
			}
			
		});
		
		mExpThresholdSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("exp_threshold", mExpThresholdSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
			
		});
	}
		
}
