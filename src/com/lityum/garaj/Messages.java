package com.lityum.garaj;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lityum.adapters.FriendsListView;
import com.lityum.adapters.FriendsRowItem;
import com.lityum.main.ServiceHandler;
import com.lityum.main.SessionManager;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Messages extends Activity {

	SessionManager session;
	private static String url = "https://www.garaj.org/api/mesaj-al";
	 String pass;
	 String email;
	private static final String TAG_EVENTS = "mesajlar";
	JSONArray messages;
	ListView lvmessages;
	FriendsListView adapter;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		new RetrieveMessageTask().execute();

		session = new SessionManager(getApplicationContext());
		HashMap<String, String> user = session.getUserDetails();
		pass = user.get(SessionManager.KEY_NAME);
		email = user.get(SessionManager.KEY_EMAIL);
		lvmessages = (ListView) findViewById(R.id.lvMessages);
		session.checkLogin();
		if (!session.isLoggedIn()) {
			finish();
		}

	}

	class RetrieveMessageTask extends AsyncTask<Void, Void, Void> {
		ArrayList<FriendsRowItem> MessageList = new ArrayList<FriendsRowItem>();
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog != null) {

				pDialog.dismiss();
			}
			// Showing progress dialog
			else {
				pDialog = new ProgressDialog(Messages.this);
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
					messages = obj.getJSONArray(TAG_EVENTS);
					for (int i = 0; i < messages.length(); i++) {
						FriendsRowItem message = new FriendsRowItem();
						JSONObject c = messages.getJSONObject(i);
						String date=c.optString("tarih");
						String konu=c.optString("konu");
						JSONObject uye=c.getJSONObject("uye");
						String nick = uye.optString("nick");
						String img = uye.optString("url_resim");
						message.setTitle(nick + "\n" + date + "\n"+ konu);
						message.setImage(img);
						MessageList.add(message);
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
			adapter = new FriendsListView(Messages.this, R.layout.list_item_1,
					MessageList);
			lvmessages.setAdapter(adapter);
			OnItemClickListener myListViewClicked = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					FriendsRowItem name = (FriendsRowItem) parent
							.getItemAtPosition(position);
					Intent i = new Intent(Messages.this, DisplayMessages.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("SenderNick", name.toString());
					startActivity(i);

				}
			};
			lvmessages.setOnItemClickListener(myListViewClicked);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages, menu);
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

		Intent profile = new Intent(Messages.this, Profile.class);
		startActivity(profile);
		finish();

	}

	public void showMessages() {
		Intent messages = new Intent(Messages.this, Messages.class);
		startActivity(messages);
		finish();

	}

	public void showFriends() {
		Intent friends = new Intent(Messages.this, Friends.class);
		startActivity(friends);
		finish();

	}
}