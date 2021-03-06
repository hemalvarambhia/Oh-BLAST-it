package com.bioinformaticsapp.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bioinformaticsapp.R;
import com.bioinformaticsapp.domain.BLASTQuery;

public class BLASTQueryAdapter extends ArrayAdapter<BLASTQuery> {

	public BLASTQueryAdapter(Context context, int textViewResourceId,
			List<BLASTQuery> objects) {
		super(context, textViewResourceId, objects);
		
	}
	
	public BLASTQueryAdapter(Context context, List<BLASTQuery> queries){
		super(context, R.layout.blast_hit_item, queries);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if(view == null){
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.blastquery_list_item, null);
		}
		
		BLASTQuery query = getItem(position);
		
		TextView recipientLabel = (TextView)view.findViewById(R.id.to_label);
		
		if(recipientLabel != null){
			recipientLabel.setText(query.getDestination());
		}
		
		TextView jobIdentifierLabel = (TextView)view.findViewById(R.id.query_job_id_label);
		
		if(jobIdentifierLabel != null){
			if(query.getJobIdentifier() != null){
				jobIdentifierLabel.setText(query.getJobIdentifier());
			}else{
				jobIdentifierLabel.setText("N/A");
			}
		}
		
		TextView sequenceLabel = (TextView)view.findViewById(R.id.query_sequence_label);
		
		if(sequenceLabel != null){
			sequenceLabel.setText(query.getSequence());
		}
		
		TextView statusLabel = (TextView)view.findViewById(R.id.query_job_status_label);
		
		if(statusLabel != null){
			statusLabel.setText(query.getStatus().toString());
		}
		
		return view;
	}

	
}
