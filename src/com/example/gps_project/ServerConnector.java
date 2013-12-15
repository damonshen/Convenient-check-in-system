package com.example.gps_project;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;



public class ServerConnector {

	private JSONArray _dataArray = new JSONArray();
	private HttpClient httpClient =  new  DefaultHttpClient();
	private String _id;
	
	ServerConnector(String id,JSONArray data) {
		
		// TODO Auto-generated constructor stub
		_id = id;
		_dataArray = data;
	}
	
	ServerConnector(String id){
		_id = id;
	}
	
	protected String sendLocation(){
		
		String url = "http://192.168.137.36/project/web_server/updateDatabase.php";
		HttpPost httpPost =new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id",_id));
		params.add(new BasicNameValuePair("data",_dataArray.toString()));
		   
		try {
			
			httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); //set the message of data
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			
			
			InputStream inputStream = entity.getContent();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            
            while((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            return builder.toString();
		
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
	
	protected String getCheckinNode(){
		
		String url = "http://192.168.137.36/project/getStopNode.php";
		HttpPost httpPost =new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("request",_id));
		   
		try {
			
			httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); //set the message of data
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			
			
			InputStream inputStream = entity.getContent();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            
            while((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            return builder.toString();
		
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
	
	protected String request(String url){
		HttpPost httpPost =new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id",_id));
		   
		try {
			
			httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); //set the message of data
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			
			
			InputStream inputStream = entity.getContent();
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            
            while((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            return builder.toString();
		
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
}