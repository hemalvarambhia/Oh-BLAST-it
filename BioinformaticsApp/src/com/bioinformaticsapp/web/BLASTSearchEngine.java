package com.bioinformaticsapp.web;

import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;

public interface BLASTSearchEngine {
	
	public String submit(BLASTQuery query) throws IllegalBLASTQueryException;
	
	public SearchStatus pollQuery(String jobId);
	
	public String retrieveBLASTResults(String jobId, String format);
	
	public void close();

}
