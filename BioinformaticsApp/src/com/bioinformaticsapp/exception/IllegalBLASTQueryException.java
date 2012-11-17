package com.bioinformaticsapp.exception;

public class IllegalBLASTQueryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3837985058679230367L;

	public IllegalBLASTQueryException(String errorMessage){
		super(errorMessage);
	}
	
}
