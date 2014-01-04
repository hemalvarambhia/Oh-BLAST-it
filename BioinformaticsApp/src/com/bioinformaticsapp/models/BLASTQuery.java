package com.bioinformaticsapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Patterns;

public class BLASTQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5182980862941006411L;

	public static final String AUTHORITY = "com.bioinformaticsapp";
	
	/**
	 * The query we're sending: what type (where blastn/p), sequence and where
	 */
	public static final String BLAST_QUERY_TABLE = "BLASTqueries";
	
	/**
	 * Table of search parameter key-value pairs e.g. database = nr, score = 50
	 * exp_threshold = 10
	 */
	public static final String BLAST_SEARCH_PARAMS_TABLE = "BLASTsearch_params";
	
	public enum Status {
		DRAFT, PENDING, SUBMITTED, UNSURE, FINISHED, ERROR, NOT_FOUND,
		
		RUNNING; //Legacy queries which were sent had a RUNNING status
	}
	
	private String blastJobID;
	
	private String program;
	
	private int destination;
	
	/*Optional parameters*/
	private List<SearchParameter> queryOptionalParameters;
	
	private String sequence;
	
	public Status statusOfJob;
	
	private Long primaryKeyId;
	
	public BLASTQuery(String typeOfQuery, int vendor){
		statusOfJob = Status.DRAFT;
		sequence = null;
		program = typeOfQuery;
		queryOptionalParameters = SearchParameter.createDefaultParametersFor(vendor);
		destination = vendor;
	}
	
	public static BLASTQuery ncbiBLASTQuery(String program){
		return new BLASTQuery(program, BLASTVendor.NCBI);
	}
	
	public static BLASTQuery emblBLASTQuery(String program){
		return new BLASTQuery(program, BLASTVendor.EMBL_EBI);
	}
	
	public void setPrimaryKeyId(long id){
		primaryKeyId = new Long(id);
	}
	
	public Long getPrimaryKey(){
		return primaryKeyId;
	}
	
	public String getJobIdentifier(){
		return blastJobID;
	}
	
	public void setJobIdentifier(String newJobIdentifier){
		blastJobID = newJobIdentifier;
	}
	
	public void setBLASTProgram(String newProgram){
		program = newProgram;
	}
	
	public String getBLASTProgram(){
		return program;
	}
	
	public void setSearchParameter(String parameterName, String value){
		
		boolean containsParameter = false;
		for(int i = 0; i < queryOptionalParameters.size(); i++){
			SearchParameter parameter = queryOptionalParameters.get(i);
			if(parameter.getName().equals(parameterName)){
				containsParameter = true;
				parameter.setValue(value);
				queryOptionalParameters.set(i, parameter);
			}
		}
		
		if(!containsParameter){
			queryOptionalParameters.add(new SearchParameter(parameterName, value));
		}
		
	}
	
	public void updateAllParameters(List<SearchParameter> newSetOfParameters){
		queryOptionalParameters = new ArrayList<SearchParameter>(newSetOfParameters);
	}
	
	public SearchParameter getSearchParameter(String parameterName){
		
		SearchParameter parameterWithName = null;
		for(int i = 0; i < queryOptionalParameters.size(); i++){
			SearchParameter parameter = queryOptionalParameters.get(i);
			if(parameter.getName().equals(parameterName)){
				parameterWithName = parameter;
			}
		}
		
		return parameterWithName;
	}
	
	public List<SearchParameter> getAllParameters(){
		return new ArrayList<SearchParameter>(queryOptionalParameters);
	}
	
	public void setStatus(Status newStatus){
		statusOfJob = newStatus;
	}
	
	/**
	 * @return the database used for the BLAST query
	 */
	public Status getStatus(){
		return statusOfJob;
	}
	
	public int getVendorID(){
		return destination;
	}
	
	/**
	 * @return the destination
	 */
	public String getDestination() {
		String vendor = "";
		switch(destination){
		case BLASTVendor.EMBL_EBI:
			vendor = "EMBL-EBI";
			break;
		
		case BLASTVendor.NCBI:
			vendor = "NCBI";
			break;
		} 
		
		return vendor;	
	}

	public void setSequence(String sequenceString){
		sequence = sequenceString;	
	}
	
	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((blastJobID == null) ? 0 : blastJobID.hashCode());
		result = prime * result + destination;
		result = prime * result
				+ ((primaryKeyId == null) ? 0 : primaryKeyId.hashCode());
		result = prime * result + ((program == null) ? 0 : program.hashCode());
		result = prime
				* result
				+ ((queryOptionalParameters == null) ? 0
						: queryOptionalParameters.hashCode());
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		result = prime * result
				+ ((statusOfJob == null) ? 0 : statusOfJob.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BLASTQuery other = (BLASTQuery) obj;
		if (blastJobID == null) {
			if (other.blastJobID != null)
				return false;
		} else if (!blastJobID.equals(other.blastJobID))
			return false;
		if (destination != other.destination)
			return false;
		if (primaryKeyId == null) {
			if (other.primaryKeyId != null)
				return false;
		} else if (!primaryKeyId.equals(other.primaryKeyId))
			return false;
		if (program == null) {
			if (other.program != null)
				return false;
		} else if (!program.equals(other.program))
			return false;
		if (queryOptionalParameters == null) {
			if (other.queryOptionalParameters != null)
				return false;
		} else if (!queryOptionalParameters
				.equals(other.queryOptionalParameters))
			return false;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		if (statusOfJob != other.statusOfJob)
			return false;
		return true;
	}

	@Override
	public Object clone() {
		BLASTQuery clone = new BLASTQuery(program, destination);
		if(primaryKeyId != null)
			clone.setPrimaryKeyId(primaryKeyId);
		clone.setSequence(sequence);
		clone.setJobIdentifier(blastJobID);
		clone.setStatus(statusOfJob);
		List<SearchParameter> cloned = new ArrayList<SearchParameter>();
		for(SearchParameter parameter: queryOptionalParameters){
			cloned.add(new SearchParameter(parameter.getName(), parameter.getValue()));
		}
		clone.updateAllParameters(cloned);
		return clone;
	}

	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append(primaryKeyId+", ");
		builder.append(blastJobID+", ");
		builder.append(destination+", ");
		builder.append(sequence+", ");
		builder.append(statusOfJob+"\t");
		builder.append(queryOptionalParameters.toString());
		
		return builder.toString();	
	}
	
	public boolean isValid() {
		
		if(sequence == null || sequence.isEmpty()){
			return false;
		}
		
		SearchParameter email = getSearchParameter("email");
		
		if(email != null){
			
			if(email.getValue() == null){
				return false;
			}
			
			boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email.getValue()).matches();
			if(!isValidEmail){
				return false;
			}
		}
		
		boolean isValid = false;
			
		try {
			DNATools.createDNA(sequence);
			isValid = true;
		} catch (IllegalSymbolException e) {
			// is valid is default set to true
				
		}
	
		return isValid;
	}
	
	public interface BLASTJob extends BaseColumns {
		
		/**
		 * Defining the columns
		 */
		
		/**
		 * The ID column required by the content provider
		 */
		public static final String COLUMN_NAME_PRIMARY_KEY = BaseColumns._ID;
		
		/**
		 * The column for the job ID returned by the web service
		 */
		public static final String COLUMN_NAME_BLASTQUERY_JOB_ID = "jobId";
		
		/**
		 * Column of the search sequence
		 */
		public static final String COLUMN_NAME_BLASTQUERY_SEQUENCE = "sequence";
		
		/**
		 * Column for the database used for the query
		 */
		public static final String COLUMN_NAME_BLASTQUERY_DATABASE = "database";
		
		/**
		 * The column for current status of the query
		 */
		public static final String COLUMN_NAME_BLASTQUERY_JOB_STATUS = "status";
		
		/**
		 * The column for the program to be used in the query search
		 */
		public static final String COLUMN_NAME_BLASTQUERY_PROGRAM = "blast_program";
		
		/**
		 * The column for the match matrix being used in the query search
		 */
		public static final String COLUMN_NAME_BLASTQUERY_SCORE = "score";
		
		/**
		 * The column for the expectation threshold parameter of the BLAST query
		 */
		public static final String COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD = "exp_threshold";
		
		
		/**
		 * The column that contains the destination the BLAST query was sent to e.g. NCBI
		 */
		public static final String COLUMN_NAME_BLASTQUERY_DESTINATION = "dest";
		
		/**
		 * The column that contains the value of a query's match/mismatch score
		 */
		public static final String COLUMN_NAME_BLASTQUERY_MATCH_MISMATCH_SCORE = "match_mismatch_score";
		
		/**
		 * This column will contain the name of the parameter e.g. 'exp_threshold'
		 */
		public static final String COLUMN_NAME_BLASTQUERY_PARAM_NAME = "param_name";
		
		/**
		 * This column refers to the value of the parameter e.g. '10' (exp_threshold = 10)
		 */
		public static final String COLUMN_NAME_BLASTQUERY_PARAM_VALUE = "param_value";
		
		/**
		 * This is the foreign refers to the query in the BLASTqueries table
		 */
		public static final String COLUMN_NAME_BLASTQUERY_QUERY_FK = "query_fk";
		
		/**
		 * All the columns of our table of BLAST queries:
		 */
		public static final String[] FULL_PROJECTIONS = new String[]{
			COLUMN_NAME_PRIMARY_KEY, 
			COLUMN_NAME_BLASTQUERY_JOB_ID, 
			COLUMN_NAME_BLASTQUERY_SEQUENCE, 
			COLUMN_NAME_BLASTQUERY_JOB_STATUS, 
			COLUMN_NAME_BLASTQUERY_PROGRAM, 
			COLUMN_NAME_BLASTQUERY_SCORE, 
			COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD,
			COLUMN_NAME_BLASTQUERY_DATABASE
		};
		
		public static final String[] PARENT_TABLE_FULL_PROJECTIONS = {
			COLUMN_NAME_PRIMARY_KEY, 
			COLUMN_NAME_BLASTQUERY_JOB_ID, 
			COLUMN_NAME_BLASTQUERY_SEQUENCE, 
			COLUMN_NAME_BLASTQUERY_JOB_STATUS, 
			COLUMN_NAME_BLASTQUERY_PROGRAM, 
			COLUMN_NAME_BLASTQUERY_DESTINATION			
		};
		
		public static final String[] OPTIONAL_PARAMETER_FULL_PROJECTION = new String[]{
			COLUMN_NAME_PRIMARY_KEY,
			COLUMN_NAME_BLASTQUERY_PARAM_NAME,
			COLUMN_NAME_BLASTQUERY_PARAM_VALUE,
			COLUMN_NAME_BLASTQUERY_QUERY_FK
		};
		
		/**
		 * The columns used to populate our list
		 */
		public static final String[] LIST_PROJECTIONS = new String[]{
			COLUMN_NAME_PRIMARY_KEY, 
			COLUMN_NAME_BLASTQUERY_JOB_ID, 
			COLUMN_NAME_BLASTQUERY_JOB_STATUS
		};
		
		/**
		 * The columns that correspond to our query parameters
		 */
		public static final String[] QUERY_PARAMETER_PROJECTIONS = new String[]{
			COLUMN_NAME_BLASTQUERY_SEQUENCE, 
			COLUMN_NAME_BLASTQUERY_PROGRAM, 
			COLUMN_NAME_BLASTQUERY_SCORE,
			COLUMN_NAME_BLASTQUERY_EXP_THRESHOLD,
			COLUMN_NAME_BLASTQUERY_DATABASE,
			COLUMN_NAME_BLASTQUERY_DESTINATION,
			COLUMN_NAME_BLASTQUERY_MATCH_MISMATCH_SCORE
		};
		
		/**
		 * Define the stuff needed for our content provider
		 */
		public static final String SCHEME = "content://";
		
		public static final String PATH_QUERIES = "/blastqueries";
		
		public static final String PATH_BLASTQUERY_ID = "/blastqueries/"; 
		
		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_QUERIES);
		
		public static final Uri CONTENT_QUERY_ID_BASE_URI = Uri.parse(SCHEME + AUTHORITY + PATH_BLASTQUERY_ID);
		
		
		
	}

	
}
