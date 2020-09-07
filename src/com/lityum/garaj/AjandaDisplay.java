package com.lityum.garaj;


import java.io.IOException;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lityum.main.ServiceHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class AjandaDisplay extends Activity {

	private String name;
	private String url_event;
	private String place;
	private String dates;
	private String time;
	private String descrip;
	private String url_image;
	private String url_facebook;
	private String newname;
	private String ajandaIntent;
	private String ajandaIntent2;
	private String jsonStr;
	private DownloadImageTask img;
	private TextView txtLoc;
	private TextView txtDateTime;
	private TextView txtDesc;
	private TextView txteventurl;
	private TextView txtfacebookevent;
	private JSONArray events;
	private static String url = "https://www.garaj.org/api/etkinlik-listesi-al";
	private static final String TAG_EVENTS = "etkinlikler";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajanda_display);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		Activity activity= this;
		activity.findViewById(android.R.id.content).setBackgroundColor(Color.LTGRAY);
		
		new RetrieveAjandaTask().execute();
		img=new DownloadImageTask((ImageView) findViewById(R.id.imgAjanda));
		txtLoc = (TextView) findViewById(R.id.txtLocationAjanda);
		txtDateTime = (TextView) findViewById(R.id.txtDatetimeAjanda);
		txtDesc = (TextView) findViewById(R.id.txtDescAjanda);
		txteventurl = (TextView) findViewById(R.id.url_etkinlik);
		txtfacebookevent = (TextView) findViewById(R.id.url_facebook);
		Intent intent = AjandaDisplay.this.getIntent();
		Bundle extras = intent.getExtras();
		ajandaIntent=extras.getString("AjandaTitle");
		ajandaIntent2= extras.getString("RequestDate");
		
		WebView webview;
		webview = (WebView) findViewById(R.id.webviewprof);
		webview.loadUrl("http://www.garaj.org/reklam/partner/300x250");
		webview.setBackgroundColor(0x00000000);

	}

	class RetrieveAjandaTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog != null) {
				// ShpDialoowing progress dialog
				pDialog.dismiss();
			} else {
				pDialog = new ProgressDialog(AjandaDisplay.this);
				pDialog.setMessage("Lutfen Bekleyiniz...");
				pDialog.setCancelable(false);
				pDialog.show();
			}

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("tarih_baslangic", ajandaIntent2));
			jsonStr = sh.makeServiceCall(url, ServiceHandler.GET, pairs);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				JSONObject jsonObj = null;
				try {
					jsonObj = new JSONObject(jsonStr);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					JSONObject obj = jsonObj.getJSONObject("data");
					events = obj.getJSONArray(TAG_EVENTS);
					for (int i = 0; i < events.length(); i++) {
						JSONObject c = events.getJSONObject(i);
						name = c.getString("isim");
						
						if(ajandaIntent.contains(name)){
							newname = name;
							url_event = c.getString("url_etkinlik");
							place = c.getString("yer");
							dates = c.getString("tarih");
							time = c.getString("saat");
							descrip = c.getString("aciklama");
							url_image = c.getString("resim_buyuk");
							url_facebook = c.getString("url_kaynak");
						}
						
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			if (pDialog.isShowing())
				pDialog.dismiss();

			    setTitle(newname);
				txtLoc.setText(" "+place);
				txtDateTime.setText(" "+dates + " " + time);
				txtDesc.setText(" "+ descrip);
				txteventurl.setText(" "+url_event);
				txtfacebookevent.setText(" "+url_facebook);
				img.execute(url_image);
				
			

		}
	}
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

		  protected Bitmap doInBackground(String... params) {
	            // TODO Auto-generated method stub
	            String urlStr = params[0];
	            Bitmap img = null;

	            HttpClient client = new DefaultHttpClient();
	            HttpGet request = new HttpGet(urlStr);
	            HttpResponse response;
	            try {
	                response = (HttpResponse)client.execute(request);           
	                HttpEntity entity = response.getEntity();
	                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
	                InputStream inputStream = bufferedEntity.getContent();
	                img = BitmapFactory.decodeStream(inputStream);
	            } catch (ClientProtocolException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            return img;
	        }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ajanda_display, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.profile:
			showProfile();
			return true;
		case R.id.messages:
			showMessages();
			return true;
		case R.id.friends:
			showFriends();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	public void showProfile() {

		Intent profile = new Intent(AjandaDisplay.this, Profile.class);
		startActivity(profile);
		finish();

	}

	public void showMessages() {
		Intent messages = new Intent(AjandaDisplay.this, Messages.class);
		startActivity(messages);
		finish();

	}

	public void showFriends() {
		Intent friends = new Intent(AjandaDisplay.this, Friends.class);
		startActivity(friends);
		finish();

	}

	
}
