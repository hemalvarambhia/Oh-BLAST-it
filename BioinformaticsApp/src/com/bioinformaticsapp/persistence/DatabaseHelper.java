package com.bioinformaticsapp.persistence;

import com.bioinformaticsapp.domain.BLASTQuery;
import com.bioinformaticsapp.domain.BLASTQuery.BLASTJob;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A helper class to handle creation of the app database
 * and any upgrades
 * @author Hemal N Varambhia
 *
 */

public class DatabaseHelper extends SQLiteOpenHelper {
	/**
	 * v1 --> 2: alter table and add columns for 
	 * 	destination text, 
	 * 	match/mismatch score text
	 * 
	 */
	
	public static final String DATABASE_NAME = "BLASTqueries.db";
	
	private static final int DATABASE_VERSION = 2;// old version 1;
	
	@SuppressWarnings("unused")
	private static final String CREATE_DATABASE_COMMAND =
	        "create table "+BLASTQuery.BLAST_QUERY_TABLE+
	        " ("+BLASTJob.COLUMN_NAME_PRIMARY_KEY+" integer primary key autoincrement, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID+" text not null, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE+" text,"
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS+" text not null, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM+" text, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_DATABASE+" text, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_SCORE+" text, "
	        
	        + BLASTJob.COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD+" text"
	        
	        +")";
	
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
	
	public DatabaseHelper(Context context){
		  super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Method to create the database of BLAST queries when the app is installed
	 * for the first time
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(CREATE_V2_QUERY_TABLE_COMMAND);
		db.execSQL(CREATE_V2_PARAMS_TABLE_COMMAND);
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//check the value of the old version number
		switch(oldVersion){
		//(transition 1 ---> 2): alter the table and add the following extra columns: 
		case 1:
			
			Version1ToVersion2Upgrader upgrader = new Version1ToVersion2Upgrader(db);
			upgrader.doSchemaMigration();
			upgrader.moveQueriesToNewTables();
			
			break;
		}
		
	}

}
