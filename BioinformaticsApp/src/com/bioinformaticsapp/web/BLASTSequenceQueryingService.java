package com.bioinformaticsapp.web;

import com.bioinformaticsapp.exception.IllegalBLASTQueryException;
import com.bioinformaticsapp.models.BLASTQuery;
import com.bioinformaticsapp.models.BLASTQuery.Status;

public interface BLASTSequenceQueryingService {
	
	public String submit(BLASTQuery query) throws IllegalBLASTQueryException;
	
	public Status pollQuery(String jobId);
	
	public String retrieveBLASTResults(String jobId, String format);
	
	public void close();

}
