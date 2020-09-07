package com.lityum.garaj;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.lityum.main.ServiceHandler;
import com.lityum.main.SessionManager;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class Profile extends Activity {

	SessionManager session;
	TextView txtname;
	TextView txtage;
	TextView txtcity;
	TextView txttanitim;
	TextView txtilgi;
	TextView txtentruman;
	TextView urlprofile;
	DownloadImageTask profilepic;
	String nick;
	String yas;
	String sehir;
	String tanitim;
	String ilgi;
	String entruman;
	String urlprof;
	String urlimg;

	private static String url = "https://www.garaj.org/api/uye-girisi-yap";
	JSONObject user;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		new RetrieveProfileTask().execute();
		txtname = (TextView) findViewById(R.id.txtName);
		txtage = (TextView) findViewById(R.id.txtAge);
		txtcity = (TextView) findViewById(R.id.txtCity);
		txttanitim = (TextView) findViewById(R.id.txtTanitim);
		txtilgi = (TextView) findViewById(R.id.txtIlgi);
		txtentruman = (TextView) findViewById(R.id.txtEntruman);
		urlprofile = (TextView) findViewById(R.id.txturl_profile);
		profilepic = new DownloadImageTask(
				(ImageView) findViewById(R.id.profilePic));

		urlprofile.setMovementMethod(LinkMovementMethod.getInstance());
		WebView webview;
		webview = (WebView) findViewById(R.id.webviewprof);
		webview.loadUrl("http://www.garaj.org/reklam/partner/300x250");
		webview.setBackgroundColor(0x00000000);

	}

	class RetrieveProfileTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog != null) {
				// ShpDialoowing progress dialog
				pDialog.dismiss();
			} else {
				pDialog = new ProgressDialog(Profile.this);
				pDialog.setMessage("Lutfen Bekleyiniz...");
				pDialog.setCancelable(false);
				pDialog.show();
			}

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
			session = new SessionManager(getApplicationContext());
			session.checkLogin();

			HashMap<String, String> user = session.getUserDetails();
			String password = user.get(SessionManager.KEY_NAME);
			String email = user.get(SessionManager.KEY_EMAIL);
			if (!session.isLoggedIn()) {
				finish();
			}
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("eposta", email));
			pairs.add(new BasicNameValuePair("sifre", password));

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET, pairs);

			Log.d("Response: ", "> " + jsonStr);
			if (jsonStr != null) {
				try {
					JSONObject json = new JSONObject(jsonStr);
					JSONObject data = json.getJSONObject("data");
					nick = data.getString("nick");
					yas = data.getString("yas");
					sehir = data.getString("sehir");
					tanitim = data.optString("tanitim");
					ilgi = data.getString("ilgi");
					entruman = data.getString("entruman");
					urlprof = data.getString("url_profil");
					urlimg = data.optString("url_resim");

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			return null;
		}

		protected void onPostExecute(Void result) {

			if (pDialog.isShowing())
				pDialog.dismiss();

			Log.i("thread", "Done...");
			txtname.setText(" " + nick);
			txtage.setText(" " + yas);
			txtcity.setText(" " + sehir);
			txttanitim.setText(" " + tanitim);
			txtilgi.setText(" " + ilgi);
			txtentruman.setText(" " + entruman);
			urlprofile.setText(" " + urlprof);
			profilepic.execute(urlimg);
			profilepic.bmImage.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					loadPhoto(profilepic.bmImage, 1000, 1000);
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
				Log.e("Error", e.getMessage());
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
		getMenuInflater().inflate(R.menu.profile, menu);
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
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.ustom_fullimage_dialog,
				(ViewGroup) findViewById(R.id.layout_root));
		ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
		image.setImageDrawable(tempImageView.getDrawable());

		imageDialog.setView(layout);
		imageDialog.setPositiveButton((R.string.ok_button),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});

		imageDialog.create();
		imageDialog.show();
	}

	public void showProfile() {

		Intent profile = new Intent(Profile.this, Profile.class);
		startActivity(profile);

	}

	public void showMessages() {
		Intent messages = new Intent(Profile.this, Messages.class);
		startActivity(messages);

	}

	public void showFriends() {
		Intent friends = new Intent(Profile.this, Friends.class);
		startActivity(friends);

	}

}
