package com.bioinformaticsapp.persistence;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.BLASTJob;
import com.bioinformaticsapp.models.BLASTQuery.Status;

public class BLASTQueryController {

	private BLASTDAO mDAO;
	
	private static final String TAG = "BLASTQueryController";
	
	public BLASTQueryController(Context context){
		mDAO = new BLASTDAO(context);
		mDAO.open();
		
	}
	
	public void close(){
		mDAO.close();
	}
	
	public long save(BLASTQuery query){
		ContentValues values = new ContentValues();		
		//When we save a draft query we need to generate a unique temporary job ID
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, query.getBLASTProgram());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, query.getJobIdentifier());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, query.getSequence());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS, query.getStatus().toString());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION, query.getVendorID());
		long primaryKey = mDAO.insertBLASTJob(values);
		
		return primaryKey;
		
	}
	
	public int update(long id, BLASTQuery query){
		ContentValues values = new ContentValues();
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, query.getJobIdentifier());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, query.getBLASTProgram());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, query.getSequence());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS, query.getStatus().toString());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION, query.getVendorID());
		
		int numberOfRowsUpdated = mDAO.updateById(id, values);
		
		return numberOfRowsUpdated;
	}
	
	public int update(BLASTQuery draftQuery) {
		ContentValues values = new ContentValues();
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, draftQuery.getJobIdentifier());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, draftQuery.getBLASTProgram());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, draftQuery.getSequence());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS, draftQuery.getStatus().toString());
		values.put(BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION, draftQuery.getVendorID());
		
		return mDAO.updateById(draftQuery.getPrimaryKey(), values);
	}

	public BLASTQuery findBLASTQueryById(long id){
		String[] projection = new String[]{
				BLASTJob.COLUMN_NAME_PRIMARY_KEY, //0 
				BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, //1
				BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION, //2
				BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, //3
				BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, //4
				BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS}; //5
		String whereClause = BLASTJob.COLUMN_NAME_PRIMARY_KEY+" = ?";
		String[] primaryKey = new String[]{String.valueOf(id)};
		Cursor row = mDAO.query(projection, whereClause, primaryKey, null);
		
		BLASTQuery query = null;
		if(row.moveToFirst()){
			long pk = row.getLong(0);
			String blastJobId = row.getString(1);
			int vendorId = row.getInt(2);
			String program = row.getString(3);
			query = new BLASTQuery(program, vendorId);
			query.setPrimaryKeyId(pk);
			query.setJobIdentifier(blastJobId);
			query.setSequence(row.getString(4));
			query.setStatus(Status.valueOf(row.getString(5)));
		}
		
		row.close();
		
		return query;
	}
	
	public List<BLASTQuery> findBLASTQueriesByStatus(Status status){
		String[] projection = new String[]{
				BLASTJob.COLUMN_NAME_PRIMARY_KEY, //0 
				BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, //1
				BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION, //2
				BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, //3
				BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, //4
				BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS}; //5
		
		String whereClause = BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS+" = ?";
		String[] primaryKey = new String[]{String.valueOf(status)};
		
		Cursor row = mDAO.query(projection, whereClause, primaryKey, null);
		
		List<BLASTQuery> queriesWithStatus = new ArrayList<BLASTQuery>();
		while(row.moveToNext()){
			long pk = row.getLong(0);
			String blastJobId = row.getString(1);
			int vendorId = row.getInt(2);
			String program = row.getString(3);
			BLASTQuery query = new BLASTQuery(program, vendorId);
			query.setPrimaryKeyId(pk);
			query.setJobIdentifier(blastJobId);
			query.setSequence(row.getString(4));
			query.setStatus(Status.valueOf(row.getString(5)));
			queriesWithStatus.add(query);
		}
		
		row.close();
		
		return queriesWithStatus;
	}

	public List<BLASTQuery> getSubmittedBLASTQueries() {
		String[] projection = new String[]{
				BLASTJob.COLUMN_NAME_PRIMARY_KEY, //0 
				BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_ID, //1
				BLASTJob.COLUMN_NAME_BLASTQUERY_DESTINATION, //2
				BLASTJob.COLUMN_NAME_BLASTQUERY_PROGRAM, //3
				BLASTJob.COLUMN_NAME_BLASTQUERY_SEQUENCE, //4
				BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS}; //5
		
		String whereClause = BLASTJob.COLUMN_NAME_BLASTQUERY_JOB_STATUS+" IN (?, ?)";
		String[] primaryKey = new String[]{String.valueOf(Status.SUBMITTED), String.valueOf(Status.RUNNING)};
		
		Cursor row = mDAO.query(projection, whereClause, primaryKey, null);
		
		List<BLASTQuery> runningAndSubmittedQueries = new ArrayList<BLASTQuery>();
		while(row.moveToNext()){
			long pk = row.getLong(0);
			String blastJobId = row.getString(1);
			int vendorId = row.getInt(2);
			String program = row.getString(3);
			BLASTQuery query = new BLASTQuery(program, vendorId);
			query.setPrimaryKeyId(pk);
			query.setJobIdentifier(blastJobId);
			query.setSequence(row.getString(4));
			if(row.getString(5).equals(String.valueOf(Status.RUNNING))){
				query.setStatus(Status.SUBMITTED);
			}else{
				query.setStatus(Status.valueOf(row.getString(5)));
			}
			runningAndSubmittedQueries.add(query);
		}
		
		row.close();
		
		return runningAndSubmittedQueries;
	}

	public int delete(long id){
		return mDAO.deleteById(id);
	}

	
}
