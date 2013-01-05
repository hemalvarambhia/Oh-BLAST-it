package com.bioinformaticsapp;

import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTVendor;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This Activity is the 'home screen' of this application
 * @author Hemal N Varambhia
 *
 */
public class BioinformaticsAppHomeActivity extends ListActivity {
    
	public static final int READY_TO_SEND = 1;
	
	private final static int CREATE_QUERY = 2;
	
	
	/** Called when the activity is first created. */
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        
    	String[] typesOfQueries = new String[]{"Draft Queries", "Pending", "Finished"};
    	
    	ArrayAdapter<String> types = new ArrayAdapter<String>(this, R.layout.categories_list_item, R.id.type_of_query_textview, typesOfQueries);
    	
    	setListAdapter(types);
        
    }
	
	/* 
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		boolean handled = false;
		switch(itemId){
		
		case R.id.create_embl_query: {
			Intent setupBLASTQuery = new Intent(this, EMBLEBISetUpQueryActivity.class);
			setupBLASTQuery.putExtra("query", new BLASTQuery("blastn", BLASTVendor.EMBL_EBI));
			startActivityForResult(setupBLASTQuery, CREATE_QUERY);
			handled = true;
			
		}
		break;
		
		case R.id.create_ncbi_query: {
			Intent setupBLASTQuery = new Intent(this, NCBIQuerySetUpActivity.class);
			setupBLASTQuery.putExtra("query", new BLASTQuery("blastn", BLASTVendor.NCBI));
			startActivityForResult(setupBLASTQuery, CREATE_QUERY);
			handled = true;
		}
		break;
		
		case R.id.settings_item: {
			Intent applicationPreferences = new Intent(this, AppPreferences.class);
			startActivity(applicationPreferences);
			handled = true;
			
		}
		break;
		
		default:
			handled = super.onOptionsItemSelected(item);
			break;
		}
		
		return handled;
	}

	/* Event handling when the user taps a row in the list item
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		Intent activityToLaunch = null;
		switch(position){
		case 0:
			activityToLaunch = new Intent(this, DraftBLASTQueriesActivity.class);
			break;
			
		case 1:
			activityToLaunch = new Intent(this, PendingQueriesActivity.class);
			break;
		case 2:
			activityToLaunch = new Intent(this, FinishedQueriesActivity.class);
			break;
			
		default:
			break;
		}
		
		if(activityToLaunch == null){
			return;
		}
		
		startActivity(activityToLaunch);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode){
		case READY_TO_SEND:
			Intent sendService = new Intent(this, SubmitQueryService.class);
			startService(sendService);
			break;
			
		default:
			break;
		}
	}
    
    
    
}