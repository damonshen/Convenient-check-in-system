package com.example.gps_project;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.model.GraphUser;

public class CheckinPage extends Activity{
	public Session session;
	public String placeID;
	public String tagID;
	public JSONObject coordinate;
	public String Start_time;
	public String End_time;
	public long time_gap = 1800000;
	static String test = "";
	static JSONArray test2 = new JSONArray();
	static private int num_of_photos;
	private ArrayList<fileinfo> filelist= new ArrayList<fileinfo>();
	private Boolean with_picture = false;
	private final static int PHOTO = 66 ;
	
	public TextView friendtext;
	public ImageView image;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.checkinpage);
    	Bundle extras = getIntent().getExtras();
    	placeID = extras.getString("place");
    	Start_time = extras.getString("Start");
    	End_time = extras.getString("End");
    	try {
			coordinate = new JSONObject(extras.getString("coordinates"));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	
    	
    	friendtext = (TextView)findViewById(R.id.b);
    	Button Checkin = (Button)findViewById(R.id.Checkin);
    	Button addfriends = (Button)findViewById(R.id.friend);
    	Button prev = (Button)findViewById(R.id.prev);
    	Button next = (Button)findViewById(R.id.next);
    	Button scale = (Button)findViewById(R.id.scale);
    	Button mPhoto = (Button) findViewById(R.id.pickphoto);
    	SimpleDateFormat gps_fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	SimpleDateFormat fmt = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
    	Date tem_date = new Date();
    	long stime, etime, half = 0, middle = 0;
    	try {
			tem_date = gps_fmt.parse(Start_time);
			stime = tem_date.getTime();
			tem_date = gps_fmt.parse(End_time);
			etime = tem_date.getTime();
			half = (etime - stime) / 2 + 900000;
			middle = (etime + stime) / 2;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	final String sFileName=Environment.getExternalStorageDirectory()+"/DCIM/100MEDIA/";
    	File file = new File(sFileName);
    	int i = 0;
        try{
	        for(i = 0; i < file.list().length; i++){
	        	String name=file.list()[i];
	        	ExifInterface exif = new ExifInterface(sFileName+name);
	            String date=exif.getAttribute(ExifInterface.TAG_DATETIME);
	            tem_date = fmt.parse(date);
	            if(Math.abs(tem_date.getTime()- middle) < half)
	            	filelist.add(new fileinfo(date, sFileName+name));
	        }
        }catch(Exception ee){
        	Log.v("error", file.list()[i]);
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inSampleSize = 4;
    	Bitmap tem;
    	image = (ImageView)findViewById(R.id.image1);
    	prev.setEnabled(false);
    	next.setEnabled(false);
    	scale.setEnabled(false);
        num_of_photos = 0;
        if(!filelist.isEmpty()){
        	if(filelist.size()>1){
        		prev.setEnabled(true);
            	next.setEnabled(true);
        	}
            scale.setEnabled(true);
        	tem = BitmapFactory.decodeFile(filelist.get(num_of_photos).Name, options);
        	image.setImageBitmap(tem);
        }
        
        prev.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap tem;
				num_of_photos--;
	        	if(num_of_photos < 0)
	        		num_of_photos = filelist.size()-1;
				tem = BitmapFactory.decodeFile(filelist.get(num_of_photos).Name, options);
	        	image.setImageBitmap(tem);
			}
        	
        });
        
        next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap tem;
				num_of_photos++;
	        	if(num_of_photos > filelist.size()-1)
	        		num_of_photos = 0;
				tem = BitmapFactory.decodeFile(filelist.get(num_of_photos).Name, options);
	        	image.setImageBitmap(tem);
			}
        	
        });
        
        scale.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bitmap tem;
				LayoutInflater inflater = LayoutInflater.from(CheckinPage.this); 
				View v_scale = inflater.inflate(R.layout.imagepreview, null);
				/*ImageView preview = (ImageView)v_scale.findViewById(R.id.preview); 
				tem = BitmapFactory.decodeFile(filelist.get(num_of_photos).Name, options);
	        	preview.setImageBitmap(tem);*/
	        	//new AlertDialog.Builder(CheckinPage.this).setTitle("Preview").setView(v).show();
	        	Intent myIntent = new Intent(getApplicationContext(), Scale.class);
	        	Bundle param = new Bundle();
	        	param.putString("path", filelist.get(num_of_photos).Name);
	        	myIntent.putExtras(param);
	            startActivityForResult(myIntent, 3);
			}
        	
        });
        
        mPhoto.setOnClickListener(new OnClickListener()
        {
           @Override
           public void onClick(View v) 
           {
           /*���貊倏�貊���*/
           Intent intent = new Intent();
           intent.setType("image/*");
           intent.setAction(Intent.ACTION_GET_CONTENT);
           startActivityForResult(intent, 2);
           }

  		
        });
        
    	Checkin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText editText = (EditText) (findViewById(R.id.edittext));
				String message = editText.getText().toString();
				message+=("\n"+Start_time);
				Bundle params = new Bundle();
            	params.putString("place", placeID);
            	params.putString("message", message);

                
            	options.inSampleSize = 2;
            	if(!filelist.isEmpty()){
	            	byte[] data = null;
	            	Bitmap bi = BitmapFactory.decodeFile(filelist.get(num_of_photos).Name, options);
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                bi.compress(Bitmap.CompressFormat.PNG, 100, baos);
	                data = baos.toByteArray();
	                params.putByteArray("picture", data);
	                params.putString("tags", test2.toString());
	                new RequestAsyncTask(new Request(Session.getActiveSession(), "me/photos", params, HttpMethod.POST, new Request.Callback() {
						
						@Override
						public void onCompleted(Response response) {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(CheckinPage.this).setTitle("123").setMessage(response.toString()).show();
						}
					})).execute();
            	}
            	else{            
                
            		params.putString("tags", test);
	            	new RequestAsyncTask(new Request(Session.getActiveSession(), "me/feed", params, HttpMethod.POST, new Request.Callback() {
						
						@Override
						public void onCompleted(Response response) {
							// TODO Auto-generated method stub
							new AlertDialog.Builder(CheckinPage.this).setTitle("123").setMessage(response.toString()).show();
						}
					})).execute();
            	}
            	
            	
			}
    		
    	});
    	
    	addfriends.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FriendPickerApplication application = (FriendPickerApplication) getApplication();
	            application.setSelectedUsers(null);

	            Intent intent = new Intent(CheckinPage.this, PickFriendsActivity.class);
	            // Note: The following line is optional, as multi-select behavior is the default for
	            // FriendPickerFragment. It is here to demonstrate how parameters could be passed to the
	            // friend picker if single-select functionality was desired, or if a different user ID was
	            // desired (for instance, to see friends of a friend).
	            PickFriendsActivity.populateParameters(intent, null, true, true);
	            startActivityForResult(intent, 1);
			}});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode){
    	case 1:
    		displaySelectedFriends(resultCode);
    		break;
    	case 2:
    		//���抒�頝臬�uri嚗蒂頧�absolute path
    		if(data == null)
    			break;
    		Uri uri = data.getData();
            String path = getRealPathFromURI(this, uri);
                         
            //霈���抒�嚗��Bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
        	options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
	        image = (ImageView)findViewById(R.id.image1);            
	        image.setImageBitmap(bitmap);
	        filelist.add(new fileinfo(null, path));
	        num_of_photos = filelist.size()-1;
	        Button prev = (Button)findViewById(R.id.prev);
	    	Button next = (Button)findViewById(R.id.next);
	    	if(filelist.size() > 1){
	    		prev.setEnabled(true);
	    		next.setEnabled(true);
	    	}
    		break;
    	case 3:
    		Log.v("Back", Integer.toString(filelist.size()));
    		break;
    	default:
    		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    		break;
    }
    }
	
	private void displaySelectedFriends(int resultCode) {
        tagID = "";
        String tagname = "";
        FriendPickerApplication application = (FriendPickerApplication) getApplication();
        int i = 0;
        Collection<GraphUser> selection = application.getSelectedUsers();
        if (selection != null && selection.size() > 0) {
        	ArrayList<String> ids = new ArrayList<String>();
            ArrayList<String> names = new ArrayList<String>();
            for (GraphUser user : selection) {
                ids.add(user.getId());
                names.add(user.getName());
                test += user.getId() +", ";
                try {
					test2.put(new JSONObject().put("tag_uid", user.getId()));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                //test2[i] = "{\"tag_uid\":"+user.getId()+",\"x\":0,\"y\":0}";
                i++;
            }
            
            tagID = TextUtils.join(", ", ids);
            tagname = TextUtils.join(", ", names);
            friendtext.setText("You Choose these friends: " + tagname);
        } else {
            tagID = "";
        }
	}
	public String getRealPathFromURI(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		  } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
		}
	
	public class fileinfo{
		String Date;
		String Name;
		
		fileinfo(String date, String name){
			Date = date;
			Name = name;
		}
	}
}
