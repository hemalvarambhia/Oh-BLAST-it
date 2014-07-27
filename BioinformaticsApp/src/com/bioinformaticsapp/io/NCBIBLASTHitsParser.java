package com.bioinformaticsapp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.bioinformaticsapp.domain.BLASTHit;

public class NCBIBLASTHitsParser implements BLASTHitsParser {
	
	private static String TAG = "NCBIBLASTHitsParser";
	
	private String hitTag = "Hit";
	private String endTag = "Iteration";
	private String accessionNumberTag = "Hit_accession";
	private String descriptionTag = "Hit_def";
	
	
	public List<BLASTHit> parse(InputStream input) throws Exception{
		
		if(input == null){
			throw new Exception("In order to parse an NCBI BLAST output xml file, Reader cannot be null");
		}
		
		List<BLASTHit> hits = null;
		
		XmlPullParser parser = Xml.newPullParser();
		Reader reader = new BufferedReader(new InputStreamReader(input));
		parser.setInput(reader);
		
		int eventType = parser.getEventType();
		BLASTHit hit = null;
		boolean doneReading = false;
		try{
		while((eventType != XmlPullParser.END_DOCUMENT) && !doneReading){
			String tagName = null;
			
			switch(eventType){
			case XmlPullParser.START_DOCUMENT:
				hits = new ArrayList<BLASTHit>();
				break;
				
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				if(tagName.equalsIgnoreCase(hitTag)){
					
					hit = new BLASTHit();
					
				}
				
				if(tagName.equals(descriptionTag)){
					
					hit.setDescription(parser.nextText().replaceAll("\n", ""));					
				
				}
				
				if(tagName.equals(accessionNumberTag)){
					
					hit.setAccessionNumber(parser.nextText().replaceAll("\n", ""));					
					
				}
				
				
				break;
			
				
			case XmlPullParser.END_TAG:
				tagName = parser.getName();
				
				if(tagName.equalsIgnoreCase(hitTag) && hit != null){
					hits.add(hit);
				}else if(tagName.equalsIgnoreCase(endTag)){
					doneReading = true;
				}
				break;
			}
			eventType = parser.nextToken();
		}
		
	} catch (XmlPullParserException e) {
		// TODO Auto-generated catch block
		Log.e(TAG, "problem while parsing: "+e.getMessage());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		Log.e(TAG, "problem while iterating through xml file");
	}
	
		
		return hits;
	}
	
 

}
