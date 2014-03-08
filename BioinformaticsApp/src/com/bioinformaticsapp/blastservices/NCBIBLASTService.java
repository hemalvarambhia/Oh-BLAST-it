package com.bioinformaticsapp.blastservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import android.util.Log;

import com.bioinformaticsapp.models.BLASTQuery;

public class NCBIBLASTService implements BLASTSearchEngine {

	private static final String TAG = "NCBIBLASTService";
	
	private static final String NCBI_BLAST_REST_BASE_URI = "http://www.ncbi.nlm.nih.gov/blast/Blast.cgi";
	
	private DefaultHttpClient mHttpClient;
	
	public NCBIBLASTService(){
		HttpParams p = new BasicHttpParams();
		HttpProtocolParams.setVersion(p, HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(p);
		
	}
	
	public String submit(BLASTQuery query) {
		HttpPost postRequest = new HttpPost(NCBI_BLAST_REST_BASE_URI);
		postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
		postRequest.setHeader("Accept", "text/plain");
		
		//convert the query object to a list of name value pairs as required by the
		//post request
		List<NameValuePair> postRequestArguments = toNameValuePairs(query);
		
		ResponseHandler<String> handler = new BasicResponseHandler();
		String ncbiJobIdentifier = null;
		try{
			AbstractHttpEntity entity = new UrlEncodedFormEntity(postRequestArguments);
			postRequest.setEntity(entity);
			String response = mHttpClient.execute(postRequest, handler);
			Document responseDoc = Jsoup.parse(response);
			ncbiJobIdentifier = readOffJobIdentifierFrom(responseDoc);
			
		}catch(ClientProtocolException e){
			Log.e(TAG, "ClientProtocolException: the query may have been sent incorrectly. Check NCBI's documentation on sending queries");
		}catch(IOException e){
			Log.e(TAG, "IOException");
		}
		
		return ncbiJobIdentifier;
		
	}
	
	public SearchStatus pollQuery(String jobIdentifier){
		
		if(jobIdentifier == null){
			throw new IllegalArgumentException("BLAST job identifier cannot be null");
		}
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("CMD", "Get"));
		parameters.add(new BasicNameValuePair("FORMAT_OBJECT", "SearchInfo"));
		parameters.add(new BasicNameValuePair("RID", jobIdentifier));
		
		String parametersPart = URLEncodedUtils.format(parameters, HTTP.UTF_8);
		
		HttpGet getRequest = new HttpGet(NCBI_BLAST_REST_BASE_URI+"?"+parametersPart);
		ResponseHandler<String> handler = new BasicResponseHandler();
		
		String statusOfQueryString = null;
		
		try{
			String response = mHttpClient.execute(getRequest, handler);
			
			//Parse the html response string 
			Document responseDoc = Jsoup.parse(response);
			 
			//extract comments, capture the QBlastInfoBegin part and get the status
			for(Element e : responseDoc.getAllElements()){
				for(Node n: e.childNodes()){
					if(n instanceof Comment){
			             String contents = ((Comment) n).getData();
			             contents = contents.replace("\n", " ");
			             if(contents.contains("QBlastInfoBegin")){
			            	 int startIndex = contents.indexOf("Status");
			            	 int endIndex = contents.indexOf("QBlastInfoEnd");
			            	 //skip the first 9 characters, namely the 'Status = '
			            	 statusOfQueryString = contents.substring(startIndex+7, endIndex);
			            	 statusOfQueryString = statusOfQueryString.trim();
			            	 
			             }
			        }
			    }
			}
			
			
		}catch(ClientProtocolException e){
			statusOfQueryString = "ERROR";
			Log.e(TAG, "ClientProtocolException");
		}catch(IOException e){
			//There may have been a loss of connection. We reasonably assume that it is running.
			//The next the round of polling should confirm the status
			statusOfQueryString = "RUNNING";
			Log.e(TAG, "IOException");
		}
		
		if(statusOfQueryString.equals("WAITING")){
			return SearchStatus.RUNNING;
		}
		
		if(statusOfQueryString.equals("READY")){
			return SearchStatus.FINISHED;
		}
		
		if(statusOfQueryString.equals("ERROR")){
			return SearchStatus.ERROR;
		}
		
		if(statusOfQueryString.equals("UNKNOWN")){
			return SearchStatus.NOT_FOUND;
		}
		
		return SearchStatus.UNSURE;
	}
	
	public String retrieveBLASTResults(String jobIdentifier, String resultType){
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("CMD", "Get"));
		parameters.add(new BasicNameValuePair("FORMAT_TYPE", resultType));
		parameters.add(new BasicNameValuePair("RID", jobIdentifier));
		
		String parametersPart = URLEncodedUtils.format(parameters, HTTP.UTF_8);
		
		HttpGet getRequest = new HttpGet(NCBI_BLAST_REST_BASE_URI+"?"+parametersPart);
		ResponseHandler<String> handler = new BasicResponseHandler();
		
		String response = null;
		try {
			response = mHttpClient.execute(getRequest, handler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "ClientProtocolException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException");
		}
		
		return response;
		
		
	}
	
	private List<NameValuePair> toNameValuePairs(BLASTQuery query){
		
		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		
		requestParameters.add(new BasicNameValuePair("CMD", "Put"));
		requestParameters.add(new BasicNameValuePair("PROGRAM", query.getBLASTProgram()));
		requestParameters.add(new BasicNameValuePair("DATABASE", query.getSearchParameter("database").getValue()));
		requestParameters.add(new BasicNameValuePair("QUERY", query.getSequence()));
		requestParameters.add(new BasicNameValuePair("EXPECT", query.getSearchParameter("exp_threshold").getValue()));
		requestParameters.add(new BasicNameValuePair("MATCH_SCORES", query.getSearchParameter("match_mismatch_score").getValue()));
		requestParameters.add(new BasicNameValuePair("FORMAT_TYPE", "HTML"));
		
		return requestParameters;
	}
	
	private String readOffJobIdentifierFrom(Document responseDoc){
		//Get the RID
		String ncbiJobIdentifier = null;
		for(Element e : responseDoc.getAllElements()){
			for(Node n: e.childNodes()){
				if(n instanceof Comment){
		             String contents = ((Comment) n).getData();
		             contents = contents.replace("\n", " ");
		             if(contents.contains("QBlastInfoBegin")){
		            	 int startIndex = contents.indexOf("RID");
		            	 int endIndex = contents.indexOf("RTOE");
		            	 //skip the first 6 characters, namely, the 'RID = '
		            	 ncbiJobIdentifier = contents.substring(startIndex+6, endIndex);
		            	 ncbiJobIdentifier = ncbiJobIdentifier.trim();
		            	 
		             }
		        }
		    }
		}
		
		return ncbiJobIdentifier;
	}
	

	public void close(){
		mHttpClient.getConnectionManager().shutdown();
	}
}
