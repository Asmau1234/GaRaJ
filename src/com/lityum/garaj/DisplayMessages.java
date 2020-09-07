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
import android.text.Html;
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

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class DisplayMessages extends Activity {

	private SessionManager session;
	private String password;
	private String email;
	private String messageIntent;
	private DownloadImageTask img;
	private TextView txtdatesent;
	private TextView txtsubject;
	private TextView txtmessages;
	private TextView txtsender;
	private String sender;
	private String subject;
	private String message;
	private String date;
	private String image;
	private static String url = "https://www.garaj.org/api/mesaj-al";
	private static final String TAG_ITEMS = "mesajlar";
	JSONArray messages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		setContentView(R.layout.activity_display_messages);
		setTitle(R.string.title_activity_messages);
		new RetrieveMessagesTask().execute();
		txtsender = (TextView) findViewById(R.id.txtSender);
		img = new DownloadImageTask(
				(ImageView) findViewById(R.id.receivedmsgImage));
		txtdatesent = (TextView) findViewById(R.id.txtMessageDate);
		txtsubject = (TextView) findViewById(R.id.txtSubject);
		txtmessages = (TextView) findViewById(R.id.txtmessages);
		txtmessages.setMovementMethod(LinkMovementMethod.getInstance());
		Intent newintent = DisplayMessages.this.getIntent();
		messageIntent = (String) newintent.getExtras().get("SenderNick");

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		HashMap<String, String> user = session.getUserDetails();
		password = user.get(SessionManager.KEY_NAME);
		email = user.get(SessionManager.KEY_EMAIL);

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

	class RetrieveMessagesTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog != null) {
				// ShpDialoowing progress dialog
				pDialog.dismiss();
			} else {
				pDialog = new ProgressDialog(DisplayMessages.this);
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
			pairs.add(new BasicNameValuePair("sifre", password));
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET, pairs);
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
					messages = obj.getJSONArray(TAG_ITEMS);
					for (int i = 0; i < messages.length(); i++) {
						JSONObject c = messages.getJSONObject(i);
						JSONObject uye = c.getJSONObject("uye");
						String nick = uye.optString("nick");
						String date1 = c.optString("tarih");
						String konu = c.optString("konu");

						if (messageIntent.contains(nick)
								&& messageIntent.contains(konu)
								&& messageIntent.contains(date1)) {
							image = uye.optString("url_resim");
							sender = nick;
							date = date1;
							subject = konu;
							message = c.optString("mesaj");
						}
					}

				}

				catch (JSONException e) {
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
			txtdatesent.setText(Html.fromHtml(" " + date));
			txtmessages.setText(Html.fromHtml(" " + message));
			txtsubject.setText(Html.fromHtml(" " + subject));
			txtsender.setText(Html.fromHtml(" " + sender));
			img.execute(image);
			img.bmImage.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					loadPhoto(img.bmImage, 1000, 1000);
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
		getMenuInflater().inflate(R.menu.display_messages, menu);
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

		Intent profile = new Intent(DisplayMessages.this, Profile.class);
		startActivity(profile);
		finish();

	}

	public void showMessages() {
		Intent messages = new Intent(DisplayMessages.this, Messages.class);
		startActivity(messages);
		finish();

	}

	public void showFriends() {
		Intent friends = new Intent(DisplayMessages.this, Friends.class);
		startActivity(friends);
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
