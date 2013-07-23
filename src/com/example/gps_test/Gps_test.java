package com.example.gps_test;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.provider.Settings;
import android.location.*;


public class Gps_test extends Activity implements LocationListener{

	private ServerConnector connect;
	private boolean getService = false;
	private TextView message_txt;
	private static final String MAP_URL = "file:///android_asset/googleMap.html"; //the url of html of google map	
	private WebView webView; //declare the webview for google map
	
	private Handler mHandler =  new  Handler() { 
		public  void  handleMessage (Message msg) {
			//message_txt = (TextView)findViewById(R.id.message);
			//message_txt.setText((String)msg.obj);
		}
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);    
            
		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			getService = true;
			locationServiceInitial();
			
		} else {
			Toast.makeText(this, "Please open the GPS service!!", Toast.LENGTH_LONG).show();
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
		setupWebView();//load Webview   
    }
	/** Sets up the WebView object and loads the URL of the page **/
	@SuppressLint("SetJavaScriptEnabled")
	private void setupWebView(){ 
	    webView = (WebView) findViewById(R.id.google_map);
	    webView.getSettings().setJavaScriptEnabled(true);//啟用Webview的JavaScript功能
	    webView.loadUrl(MAP_URL);  //載入URL 
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gps_test, menu);
        
        menu.add(0, 0, 0, "Update");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    		case 0:
    			if(getService) {
    				lms.requestLocationUpdates(bestProvider, 100, 0, this);
    			}
    			return true;
    			
    	}
    	return false;
    }
    
    /*當位置改變的時候*/

    @Override

    public void onLocationChanged(Location location) {

        // TODO Auto-generated method stub

		getLocation(location);
		if (location !=null){    
	        //將畫面移至定位點的位置，呼叫在googlemaps.html中的centerAt函式
	        final String centerURL = "javascript:centerAt(" +
	        		location.getLatitude() + "," +
	        		location.getLongitude()+ ")";
	        webView.loadUrl(centerURL);
	      
	        final String markURL = "javascript:mark(" +
	        		location.getLatitude() + "," +
	        		location.getLongitude()+ ")";
	        webView.loadUrl(markURL);
		}
		
    }

 

    /*當GPS或是網路定位功能關閉時*/

    @Override

    public void onProviderDisabled(String provider) {

        // TODO Auto-generated method stub
    	Toast.makeText(this, "Location provider disabled", Toast.LENGTH_LONG).show();
    }

 

    /*當GPS或是網路定位功能開啟時*/

    @Override

    public void onProviderEnabled(String provider) {

        // TODO Auto-generated method stub
    	Toast.makeText(this, "Get location provider", Toast.LENGTH_LONG).show();
    }

 

    /*定位狀態改變時*/

    @Override

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    	Toast.makeText(this, "Status changed", Toast.LENGTH_LONG).show();
    }
    private LocationManager lms;

    private String bestProvider = LocationManager.GPS_PROVIDER;

    private void locationServiceInitial() {

        // TODO Auto-generated method stub

    lms = (LocationManager) getSystemService(LOCATION_SERVICE);

    Criteria criteria = new Criteria();
    bestProvider = lms.getBestProvider(criteria, true);

    Location location = lms.getLastKnownLocation(bestProvider);
    
        getLocation(location);

    }

 
	private void getLocation(Location location) {

        // TODO Auto-generated method stub

        if(location != null){

            TextView longitude_txt = (TextView)findViewById(R.id.longitude);  

            TextView latitude_txt = (TextView)findViewById(R.id.latitude);    
            
            TextView time_txt = (TextView)findViewById(R.id.time);
            
            

            Double longitude = location.getLongitude(); //蝬漲get

            Double latitude = location.getLatitude();   //蝺臬漲get
                                  
            String time_str = Long.toString(location.getTime()); //get time and convert to string
			Date date = new Date(Long.parseLong(time_str.trim()));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss"); //transform the time into format
			String dateString = formatter.format(date);
           

            longitude_txt.setText(String.valueOf(longitude));

            latitude_txt.setText(String.valueOf(latitude));
            
            time_txt.setText(String.valueOf(dateString));
            
            message_txt = (TextView)findViewById(R.id.message);
            message_txt.setText(String.valueOf(location));
            
            JSONArray jsonArr = new JSONArray();
            JSONObject jsonObj = new JSONObject();
            
            
            
            try {
            	
            	jsonObj.put("id", "091234567");			
				jsonObj.put("longitude", longitude);
				jsonObj.put("latitude", latitude);
				jsonObj.put("time", dateString);
				jsonArr.put(jsonObj);
				connect = new ServerConnector(jsonArr);
				
				new  Thread (runnable).start();  
				
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            

        }else{

            Toast.makeText(this, "Can't get your location!!", Toast.LENGTH_LONG).show();

        }

       

    }
    
    Runnable runnable = new Runnable(){  //create a runnable for sending request of http 
	      public void run(){
	    	  Message message;
	    	  message = mHandler.obtainMessage(1,connect.sendLocation());
	    	  mHandler.sendMessage(message);
	      }
	};
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(getService) {
			lms.requestLocationUpdates(bestProvider, 1, 0, this);
		}
	
	}
 
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	/*	if(getService) {
			lms.requestLocationUpdates(bestProvider, 1, 0, this);
		}
	*/
	}

}


