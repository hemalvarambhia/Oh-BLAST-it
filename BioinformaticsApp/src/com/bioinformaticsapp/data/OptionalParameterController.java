package com.bioinformaticsapp.data;

import java.util.ArrayList;
import java.util.List;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.OptionalParameter;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class OptionalParameterController {

	private OptionalParametersDAO mDAO;
	
	public OptionalParameterController(Context context){
		mDAO = new OptionalParametersDAO(context);
		mDAO.open();
	}
	
	
	public void close(){
		mDAO.close();
	}
	
	public long save(OptionalParameter parameter){
		ContentValues values = new ContentValues();
		values.put(BLASTQuery.BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME, parameter.getName());
		values.put(BLASTQuery.BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE, parameter.getValue());
		values.put(BLASTQuery.BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK, parameter.getBlastQueryId());
	
		long parameterPrimaryKey = mDAO.insert(values);
		
		return parameterPrimaryKey;
		
	}
	
	public List<OptionalParameter> getParametersForQuery(long queryId){
		
		String where = BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK +" = ?";
		String[] queryFK = new String[]{String.valueOf(queryId)};
		List<OptionalParameter> parametersForQuery = null;
		Cursor cursor = mDAO.query(BLASTJob.OPTIONAL_PARAMETER_FULL_PROJECTION, where, queryFK);
		
		if(cursor.getCount() > 0){
			parametersForQuery = new ArrayList<OptionalParameter>();
		}
		
		while(cursor.moveToNext()){
			long id = cursor.getLong(0);
			String parameterName = cursor.getString(1);
			String valueOfParameter = cursor.getString(2);
			long queryForeignKey = cursor.getLong(3);
			OptionalParameter parameter = new OptionalParameter(parameterName, valueOfParameter);
			parameter.setPrimaryKey(id);
			parameter.setBlastQueryId(queryForeignKey);
			parametersForQuery.add(parameter);
		}
		cursor.close();
		
		return parametersForQuery;
		
	}
	
	public int update(long primaryKey, OptionalParameter parameter){
		ContentValues newValues = new ContentValues();
		newValues.put(BLASTJob.COLUMN_NAME_PRIMARY_KEY, parameter.getPrimaryKey());
		newValues.put(BLASTQuery.BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME, parameter.getName());
		newValues.put(BLASTQuery.BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE, parameter.getValue());
		newValues.put(BLASTQuery.BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK, parameter.getBlastQueryId());
		
		int numberUpdated = mDAO.updateParameters(parameter.getPrimaryKey(), newValues);
		
		return numberUpdated;
		
	}

	public int deleteParametersFor(long queryId){
		return mDAO.deleteOptionsFor(queryId);
	}
	
}
