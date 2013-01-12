package com.bioinformaticsapp;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.widget.SearchParametersAdapter;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BLASTQuerySearchParametersActivity extends ListActivity {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_parameters_layout);
		query = (BLASTQuery)getIntent().getSerializableExtra("query");
		
		if(query.getJobIdentifier() == null){
			setTitle("BLAST Query Parameters");
		}else{
			setTitle(query.getJobIdentifier());
		}
		
		TextView programValueLabel = (TextView)findViewById(R.id.program_value_label);
		programValueLabel.setText(query.getBLASTProgram());
		SearchParametersAdapter adapter = new SearchParametersAdapter(this, query.getAllParameters());
		setListAdapter(adapter);
	}
	
	private BLASTQuery query;
	
}
