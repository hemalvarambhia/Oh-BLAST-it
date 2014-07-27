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

import com.bioinformaticsapp.domain.BLASTHit;

import android.util.Log;
import android.util.Xml;

public class EMBLEBIBLASTHitsParser implements BLASTHitsParser {

	private static final String TAG = "EMBLEBIBLASTHitsParser";
	
	private String endTag;
	
	private String hitTag;
	
	private String accessionNumberAttribute;
	
	private String descriptionAttribute;
	
	public EMBLEBIBLASTHitsParser(){
		endTag = "SequenceSimilaritySearchResult";
		hitTag = "hit";
		accessionNumberAttribute = "ac";
		descriptionAttribute = "description";
			
		
	}
	
	
	
	public List<BLASTHit> parse(InputStream input){
		
		if(input == null){
			throw new IllegalArgumentException("Reader cannot be null");
		}
		
		List<BLASTHit> hits = null;
		
		XmlPullParser parser = Xml.newPullParser();
		
		try {
			BLASTHit hit = null;
			Reader reader = new BufferedReader(new InputStreamReader(input));
			parser.setInput(reader);
			boolean doneReading = false;
			
			int eventType = parser.getEventType();
			
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
						String ac = parser.getAttributeValue(null, accessionNumberAttribute);
						String descript = parser.getAttributeValue(null, descriptionAttribute);
						hit.setAccessionNumber(ac);
						hit.setDescription(descript);
						
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
				eventType = parser.next();
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
