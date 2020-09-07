package com.lityum.garaj;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lityum.adapters.FriendsRowItem;
import com.lityum.main.ServiceHandler;
import com.lityum.main.SessionManager;



import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsDisplay extends Activity {
	String nick;
	String yas;
	String tanitim;
	String ilgi;
	String entruman;
	String urlprof;
	String sehir;
	String urlimage;
	String newnick;
	private TextView txtnick;
	private TextView txtyas;
	private TextView txttanitim;
	private TextView txtilgi;
	private TextView txtentruman;
	private TextView txturlprof;
	private TextView txtsehir;
	private DownloadImageTask img;
	SessionManager session;
	private static String url = "https://www.garaj.org/api/es-dost-listesi-al";
	private String pass;
	private String email;
	private static final String TAG_EVENTS = "es-dost";
	JSONArray friends;
	FriendsRowItem friend;
	private String friendsIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_display);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		new DisplayFriendsTask().execute();
		session = new SessionManager(getApplicationContext());
		HashMap<String, String> user = session.getUserDetails();
		pass = user.get(SessionManager.KEY_NAME);
		email = user.get(SessionManager.KEY_EMAIL);
		session.checkLogin();
		if (!session.isLoggedIn()) {
			finish();
		}
		
		img=new DownloadImageTask((ImageView) findViewById(R.id.ilanImage));
		txtnick=(TextView)findViewById(R.id.displayNick);
		txtyas=(TextView)findViewById(R.id.displayYas);
		txttanitim=(TextView)findViewById(R.id.displayTanitim);
		txtilgi=(TextView)findViewById(R.id.dsiplayIlgi);
		txtentruman=(TextView)findViewById(R.id.displayEntruman);
		txturlprof=(TextView)findViewById(R.id.displayProfile);
		txtsehir=(TextView)findViewById(R.id.displaySehir);
		Intent intent = FriendsDisplay.this.getIntent();
		friendsIntent = (String) intent.getExtras().get("FriendName");
		
		WebView webview;
		webview = (WebView) findViewById(R.id.webviewfriend);
		webview.loadUrl("http://www.garaj.org/reklam/partner/300x250");
		webview.setBackgroundColor(0x00000000);
	}
	
	class DisplayFriendsTask extends AsyncTask<Void, Void, Void> {
		
		ArrayList<FriendsRowItem> FriendList = new ArrayList<FriendsRowItem>();
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog != null) {

				pDialog.dismiss();
			}
			// Showing progress dialog
			else {
				pDialog = new ProgressDialog(FriendsDisplay.this);
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
			pairs.add(new BasicNameValuePair("eposta", email));
			pairs.add(new BasicNameValuePair("sifre", pass));

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET, pairs);

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
					friends = obj.getJSONArray(TAG_EVENTS);
					for (int i = 0; i < friends.length(); i++) {
						JSONObject c = friends.getJSONObject(i);
						nick = c.getString("nick");

						 if(friendsIntent.contains(nick)){
							 newnick = nick;
							 yas=c.getString("yas");
							 tanitim=c.getString("tanitim");
							 ilgi=c.getString("ilgi");
							 entruman=c.getString("entruman");
							 urlprof=c.getString("url_profil");
							 sehir=c.getString("sehir");
							 urlimage=c.getString("url_resim");
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
			 txtnick.setText(newnick);
			txtyas.setText(yas);
			 txttanitim.setText(tanitim);
			 txtilgi.setText(ilgi);
			txtentruman.setText(entruman);
			txturlprof.setText(urlprof);
			txtsehir.setText(sehir);
			img.execute(urlimage);
			img.bmImage.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                loadPhoto(img.bmImage,1000,1000);
	            }
	        });
		}

	}
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	           // Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	    }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends_display, menu);
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
	private void loadPhoto(ImageView imageView, int width, int height) {

        ImageView tempImageView = imageView;


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.ustom_fullimage_dialog,
                (ViewGroup) findViewById(R.id.layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        image.setImageDrawable(tempImageView.getDrawable());
        imageDialog.setView(layout);
        imageDialog.setPositiveButton((R.string.ok_button), new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });


        imageDialog.create();
        imageDialog.show();     
    }
	public void showProfile() {

		Intent profile = new Intent(FriendsDisplay.this, Profile.class);
		startActivity(profile);
		finish();

	}

	public void showMessages() {
		Intent messages = new Intent(FriendsDisplay.this, Messages.class);
		startActivity(messages);
		finish();

	}

	public void showFriends() {
		Intent friends = new Intent(FriendsDisplay.this, Friends.class);
		startActivity(friends);
		finish();

	}
}
