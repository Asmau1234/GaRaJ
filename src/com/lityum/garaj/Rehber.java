package com.lityum.garaj;

import java.io.IOException;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.lityum.adapters.CustomListView;
import com.lityum.adapters.RowItem;
import com.lityum.classes.Rehberlar;
import com.lityum.main.SessionManager;

public class Rehber extends FragmentActivity {

	SessionManager session;
	private GoogleMap map;
	private LocationManager locationManager;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rehber);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());

		ViewGroup mapHost = (ViewGroup) findViewById(R.id.top);
		mapHost.requestTransparentRegion(mapHost);
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.rehberMap)).getMap();
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.57,
				32.54), 3.0f));
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		map.setMyLocationEnabled(true);

		List<String> titles = new ArrayList<String>();
		List<Integer> thumb_images = new ArrayList<Integer>();
		List<String> datelist = new ArrayList<String>();
		List<String> categories = new ArrayList<String>();
		List<String> desc = new ArrayList<String>();
		ListView listview = (ListView) findViewById(R.id.ListRehber);

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		HashMap<String, String> user = session.getUserDetails();
		String name = user.get(SessionManager.KEY_NAME);
		String email = user.get(SessionManager.KEY_EMAIL);

		String feed = loadJSONFromAsset();
		Rehberlar rehber = new Rehberlar();
		try {
			JSONObject mJsonObj = new JSONObject(feed);
			JSONArray jsonArray = mJsonObj.getJSONArray("rehber");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String title = jsonObject.getString("title");
				String image = jsonObject.getString("image");
				String date = jsonObject.getString("date");
				String description = jsonObject.getString("description");
				String category = jsonObject.getString("category");
				String address = jsonObject.getString("address");
				Double phone = jsonObject.getDouble("telephone");
				Double lat = jsonObject.getDouble("lat");
				Double lang = jsonObject.getDouble("lang");

				rehber.setTitle(title);
				rehber.setDate(date);
				rehber.setImage(image);
				rehber.setDescription(description);
				rehber.setAddress(address);
				rehber.setCategory(category);
				rehber.setTelephone(phone);
				rehber.setLang(lang);
				rehber.setLat(lat);
				rehber.setCategory(category);
				titles.add(rehber.getTitle());
				int resID = getResources().getIdentifier(rehber.getImage(),
						"drawable", this.getPackageName());
				thumb_images.add(resID);
				datelist.add(" " + rehber.getDate());
				desc.add(rehber.getDescription());
				categories.add(rehber.getCategory());
				ArrayList<RowItem> rowItems = new ArrayList<RowItem>();
			/*	for (int x = 0; x < titles.size(); x++) {
					RowItem item = new RowItem(thumb_images.get(x),
							titles.get(x), datelist.get(x), categories.get(x));
					rowItems.add(item);
				}
				CustomListView adapter1 = new CustomListView(this,
						R.layout.list_item_1, rowItems);
				listview.setAdapter(adapter1);
				listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						RowItem name = (RowItem) parent
								.getItemAtPosition(position);

						Intent i = new Intent(Rehber.this, RehberDisplay.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						i.putExtra("RehberTitle", name.toString());
						startActivity(i);

					}
				});*/

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		/*DefaultHttpClient client = new DefaultHttpClient();
		// set up Garaj api URL here
		String url = "http://10.0.2.2:7001/proj/login.jsp";
		HttpPost post = new HttpPost(url);
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
																				// Limit
		String datereq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String offset = "1";
		String URL = "";
		try {

			// this is where u send the values u want returned to the JSONObject
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("date", datereq));
			pairs.add(new BasicNameValuePair("offset", offset));
			post.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = client.execute(post);
			int status = response.getStatusLine().getStatusCode();

			if (status == 200) {
				HttpEntity e = response.getEntity();
				String data = EntityUtils.toString(e);
				JSONObject last = new JSONObject(data);
				try {
					JSONArray jsonArray = last.getJSONArray("rehber");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						String title = jsonObject.getString("title");
						String image = jsonObject.getString("url_picture");
						String date = jsonObject.getString("date");
						// add String time = jsonObject.getString("time");
						String description = jsonObject
								.getString("description");
						String category = jsonObject.getString("category");
						// change category to String type =
						// jsonObject.getString("type");
						String address = jsonObject.getString("address");
						// change address to String location =
						// jsonObject.getString("location");
						Double phone = jsonObject.getDouble("telephone");
						// remove telephone
						Double lat = jsonObject.getDouble("lat");
						Double lang = jsonObject.getDouble("lang");

						rehber.setTitle(title);
						rehber.setDate(date);
						rehber.setImage(image);
						rehber.setDescription(description);
						rehber.setAddress(address);
						rehber.setCategory(category);
						rehber.setTelephone(phone);
						rehber.setLang(lang);
						rehber.setLat(lat);
						rehber.setCategory(category);
						titles.add(rehber.getTitle());
						int resID = getResources().getIdentifier(
								rehber.getImage(), "drawable",
								this.getPackageName());
						thumb_images.add(resID);
						datelist.add(" " + rehber.getDate());
						desc.add(rehber.getDescription());
						categories.add(rehber.getCategory());
						ArrayList<RowItem> rowItems = new ArrayList<RowItem>();
						for (int x = 0; x < titles.size(); x++) {
							RowItem item = new RowItem(thumb_images.get(x),
									titles.get(x), datelist.get(x),
									categories.get(x));
							rowItems.add(item);
						}
						CustomListView adapter1 = new CustomListView(this,
								R.layout.list_item_1, rowItems);
						listview.setAdapter(adapter1);
						listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								RowItem name = (RowItem) parent
										.getItemAtPosition(position);

								Intent i = new Intent(Rehber.this,
										RehberDisplay.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								i.putExtra("RehberTitle", name.toString());
								startActivity(i);

							}
						});

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}*/

		if (!session.isLoggedIn()) {
			finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rehber, menu);
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

		Intent profile = new Intent(Rehber.this, Profile.class);
		startActivity(profile);

	}

	public void showMessages() {
		Intent messages = new Intent(Rehber.this, Messages.class);
		startActivity(messages);

	}

	public void showFriends() {
		Intent friends = new Intent(Rehber.this, Friends.class);
		startActivity(friends);

	}

	public void logout() {
		session.logoutUser();
		finish();

	}

	public String loadJSONFromAsset() {
		String json;
		try {

			InputStream is = Rehber.this.getAssets().open("rehber.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;

	}

}
