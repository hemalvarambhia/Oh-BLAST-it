package com.bioinformaticsapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;

public class Version1ToVersion2Upgrader {

	private SQLiteDatabase database;
	
	private static final String CREATE_V2_QUERY_TABLE_COMMAND = 
			"create table if not exists "+BLASTQuery.BLAST_QUERY_TABLE +
			" ("+BLASTJob.COLUMN_NAME_PRIMARY_KEY+" integer primary key, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID+" text, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE+" text,"
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS+" text not null, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM+" text, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION+" int"
	
			+ ")";
			
	private static final String CREATE_V2_PARAMS_TABLE_COMMAND =
			"create table if not exists "+BLASTQuery.BLAST_SEARCH_PARAMS_TABLE +
	
			" ( " + BLASTJob.COLUMN_NAME_PRIMARY_KEY+" integer primary key autoincrement,"
			
			+ BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME+" text not null, "
			
			+ BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE+" text not null, "
			
			+ BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK+" int, "
			
			+ "FOREIGN KEY("+BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK+") REFERENCES "+BLASTQuery.BLAST_QUERY_TABLE+"("+BLASTJob.COLUMN_NAME_PRIMARY_KEY+")"
			
			+ ")";

	private static final String TAG = "Version1ToVersion2Upgrader";

	public Version1ToVersion2Upgrader(SQLiteDatabase db){
		database = db;
	}
	
	
	public void doSchemaMigration(){
		
		//Re-name the table to query_v1
		database.execSQL("ALTER TABLE "+BLASTQuery.BLAST_QUERY_TABLE+" RENAME TO "+BLASTQuery.BLAST_QUERY_TABLE+"_v"+1);
		
		//Create the new version of the tables
		database.execSQL(CREATE_V2_QUERY_TABLE_COMMAND);
		database.execSQL(CREATE_V2_PARAMS_TABLE_COMMAND);
		
	}
	
	public void moveQueriesToNewTables(){
		String oldTable = BLASTQuery.BLAST_QUERY_TABLE+"_v1";
		String[] projection = new String[]{BLASTJob.COLUMN_NAME_PRIMARY_KEY, 
				BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID,
				BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM,
				BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE,
				BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS,
				BLASTJob.COLUMN_NAME_BLASTQUERY_DATABASE,
				BLASTJob.COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD,
				BLASTJob.COLUMN_NAME_BLASTQUERY_SCORE
		
		};
		Cursor userDataCursor = database.query(oldTable, projection, null, null, null, null, null);
		while(userDataCursor.moveToNext()){
			database.beginTransaction();
			long pk = userDataCursor.getLong(0);
			String blastJobID = userDataCursor.getString(1);
			String program = userDataCursor.getString(2);
			String sequence = userDataCursor.getString(3);
			String status = userDataCursor.getString(4);
			
			ContentValues values = new ContentValues();
			values.put(BLASTJob.COLUMN_NAME_PRIMARY_KEY, pk);
			values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, blastJobID);
			values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, program);
			values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, sequence);
			values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION, BLASTVendor.EMBL_EBI);
			values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS, status);
			long rowID = database.insert(BLASTQuery.BLAST_QUERY_TABLE, null, values);
			
			if(rowID > -1){
				//Add the search parameter for this query to the search parameters table
				String databaseString = userDataCursor.getString(5);
				ContentValues databaseValue = new ContentValues();
				databaseValue.put(BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK, pk);
				databaseValue.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME, "database");
				databaseValue.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE, databaseString);
				database.insert(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, null, databaseValue);
				
				String expThreshold = userDataCursor.getString(6);
				ContentValues expThresholdValue = new ContentValues();
				expThresholdValue.put(BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK, pk);
				expThresholdValue.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME, "exp_threshold");
				expThresholdValue.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE, expThreshold);
				database.insert(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, null, expThresholdValue);
				
				String score = userDataCursor.getString(7);
				ContentValues scoreValue = new ContentValues();
				scoreValue.put(BLASTJob.COLUMN_NAME_BLASTQUERY_QUERY_FK, pk);
				scoreValue.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_NAME, "score");
				scoreValue.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PARAM_VALUE, score);
				database.insert(BLASTQuery.BLAST_SEARCH_PARAMS_TABLE, null, scoreValue);
			}
			database.setTransactionSuccessful();
			database.endTransaction();
		}
		
		
		userDataCursor.close();
	}
	
	
}
