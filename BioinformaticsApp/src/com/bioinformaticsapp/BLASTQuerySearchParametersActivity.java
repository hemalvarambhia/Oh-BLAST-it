package com.bioinformaticsapp;

import android.app.ListActivity;
import android.os.Bundle;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.widget.SearchParametersAdapter;

public class BLASTQuerySearchParametersActivity extends ListActivity {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		query = (BLASTQuery)getIntent().getSerializableExtra("query");
		
		if(query.getJobIdentifier() == null){
			setTitle("BLAST Query Parameters");
		}else{
			setTitle(query.getJobIdentifier());
		}
		
		SearchParametersAdapter adapter = new SearchParametersAdapter(this, query.getAllParameters());
		setListAdapter(adapter);
	}
	
	private BLASTQuery query;
	
}
