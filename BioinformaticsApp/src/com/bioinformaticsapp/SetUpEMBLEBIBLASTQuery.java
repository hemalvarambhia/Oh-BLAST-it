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

import com.bioinformaticsapp.domain.SearchParameter;
import com.bioinformaticsapp.text.DNASymbolFilter;

public class SetUpEMBLEBIBLASTQuery extends SetUpBLASTQuery {
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.embl_ebi_blast_query_form);
		
		if(isLegacyQuery()){
			Resources resources = getResources();
			String[] matchMismatchScores = resources.getStringArray(R.array.ebi_match_mismatch_score_options);
			query.setJobIdentifier(null);
			query.setSearchParameter("match_mismatch_score", matchMismatchScores[0]);
			query.setSearchParameter("email", "");
		}
		
		sequenceEditor = (EditText)findViewById(R.id.embl_sequence_editor);
		sequenceEditor.setFilters(new InputFilter[]{ new DNASymbolFilter() });
		programSpinner = (Spinner)findViewById(R.id.blastqueryentry_program_spinner);
		databaseSpinner = (Spinner)findViewById(R.id.blastqueryentry_database_spinner);
		scoreSpinner = (Spinner)findViewById(R.id.blastqueryentry_score_spinner);
		expThresholdSpinner = (Spinner)findViewById(R.id.blastqueryentry_expthreshold_spinner);
		matchMismatchSpinner = (Spinner)findViewById(R.id.ebi_match_mismatch_score_spinner);
		progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		emailEditor = (EditText)findViewById(R.id.embl_send_to_email);
		attachListeners();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(progressDialog !=null){
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
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
	}
	
	protected void setUpScreenWithInitialValues(){
		Resources appResources = getResources();
		
		String program = query.getBLASTProgram();
		String[] blastProgramsOptions = appResources.getStringArray(R.array.blast_program_options);
		List<String> listOfBLASTPrograms = Arrays.asList(blastProgramsOptions);
		int programPosition = listOfBLASTPrograms.indexOf(program);
		programSpinner.setSelection(programPosition);
		
		SearchParameter database = query.getSearchParameter("database");
		String[] databaseOptions = appResources.getStringArray(R.array.blast_database_options);
		List<String> listOfDatabases = Arrays.asList(databaseOptions);
		int databasePosition = listOfDatabases.indexOf(database.getValue());
		databaseSpinner.setSelection(databasePosition);
		
		SearchParameter expThreshold = query.getSearchParameter("exp_threshold");
		String[] expThresholdOptions = appResources.getStringArray(R.array.exp_threshold_options);
		List<String> listOfExpThresholds = Arrays.asList(expThresholdOptions);
		int expThresholdPosition = listOfExpThresholds.indexOf(expThreshold.getValue());
		expThresholdSpinner.setSelection(expThresholdPosition);
		
		SearchParameter score = query.getSearchParameter("score");
		String[] scoreOptions = appResources.getStringArray(R.array.blastqueryentry_score_options);
		List<String> listOfScores = Arrays.asList(scoreOptions);
		int scorePosition = listOfScores.indexOf(score.getValue());
		scoreSpinner.setSelection(scorePosition);
		
		SearchParameter matchMismatchScore = query.getSearchParameter("match_mismatch_score");
		String[] matchMismatchScoreOptions = appResources.getStringArray(R.array.ebi_match_mismatch_score_options);
		List<String> listOfMatchMismatchScores = Arrays.asList(matchMismatchScoreOptions);
		int matchMismatchScorePosition = listOfMatchMismatchScores.indexOf(matchMismatchScore.getValue());
		matchMismatchSpinner.setSelection(matchMismatchScorePosition);
		
		if(query.getSequence() == null || query.getSequence().isEmpty()){
			sequenceEditor.setHint("Enter a sequence");
		}else{
			sequenceEditor.setText(query.getSequence());
		}
		
		SearchParameter email = query.getSearchParameter("email");
		if(email.getValue() == null || email.getValue().isEmpty()){
			emailEditor.setHint("Enter an e-mail address");		
		}else{
			emailEditor.setText(email.getValue());
		}
		
	}
	
	private boolean isLegacyQuery(){
		SearchParameter matchMismatchScore = query.getSearchParameter("match_mismatch_score");
		SearchParameter email = query.getSearchParameter("email");
		return matchMismatchScore == null && email == null;		
	}
	
	private void attachListeners(){
		programSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setBLASTProgram(programSpinner.getSelectedItem().toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		sequenceEditor.addTextChangedListener(new TextWatcher() {	
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
								
			}
			
			public void afterTextChanged(Editable s) {
				query.setSequence(s.toString());
			}
		});
		
		emailEditor.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			public void afterTextChanged(Editable s) {
				query.setSearchParameter("email", s.toString());
			}
		});
		
		databaseSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("database", databaseSpinner.getSelectedItem().toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		scoreSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("score", scoreSpinner.getSelectedItem().toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
								
			}
		});
		
		expThresholdSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("exp_threshold", expThresholdSpinner.getSelectedItem().toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		matchMismatchSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("match_mismatch_score", matchMismatchSpinner.getSelectedItem().toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}		
		});
	}
	
	private EditText sequenceEditor;
	private Spinner programSpinner;
	private Spinner databaseSpinner;
	private Spinner scoreSpinner;
	private Spinner expThresholdSpinner;
	private EditText emailEditor;
	private Spinner matchMismatchSpinner;
}
