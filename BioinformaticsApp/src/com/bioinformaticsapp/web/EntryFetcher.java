package com.bioinformaticsapp.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class EntryFetcher {
	private static final String TAG = "EntryFetcher";
	
	private static final String DBFETCH_BASE_URI = "http://www.ebi.ac.uk/Tools/dbfetch/dbfetch/";
	
	private HttpClient mHttpClient;
	
	private String mEntryAccessionNumber;
	
	private Document mBlastHitsDocument = null;
	
	
	public EntryFetcher(String accessionNumber){
		mEntryAccessionNumber = accessionNumber;
		HttpParams p = new BasicHttpParams();
		HttpProtocolParams.setVersion(p, HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(p);
		
		fetchXmlDocForEntry(accessionNumber);
		
	}
	
	public String getAccessionNumber(){
		return mEntryAccessionNumber;
	}
	
	public Map<String, String> getOrganism(){
		
		Map<String, String> organismMap = new HashMap<String, String>();
        
		//If the entry is null, then add an error message
		if(mBlastHitsDocument == null){
			organismMap.put("Error", "Unable to load organism data for "+mEntryAccessionNumber);
			return organismMap;
		}
		
        NodeList organismFeature = mBlastHitsDocument.getElementsByTagName("organism");
        
        Node organism = organismFeature.item(0);
        NodeList organismChildren = organism.getChildNodes();
        for(int i = 0; i < organismChildren.getLength(); i++){
        	String name = organismChildren.item(i).getNodeName();
        	String value = organismChildren.item(i).getTextContent();
        	if(!name.startsWith("#")){
        		organismMap.put(name, value);
        	}
        }
        return organismMap;
	}
	
	public void closeHttpConnection(){
		mHttpClient.getConnectionManager().shutdown();
	}
	

	private void fetchXmlDocForEntry(String accessionNumber){
		if(accessionNumber == null || accessionNumber.length() == 0){
			throw new IllegalArgumentException("Accession number must be non-null and non-empty");
		}
		
		HttpGet getRequest = new HttpGet(DBFETCH_BASE_URI+"embl/"+accessionNumber+"/emblxml");
		getRequest.setHeader("Content-Type", "text/plain");
		getRequest.setHeader("Accept", "text/plain");
		
		ResponseHandler<String> handler = new BasicResponseHandler();
		
		String dbXmlEntry = null;
		
		try{
			dbXmlEntry = mHttpClient.execute(getRequest, handler);			
		}catch(ClientProtocolException e){
			Log.e(TAG, "Client protocol exception");
		}catch(IOException e){
			Log.e(TAG, "I/O exception");
		}
		
		if(dbXmlEntry == null){
			return;
		}
		
		BufferedReader reader = new BufferedReader(new StringReader(dbXmlEntry));
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource xmlSrc = new InputSource();
			xmlSrc.setCharacterStream(reader);
			mBlastHitsDocument = builder.parse(xmlSrc);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Parser config problem");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "SaxException while parsing");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException while parsing");
		}

		
	}
	
}
