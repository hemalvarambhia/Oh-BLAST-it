package com.bioinformaticsapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchParameter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1362234717956849422L;

	private Long primaryKey;
	
	private Long blastQueryId;
	
	private String name;
	
	private String value;
	
	public SearchParameter(String name, String value){
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the primaryKey
	 */
	public Long getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(Long primaryKey) {
		this.primaryKey = primaryKey;
	}

	
	/**
	 * @return the blastQueryId
	 */
	public Long getBlastQueryId() {
		return blastQueryId;
	}

	/**
	 * @param blastQueryId the blastQueryId to set
	 */
	public void setBlastQueryId(Long blastQueryId) {
		this.blastQueryId = blastQueryId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((primaryKey == null) ? 0 : primaryKey.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		SearchParameter other = (SearchParameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (primaryKey == null) {
			if (other.primaryKey != null)
				return false;
		} else if (!primaryKey.equals(other.primaryKey))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OptionalParameter [primaryKey=" + primaryKey + ", name=" + name
				+ ", value=" + value + "]";
	}
	
	public static List<SearchParameter> createDefaultParametersFor(int vendorID){
		List<SearchParameter> queryOptionalParameters = new ArrayList<SearchParameter>();
		switch(vendorID){
		case BLASTVendor.EMBL_EBI:			
			queryOptionalParameters.add(new SearchParameter("database", "em_rel_fun"));
			queryOptionalParameters.add(new SearchParameter("exp_threshold", "10"));
			queryOptionalParameters.add(new SearchParameter("score", "50"));
			queryOptionalParameters.add(new SearchParameter("match_mismatch_score", "1,-2"));
			queryOptionalParameters.add(new SearchParameter("email", ""));
			
			break;
			
		case BLASTVendor.NCBI:
			queryOptionalParameters.add(new SearchParameter("database", "nr"));
			queryOptionalParameters.add(new SearchParameter("word_size", "28"));
			queryOptionalParameters.add(new SearchParameter("exp_threshold", "10"));
			queryOptionalParameters.add(new SearchParameter("match_mismatch_score", "1,-2"));
			break;
		default:
			break;
				
		}
		
		return queryOptionalParameters;
	}
}
