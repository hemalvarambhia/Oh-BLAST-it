package com.bioinformaticsapp.widget;

import java.util.List;

import com.bioinformaticsapp.R;
import com.bioinformaticsapp.models.SearchParameter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SearchParametersAdapter extends ArrayAdapter<SearchParameter> {

	public SearchParametersAdapter(Context context, int resource,
			int textViewResourceId, List<SearchParameter> objects) {
		super(context, resource, textViewResourceId, objects);
		
	}
	
	public SearchParametersAdapter(Context context, List<SearchParameter> parameters){
		super(context, R.layout.search_parameter_list_item, parameters);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		if(view == null){
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.search_parameter_list_item, null);
		}
		
		TextView parameterScientificName = (TextView)view.findViewById(R.id.parameter_scientific_name_label);
		SearchParameter parameter = getItem(position);
		
		if(parameterScientificName != null){
			parameterScientificName.setText(translateFrom(parameter.getName()));
		}
		
		TextView parameterValue = (TextView)view.findViewById(R.id.parameter_value_label);
		if(parameterValue != null){
			parameterValue.setText(parameter.getValue());
		}
		
		return view;
	}


	private String translateFrom(String parameterName){
		if(parameterName.equals("database")){
			return "Database";
		}
		
		if(parameterName.equals("exp_threshold")){
			return "Exponential Threshold";
		}
		
		if(parameterName.equals("match_mismatch_score")){
			return "Match/Mismatch Score";
		}
		
		
		return null;
		
	}
	
}
