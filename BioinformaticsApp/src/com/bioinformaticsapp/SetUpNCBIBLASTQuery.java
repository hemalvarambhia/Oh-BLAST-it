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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bioinformaticsapp.text.DNASymbolFilter;

public class SetUpNCBIBLASTQuery extends SetUpBLASTQuery {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ncbi_blast_query_form);
		
		programSpinner = (Spinner)findViewById(R.id.ncbi_program_spinner);
		sequenceEditor = (EditText)findViewById(R.id.ncbi_sequence_edittext);
		sequenceEditor.setFilters(new InputFilter[]{ new DNASymbolFilter() });
		databaseSpinner = (Spinner)findViewById(R.id.ncbi_database_spinner);
		wordSizeSpinner = (Spinner)findViewById(R.id.ncbi_wordsize_spinner);
		expThresholdEditText = (EditText)findViewById(R.id.ncbi_exp_threshold_edittext);
		matchMismatchScoreSpinner = (Spinner)findViewById(R.id.ncbi_match_mismatch_spinner);
		progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
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
			Toast saved = Toast.makeText(this, R.string.blastquerysaved_text, Toast.LENGTH_LONG);
			saved.show();
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
		
		String database = query.getSearchParameter("database").getValue();
		String[] databaseOptions = appResources.getStringArray(R.array.ncbi_database_options);
		List<String> listOfNCBIDatabases = Arrays.asList(databaseOptions);
		int databasePosition = listOfNCBIDatabases.indexOf(database);
		databaseSpinner.setSelection(databasePosition);
		
		String wordSize = query.getSearchParameter("word_size").getValue();
		String[] wordSizeOptions = appResources.getStringArray(R.array.ncbi_word_size_options);
		List<String> listOfWordSizes = Arrays.asList(wordSizeOptions);
		int wordSizePosition = listOfWordSizes.indexOf(wordSize);
		wordSizeSpinner.setSelection(wordSizePosition);
		
		String expThreshold = query.getSearchParameter("exp_threshold").getValue();
		expThresholdEditText.setText(expThreshold);
		
		String matchMisMatchScore = query.getSearchParameter("match_mismatch_score").getValue();
		String[] matchMisMatchScoreOptions = appResources.getStringArray(R.array.ncbi_match_mismatch_score_options);
		List<String> matchMisMatchScores = Arrays.asList(matchMisMatchScoreOptions);
		int matchMismatchScorePosition = matchMisMatchScores.indexOf(matchMisMatchScore);
		matchMismatchScoreSpinner.setSelection(matchMismatchScorePosition);
		
		if(query.getSequence() == null || query.getSequence().isEmpty()){
			sequenceEditor.setHint("Enter a sequence");
		}else{
			sequenceEditor.setText(query.getSequence());
		}
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
		
		databaseSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("database", databaseSpinner.getSelectedItem().toString());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		matchMismatchScoreSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("match_mismatch_score", matchMismatchScoreSpinner.getSelectedItem().toString());
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		wordSizeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				query.setSearchParameter("word_size", wordSizeSpinner.getSelectedItem().toString());
				
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
		
		expThresholdEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			public void afterTextChanged(Editable s) {
				query.setSearchParameter("exp_threshold", s.toString());			
			}
		});
	}

	private EditText sequenceEditor;
	private Spinner programSpinner;
	private Spinner databaseSpinner;
	private Spinner wordSizeSpinner;
	private EditText expThresholdEditText;
	private Spinner matchMismatchScoreSpinner;
}
