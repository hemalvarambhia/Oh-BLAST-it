package com.bioinformaticsapp;

import java.util.ArrayList;
import java.util.List;

import com.bioinformaticsapp.data.BLASTQueryController;
import com.bioinformaticsapp.data.OptionalParameterController;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.OptionalParameter;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;

public class SetUpBLASTQueryActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		
		inflater.inflate(R.menu.blastqueryentry_menu, menu);
		
		return true;
	}


	protected void storeQueryInDatabase(){
		
		
		if(query.getPrimaryKey() == null){
			//Add the job to our database of BLAST queries...
			long primaryKey = controller.save(query);
			
			query.setPrimaryKeyId(primaryKey);
			
			//Save the parameters:
			List<OptionalParameter> parameters = new ArrayList<OptionalParameter>();
			for(OptionalParameter parameter: query.getAllParameters()){
				parameter.setBlastQueryId(query.getPrimaryKey());
				long parameterPrimaryKey = optionalParametersController.save(parameter);
				parameter.setPrimaryKey(parameterPrimaryKey);
				parameters.add(parameter);
				
			}
			
			query.updateAllParameters(parameters);
			
			
		}else{ 
			//...or date the columns for the specified row:
			controller.update(query.getPrimaryKey(), query);
			
			for(OptionalParameter parameter: query.getAllParameters()){
				optionalParametersController.update(parameter.getPrimaryKey(), parameter);
			}
			
		}
	}
	
	protected BLASTQuery query;
	
	protected BLASTQueryController controller;
	
	protected OptionalParameterController optionalParametersController;
	
}
