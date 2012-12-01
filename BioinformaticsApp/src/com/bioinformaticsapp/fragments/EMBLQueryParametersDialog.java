package com.bioinformaticsapp.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bioinformaticsapp.R;
import com.bioinformaticsapp.models.BLASTQuery;

public class EMBLQueryParametersDialog extends DialogFragment {

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		emblQuery = (BLASTQuery)getArguments().getSerializable("query");
		setCancelable(true);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getDialog().setTitle(emblQuery.getJobIdentifier());
		
		View dialogView = inflater.inflate(R.layout.embl_parameters_dialog_layout, container);
		
		TextView programTextView = (TextView)dialogView.findViewById(R.id.query_program_text_view);
		programTextView.setText(emblQuery.getBLASTProgram());
		
		TextView databaseTextView = (TextView)dialogView.findViewById(R.id.query_database_text_view);
		databaseTextView.setText(emblQuery.getSearchParameter("database").getValue());
		
		TextView expThresholdTextView = (TextView)dialogView.findViewById(R.id.query_exp_threshold_text_view);
		expThresholdTextView.setText(emblQuery.getSearchParameter("exp_threshold").getValue());
		
		TextView scoreTextView = (TextView)dialogView.findViewById(R.id.query_score_text_view);
		scoreTextView.setText(emblQuery.getSearchParameter("score").getValue());
		
		TextView emailTextView = (TextView)dialogView.findViewById(R.id.query_email_text_view);
		emailTextView.setText(emblQuery.getSearchParameter("email").getValue());
		
		
		return dialogView;
	}

	
	private BLASTQuery emblQuery;
	
}
