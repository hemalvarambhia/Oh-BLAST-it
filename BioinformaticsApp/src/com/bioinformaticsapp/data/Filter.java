package com.bioinformaticsapp.data;

import java.io.Serializable;

public class Filter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3060706459738952469L;

	public Filter(String where, String[] args){
		this.where = where;
		this.arguments = args;
	}
	
	public String condition(){
		return where;
	}
	
	public String[] arguments(){
		return arguments;
	}
	
	private final String where;
	
	private final String[] arguments;
	
}
