package com.lityum.garaj;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class IlanDisplay extends Activity {

	private String name;
	private String newname;
	private String url_ilan;
	private String place;
	private String dates;
	private String descrip;
	private String url_image;
	private String fiyat;
	private String nick;
	private String ilanIntent;
	private String jsonStr;
	private DownloadImageTask img;
	private TextView txtLoc;
	private TextView txtDateTime;
	private TextView txtDesc;
	private TextView txtilanurl;
	private TextView txtilannick;
	private TextView txtilanfiyat;
	private static String url = "https://www.garaj.org/api/ilan-listesi-al";
	private static final String TAG_ITEMS = "ilanlar";
	private JSONArray ilans;
	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ilan_display);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		new RetrieveIlanTask().execute();
		img = new DownloadImageTask((ImageView) findViewById(R.id.ilanImage));
		txtLoc = (TextView) findViewById(R.id.txtilanYer);
		txtDateTime = (TextView) findViewById(R.id.txtilantarih);
		txtDesc = (TextView) findViewById(R.id.txtilanAciklama);
		txtilanurl = (TextView) findViewById(R.id.txturlIlan);
		txtilannick = (TextView) findViewById(R.id.txtilannick);
		txtilanfiyat = (TextView) findViewById(R.id.txtilanFiyat);
		Intent intent = IlanDisplay.this.getIntent();
		ilanIntent = (String) intent.getExtras().get("IlanTitle");

		WebView webview;
		webview = (WebView) findViewById(R.id.webviewprof);
		webview.loadUrl("http://www.garaj.org/reklam/partner/300x250");
		webview.setBackgroundColor(0x00000000);
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();

		if (!session.isLoggedIn()) {
			finish();
		}
	}

	class RetrieveIlanTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog != null) {
				// ShpDialoowing progress dialog
				pDialog.dismiss();
			} else {
				pDialog = new ProgressDialog(IlanDisplay.this);
				pDialog.setMessage("Lutfen Bekleyiniz...");
				pDialog.setCancelable(false);
				pDialog.show();
			}

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
			String offset = "1";

			String date = "2013-12-01";
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("tarih_baslangic", date));
			pairs.add(new BasicNameValuePair("ofset", offset));

			// Making a request to url and getting response
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
					ilans = obj.getJSONArray(TAG_ITEMS);
					for (int i = 0; i < ilans.length(); i++) {
						JSONObject c = ilans.getJSONObject(i);
						name = c.getString("baslik");

						if (ilanIntent.contains(name)) {
							newname = name;
							url_ilan = c.getString("url_ilan");
							place = c.getString("yer");
							dates = c.getString("tarih");
							descrip = c.getString("aciklama");
							url_image = c.getString("resim");
							fiyat = c.getString("fiyat");
							JSONObject uye = c.getJSONObject("uye");
							nick = uye.getString("nick");
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
			txtLoc.setText(Html.fromHtml(" "+place));
			txtDateTime.setText(" "+dates );
			txtDesc.setText(Html.fromHtml(" "+descrip));
			img.execute(url_image);
			img.bmImage.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					loadPhoto(img.bmImage, 1000, 1000);
				}
			});
			txtilanurl.setText(url_ilan);
			txtilannick.setText(Html.fromHtml(nick));
			txtilanfiyat.setText(fiyat);

		}
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ilan_display, menu);
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
		case R.id.signout:
			logout();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showProfile() {

		Intent profile = new Intent(IlanDisplay.this, Profile.class);
		startActivity(profile);
		finish();

	}

	public void showMessages() {
		Intent messages = new Intent(IlanDisplay.this, Messages.class);
		startActivity(messages);
		finish();

	}

	public void showFriends() {
		Intent friends = new Intent(IlanDisplay.this, Friends.class);
		startActivity(friends);
		finish();

	}

	public void logout() {
		session.logoutUser();
		finish();

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
}
