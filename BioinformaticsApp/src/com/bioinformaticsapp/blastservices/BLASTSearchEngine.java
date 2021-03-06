package com.bioinformaticsapp.blastservices;

import com.bioinformaticsapp.domain.BLASTQuery;

public interface BLASTSearchEngine {
	
	public String submit(BLASTQuery query);
	
	public SearchStatus pollQuery(String jobId);
	
	public String retrieveBLASTResults(String jobId, String format);
	
	public void close();

}
