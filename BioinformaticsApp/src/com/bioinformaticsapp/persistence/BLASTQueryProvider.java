package com.bioinformaticsapp.persistence;

import com.bioinformaticsapp.models.BLASTQuery;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class BLASTQueryProvider extends ContentProvider {

	private BLASTDAO mDAO;
	
	private static final UriMatcher uriMatcher;
	
	private static final int BLAST_QUERIES = 1;

    // The incoming URI matches the URI for a single expense specified by ID
    private static final int BLAST_QUERY_ID = 2;

	
	static {
		
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		uriMatcher.addURI(BLASTQuery.AUTHORITY, "blastqueries", BLAST_QUERIES);
		
		uriMatcher.addURI(BLASTQuery.AUTHORITY, "blastqueries/#", BLAST_QUERY_ID);
		
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		
		int numberOfItemsDeleted = 0;
		
		switch(uriMatcher.match(uri)){
		
		case BLAST_QUERY_ID:
			
			long id = ContentUris.parseId(uri);
			numberOfItemsDeleted = mDAO.deleteById(id);
			break;
			
		default:
			break;
		
		}

		getContext().getContentResolver().notifyChange(uri, null);
		
		return numberOfItemsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		String type = "";
		switch(uriMatcher.match(uri)){
		case BLAST_QUERIES:
			type = "vnd.android.cursor.dir/vnd.bioinformaticsapp.blastqueries";
			break;
		
		case BLAST_QUERY_ID:
			type = "vnd.android.cursor.item/vnd.bioinformaticsapp.blastqueries";
			break;
		
		default:
			throw new IllegalArgumentException("uri not recognised");
			
		}
		
		return type;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if(uriMatcher.match(uri) != BLAST_QUERIES){
			throw new IllegalArgumentException("Uri could not be recognised during insertion");
		}
		
		
		long rowId = mDAO.insertBLASTJob(values);
		
		if(rowId > 0){
			
			Uri insertedUri = ContentUris.withAppendedId(BLASTQuery.BLASTJob.CONTENT_QUERY_ID_BASE_URI, rowId);
		
			ContentResolver resolver = getContext().getContentResolver();
		
			//Notify interested parties that the uri changed
			resolver.notifyChange(insertedUri, null);
			
			return insertedUri;
		}else{
			
			throw new SQLiteException("Unable to add the query to the app database");
		
		}
		
	}

	@Override
	public boolean onCreate() {
		
		mDAO = new BLASTDAO(getContext());
		mDAO.open();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor rowsOfTheQuery = null;
		
		//Check for the type of uri submitted: does it relate to a query returning 
		//multiple rows or a single row.
		switch(uriMatcher.match(uri)){
		
		//Handle the possibility of multiple rows that satisfy the where clause
		case BLAST_QUERIES:
			rowsOfTheQuery = mDAO.query(projection, selection, selectionArgs, sortOrder);
			break;
		
		//Get the single selected row
		case BLAST_QUERY_ID:
			rowsOfTheQuery = mDAO.query(projection, selection, selectionArgs, sortOrder);
			break; 
		
		//if there was no match, close the app, this is a fatal error
		default:
			throw new UnsupportedOperationException("There was a problem loading the selection");
		}
		
		
		return rowsOfTheQuery;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		//Initially set the number of affected rows to 0:
		int numberOfRowsAffected = 0;
		
		//Determine the type of uri that was submitted, whether it was for many item URI or
		//a single row URI:
		switch(uriMatcher.match(uri)){
		case BLAST_QUERIES:
			//Update all the rows satisfying the where clause
			numberOfRowsAffected = mDAO.update(selection, selectionArgs, values);
			
		case BLAST_QUERY_ID:
			//Parse the uri, get the primary key id of the row
			long id = ContentUris.parseId(uri);
			
			//Update the row
			numberOfRowsAffected = mDAO.updateById(id, values);
		}
		
		
		return numberOfRowsAffected;
	}

}
