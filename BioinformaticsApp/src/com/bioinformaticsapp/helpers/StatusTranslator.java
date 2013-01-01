package com.bioinformaticsapp.helpers;

import java.util.EnumMap;

import com.bioinformaticsapp.models.BLASTQuery.Status;
import com.bioinformaticsapp.web.SearchStatus;

public class StatusTranslator {
	
	public StatusTranslator(){
		dictionary = new EnumMap<SearchStatus, Status>(SearchStatus.class);
		dictionary.put(SearchStatus.RUNNING, Status.SUBMITTED);
		dictionary.put(SearchStatus.FINISHED, Status.FINISHED);
		dictionary.put(SearchStatus.ERROR, Status.ERROR);
		dictionary.put(SearchStatus.NOT_FOUND, Status.NOT_FOUND);
		dictionary.put(SearchStatus.UNSURE, Status.UNSURE);
	}

	public Status translate(SearchStatus statusOfSearch){
		Status statusOfQuery = Status.UNSURE;
		
		statusOfQuery = dictionary.get(statusOfSearch);
		
		return statusOfQuery;
	}
	
	private EnumMap<SearchStatus, Status> dictionary;
	
}
