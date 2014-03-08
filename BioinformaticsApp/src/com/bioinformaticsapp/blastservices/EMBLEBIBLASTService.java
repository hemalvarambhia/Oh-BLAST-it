package com.bioinformaticsapp.blastservices;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import com.bioinformaticsapp.models.BLASTQuery;

public class EMBLEBIBLASTService implements BLASTSearchEngine {

	private static final String TAG = "EMBLEBIBLASTService";
	
	private static final String EMBL_BLAST_REST_BASE_URI = "http://www.ebi.ac.uk/Tools/services/rest/ncbiblast/";
	
	public static String CLIENT_PROTOCOL_ERROR_MESSAGE = "Could not retrieve response due to client protocol problem";
	
	public static String IO_ERROR_MESSAGE = "There was an input/output problem while submitting the job";
	
	private DefaultHttpClient mHttpClient;
	
	public EMBLEBIBLASTService(){
		HttpParams p = new BasicHttpParams();
		HttpProtocolParams.setVersion(p, HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(p);
	}
	
	public String submit(BLASTQuery query) {
		HttpPost postRequest = new HttpPost(EMBL_BLAST_REST_BASE_URI+"run/");
		postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
		postRequest.setHeader("Accept", "text/plain");
		List<NameValuePair> requestParameters = toNameValuePairs(query);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String ebiJobIdentifier = null;
		try {
			AbstractHttpEntity entry = new UrlEncodedFormEntity(requestParameters);		
			postRequest.setEntity(entry);
			ebiJobIdentifier = mHttpClient.execute(postRequest, handler);
			ebiJobIdentifier = ebiJobIdentifier.replace("\n", "");
				
		} catch (ClientProtocolException e){
			Log.w(TAG, "There was an attempt to incorrectly send the query. Check the service's documentation");
		}catch (IOException e) {
			Log.e(TAG, Arrays.toString(e.getStackTrace()));
		} 
		
		return ebiJobIdentifier;
	}
	
	public SearchStatus pollQuery(String jobIdentifier) {
		
		if(jobIdentifier == null){
			throw new IllegalArgumentException("Job identifier was null");
		}
		
		HttpGet getRequest = new HttpGet(EMBL_BLAST_REST_BASE_URI+"status/"+jobIdentifier);
		getRequest.setHeader("Content-Type", "text/plain");
		getRequest.setHeader("Accept", "text/plain");
		
		ResponseHandler<String> handler = new BasicResponseHandler();
		
		String statusOfQueryString = null;
		try{
			statusOfQueryString = mHttpClient.execute(getRequest, handler);
			
			statusOfQueryString = statusOfQueryString.replace("\n", "");
		}catch(ClientProtocolException e){
			statusOfQueryString = "ERROR";
			Log.e(TAG, "ClientProtocolException");
		}catch(IOException e){
			//There may have been a loss of connection. We reasonably assume that it is running.
			//The next the round of polling should confirm the status
			statusOfQueryString = "RUNNING";
			Log.e(TAG, "IOException");
		}
		SearchStatus statusOfQuery = SearchStatus.valueOf(statusOfQueryString);
		
		return statusOfQuery;
		
	}
	
	public void close(){
		mHttpClient.getConnectionManager().shutdown();
	}
	
	public String retrieveBLASTResults(String jobIdentifier, String resultType){
		
		if(jobIdentifier == null){
			throw new IllegalArgumentException("job identifier was null");
		}
		
		String type = "xml";
		
		if(resultType != null){
			type = resultType;
		}
		
		HttpGet getRequest = new HttpGet(EMBL_BLAST_REST_BASE_URI+"result/"+jobIdentifier+"/"+type);
		getRequest.setHeader("Content-Type", "text/plain");
		getRequest.setHeader("Accept", "text/plain");
		
		ResponseHandler<String> handler = new BasicResponseHandler();
		
		String response = null;
		try{
			response = mHttpClient.execute(getRequest, handler);
			response = response.replace("\n", "");
		}catch(ClientProtocolException e) {
			response = "Client protocol problem: "+e.getMessage();
		}catch(IOException e) {
			response = "I/O problem: "+e.getMessage();
		}finally {
			close();
		}
		
		return response;
	}
	
	private List<NameValuePair> toNameValuePairs(BLASTQuery query){
		
		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		requestParameters.add(new BasicNameValuePair("email", query.getSearchParameter("email").getValue()));
		requestParameters.add(new BasicNameValuePair("program", query.getBLASTProgram()));
		requestParameters.add(new BasicNameValuePair("database", query.getSearchParameter("database").getValue()));
		requestParameters.add(new BasicNameValuePair("sequence", query.getSequence()));
		requestParameters.add(new BasicNameValuePair("stype", "dna"));
		requestParameters.add(new BasicNameValuePair("scores",query.getSearchParameter("score").getValue()));
		requestParameters.add(new BasicNameValuePair("exp", query.getSearchParameter("exp_threshold").getValue()));
		
		return requestParameters;
	}
	/**
	public List<BLASTHit> getBLASTHitsForQuery(String jobIdentifier){
		
		String xml = retrieveBLASTResults(jobIdentifier, "xml");
		StringReader reader = new StringReader(xml);
		
		EMBLEBIBLASTHitsParser parser = new EMBLEBIBLASTHitsParser();
		List<BLASTHit> blastHits = parser.parse(reader);
		
		return blastHits;
		
		
	}
	*/
	@SuppressWarnings("unused")
	private class HttpResender implements HttpRequestRetryHandler {

		public boolean retryRequest(IOException exception, int executionCount,
				HttpContext context) {
			if(executionCount > 5){
				return false;
			}
			
			if(exception instanceof SocketException){
				return true;
			}
			
			return false;
		}
		
	}

}
