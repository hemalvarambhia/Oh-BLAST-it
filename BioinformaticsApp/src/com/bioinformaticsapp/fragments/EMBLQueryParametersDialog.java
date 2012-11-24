package com.bioinformaticsapp.fragments;

import com.bioinformaticsapp.R;
import com.bioinformaticsapp.models.BLASTQuery;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EMBLQueryParametersDialog extends DialogFragment {

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreateDialog(savedInstanceState);
		emblQuery = (BLASTQuery)getArguments().getSerializable("query");
		
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View dialogView = inflater.inflate(R.layout.embl_parameters_dialog_layout, container);
		
		TextView programTextView = (TextView)dialogView.findViewById(R.id.query_program_text_view);
		programTextView.setText(emblQuery.getBLASTProgram());
		
		
		return dialogView;
	}

	
	private BLASTQuery emblQuery;
	
}
