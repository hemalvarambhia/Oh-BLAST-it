package com.bioinformaticsapp;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.widget.SearchParametersAdapter;

import android.app.ListActivity;
import android.os.Bundle;

public class BLASTQuerySearchParametersActivity extends ListActivity {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		query = (BLASTQuery)getIntent().getSerializableExtra("query");
		
		SearchParametersAdapter adapter = new SearchParametersAdapter(this, query.getAllParameters());
		setListAdapter(adapter);
	}
	
	private BLASTQuery query;
	
}
