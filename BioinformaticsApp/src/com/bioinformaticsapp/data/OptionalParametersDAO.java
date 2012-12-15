package com.bioinformaticsapp.data;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;

/**
 * DAO to help create, read, update or delete our optional parameters
 * @author Hemal N Varambhia
 *
 */
public class OptionalParametersDAO {
	private static final String TAG = "OptionalParametersDAO";
	/**
	 * The delegate class that will create our database
	 */
	private DatabaseHelper mDatabaseHelper;
	
	/**
	 * This object is used in the SQLite query builder
	 */
	private static Map<String, String> projections;
	
	/**
	 * Instanciate and initialise the projections map
	 */
	static {
		
		projections = new HashMap<String, String>();
		projections.put(BLASTJob.COLUMN_NAME_PRIMARY_KEY, BLASTJob.COLUMN_NAME_PRIMARY_KEY);
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME, BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME);
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE, BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE);
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK, BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK);
		
	}
	
	private SQLiteDatabase database;
	
	public OptionalParametersDAO(Context context){
		mDatabaseHelper = new DatabaseHelper(context);
	}
	
	public OptionalParametersDAO open(){
		database = mDatabaseHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		database.close();
	}
	
	public long insert(ContentValues option){
		//SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
		long primaryKey = database.insert(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, null, option);
		//database.close();
		
		return primaryKey;		
		
	}
	
	public Cursor query(String[] columns, String where, String[] whereArgs){
		//SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
		Cursor c = database.query(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, columns, where, whereArgs, null, null, null);
		
		return c;
		
	}
	
	public int deleteOptionsFor(long queryPrimaryKey){
		String where = BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK + "= ?";
		String[] id = new String[]{String.valueOf(queryPrimaryKey)};
		return database.delete(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, where, id);
	}
	
	public int updateParameters(long id, ContentValues newValues){
		
		//SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
		int numberUpdated = 0;
		String where = BLASTJob.COLUMN_NAME_PRIMARY_KEY +" = ?";
		String[] parameterPrimaryKey = new String[]{ String.valueOf(id) };
		numberUpdated += database.update(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, newValues, where, parameterPrimaryKey); 
		
		//database.close();
		
		return numberUpdated;
	}

}
