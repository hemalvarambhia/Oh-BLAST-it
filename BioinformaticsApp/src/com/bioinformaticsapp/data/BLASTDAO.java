package com.bioinformaticsapp.data;

import java.util.HashMap;
import java.util.Map;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class BLASTDAO {

	/**
	 * The delegate class that will create our database
	 */
	private DatabaseHelper mDatabaseHelper;
	
	/**
	 * This object is used in the SQLite query builder
	 */
	private static Map<String, String> projections;
	
	private SQLiteDatabase database;
	
	/**
	 * Instanciate and initialise the projections map
	 */
	static {
		
		projections = new HashMap<String, String>();
		
		projections.put(BLASTJob.COLUMN_NAME_PRIMARY_KEY, BLASTJob.COLUMN_NAME_PRIMARY_KEY);

		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID);
		
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION, BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION);
		
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE);
		
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS, BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS);
		
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM);
		
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME, BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME);
		
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE, BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE); 
		
		projections.put(BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK, BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK);
		
	}
	
	public BLASTDAO(Context context){
		mDatabaseHelper = new DatabaseHelper(context);
	}
	
	public BLASTDAO open(){
		database = mDatabaseHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		database.close();
	}
	
	public long insertBLASTJob(ContentValues values){
		
		//SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
		
		long rowId = database.insert(BLASTQuery.BLAST_QUERY_TABLE, null, values);
		
		return rowId;
		
	}
	
	public Cursor query(String[] projection, String selection, String[] selectionArguments, String sortOrder){
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(BLASTQuery.BLAST_QUERY_TABLE);
		queryBuilder.setProjectionMap(projections);
		
		/**
		 * There is no need for the GROUPBY clause or the HAVING clause
		 */
		Cursor queryResults = queryBuilder.query(database, projection, selection, selectionArguments, null, null, sortOrder);

		return queryResults;
	}
	
	
	public int update(String whereClause, String[] whereArguments, ContentValues values){
		//Update the row with new columns values as specified by the clause and our values	 
		int numberOfRowsAffected = database.update(BLASTQuery.BLAST_QUERY_TABLE, values, whereClause, whereArguments);
		
		return numberOfRowsAffected;
	}
	
	public int updateById(long id,ContentValues values){
		
		// Get the editable form of the BLAST jobs database
		//SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
		
		//set up the where clause:
		String whereClause = "_id = ?";
		
		//Set up the value for the where clauses placeholder:
		String[] whereArguments = new String[]{new Long(id).toString()}; 
		
		//Update the database and get the number of rows affected by the change
		int numberOfRowsAffected = database.update(BLASTQuery.BLAST_QUERY_TABLE, values, whereClause, whereArguments);
		
		return numberOfRowsAffected;
	}
	
	public int deleteById(long id){
		//SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
		
		String whereClause = "_id = ?";
		
		String[] whereArgs = new String[]{new Long(id).toString()};
		
		int numberOfRowsDeleted = database.delete(BLASTQuery.BLAST_QUERY_TABLE, whereClause, whereArgs);
		
		
		return numberOfRowsDeleted;
	}
	
}
