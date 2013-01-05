package com.bioinformaticsapp.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bioinformaticsapp.R;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;

public class BLASTQueryParametersDialog extends DialogFragment {

	
	
	private static final String TAG = "BLASTQueryParametersDialog";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mBLASTquery = (BLASTQuery)getArguments().getSerializable("query");
		setCancelable(true);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(mBLASTquery.getJobIdentifier() == null){
			getDialog().setTitle("BLAST Query Parameters");
		}else{
			getDialog().setTitle(mBLASTquery.getJobIdentifier());
		}
		
		View dialogView = null;
		switch(mBLASTquery.getVendorID()){
		case BLASTVendor.EMBL_EBI:
			dialogView = inflater.inflate(R.layout.embl_parameters_dialog_layout, container);
			break;
		case BLASTVendor.NCBI:
			dialogView = inflater.inflate(R.layout.ncbi_parameters_dialog_layout, container);
			break;
		}
		
		dialogView = populateView(dialogView);
		
		return dialogView;
	}

	
	private View populateView(View dialogView){
		TextView programTextView = (TextView)dialogView.findViewById(R.id.query_program_text_view);
		programTextView.setText(mBLASTquery.getBLASTProgram());
		
		TextView databaseTextView = (TextView)dialogView.findViewById(R.id.query_database_text_view);
		databaseTextView.setText(mBLASTquery.getSearchParameter("database").getValue());
		
		TextView expThresholdTextView = (TextView)dialogView.findViewById(R.id.query_exp_threshold_text_view);
		expThresholdTextView.setText(mBLASTquery.getSearchParameter("exp_threshold").getValue());
		
		
		if(mBLASTquery.getVendorID() == BLASTVendor.EMBL_EBI){
			
			TextView scoreTextView = (TextView)dialogView.findViewById(R.id.query_score_text_view);
			scoreTextView.setText(mBLASTquery.getSearchParameter("score").getValue());
			
			TextView emailTextView = (TextView)dialogView.findViewById(R.id.query_email_text_view);
			emailTextView.setText(mBLASTquery.getSearchParameter("email").getValue());
			
			
			return dialogView;
		}
		
		if(mBLASTquery.getVendorID() == BLASTVendor.NCBI){
			
			TextView wordSizeTextView = (TextView)dialogView.findViewById(R.id.query_word_size_text_view);
			wordSizeTextView.setText(mBLASTquery.getSearchParameter("word_size").getValue());
			
			TextView matchMismatchScoreTextView = (TextView)dialogView.findViewById(R.id.query_match_mismatch_score_text_view);
			matchMismatchScoreTextView.setText(mBLASTquery.getSearchParameter("match_mismatch_score").getValue());
			
			
			return dialogView;
		}
		
		return null;
		
	}
	
	private BLASTQuery mBLASTquery;
	
}
