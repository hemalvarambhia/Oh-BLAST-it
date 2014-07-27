package com.bioinformaticsapp.domain;

public class BLASTHit {
	
	private String accessionNumber;
	
	private String description;
	
	public BLASTHit(){
		accessionNumber = "";
		description = "";
	}
	
	public void setAccessionNumber(String accessionNo){
		accessionNumber = accessionNo;
	}
	
	public void setDescription(String descript){
		description = descript;
	}
	
	public String getAccessionNumber(){
		return accessionNumber;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String toString(){
		return accessionNumber+"\t"+description;
	}
	
	public boolean equals(Object object){
		
		if(!(object instanceof BLASTHit)){
			return false;
		}
		
		if(object == this){
			return true;
		}
		
		BLASTHit other = (BLASTHit)object;
		
		if(!accessionNumber.equals(other)){
			return false;
		}
		
		if(!description.equals(other)){
			return false;
		}
		
		return true;
		
	}
	
	
	
}
