package com.bioinformaticsapp.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class BLASTQueryAdapter extends CursorAdapter {

	public BLASTQueryAdapter(Context context, Cursor c) {
		super(context, c);
		
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
