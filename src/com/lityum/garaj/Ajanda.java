package com.lityum.garaj;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.location.LocationListener;
import com.lityum.adapters.CustomListView;
import com.lityum.adapters.RowItem;
import com.lityum.main.ServiceHandler;
import com.lityum.main.SessionManager;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class Ajanda extends Activity implements
		android.location.LocationListener {

	private JSONArray events;
	private SessionManager session;
	private ListView listview;
	private RowItem ajanda;
	private static String url = "https://www.garaj.org/api/etkinlik-listesi-al";
	private static final String TAG_EVENTS = "etkinlikler";
	private ArrayList<RowItem> eventList = new ArrayList<RowItem>();
	private OnItemClickListener myListViewClicked;
	private DatePicker dp;
	private Button go;
	private int year;
	private int month;
	private int day;
	private NotificationManager NM;
	private Context context;
	private String loc;
	private String newmonth;
	private CustomListView adapter;
	private String date;
	private LocationManager locationManager;
	private String provider;
	private double latitude = 0;
	private double longitude = 0;

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajanda);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Activity activity = this;
		activity.findViewById(android.R.id.content).setBackgroundColor(
				Color.LTGRAY);

		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} else {
			locationManager = getLocationUpdates();
			Log.e("GooseLib", "GeoLocation - Location not available");
		}

		new RetrieveAjandaTask().execute();
		listview = (ListView) findViewById(R.id.ListAjanda);

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		dp = (DatePicker) findViewById(R.id.datePicker1);
		dp.init(year, month, day, new OnDateChangedListener() {

			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(year, monthOfYear, dayOfMonth);
				final String str1 = Integer.toString(year);
				String str2 = null;
				String str3 = null;
				if (monthOfYear >= 0 && monthOfYear < 10) {
					str2 = 0 + Integer.toString(monthOfYear + 1);

				} else if (monthOfYear >= 10) {
					str2 = Integer.toString(monthOfYear + 1);
				}
				if (dayOfMonth < 10) {

					str3 = 0 + Integer.toString(dayOfMonth);
				} else {
					str3 = Integer.toString(dayOfMonth);
				}

				date = str1 + "-" + str2 + "-" + str3;
				System.out.println("TEST");
			}
		});

		go = (Button) findViewById(R.id.ajandaSearch);
		go.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				listview.setAdapter(null);
				eventList.clear();
				new RetrieveAjandaGoTask().execute();
				System.out.println(date);

			}
		});

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		if (!session.isLoggedIn()) {
			finish();
		}

	}

	class RetrieveAjandaTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog == null) {
				// Showing progress dialog
				pDialog = new ProgressDialog(Ajanda.this);
				pDialog.setMessage("Lutfen Bekleyiniz...");
				pDialog.setCancelable(false);
				pDialog.show();
			} else {
				pDialog.dismiss();
			}

		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

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
						ajanda = new RowItem();
						ajanda.setDate(c.optString("tarih_txt"));
						ajanda.setDesc(c.optString("aciklama"));
						ajanda.setPlace(c.optString("yer"));
						ajanda.setTitle(c.optString("isim"));
						ajanda.setTime(c.optString("saat"));
						ajanda.setImage(c.getString("resim_kucuk"));

						eventList.add(ajanda);
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
			adapter = new CustomListView(Ajanda.this, R.layout.list_item_1,
					eventList);
			listview.setAdapter(adapter);
			myListViewClicked = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					RowItem name = (RowItem) parent.getItemAtPosition(position);
					Intent i = new Intent(Ajanda.this, AjandaDisplay.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("AjandaTitle", name.toString());
					startActivity(i);
					finish();

				}
			};
			listview.setOnItemClickListener(myListViewClicked);

		}
	}

	class RetrieveAjandaGoTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog == null) {
				// Showing progress dialog
				pDialog = new ProgressDialog(Ajanda.this);
				pDialog.setMessage("Please wait...");
				pDialog.setCancelable(false);
				pDialog.show();
			} else {
				pDialog.dismiss();
			}

		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			String offset = "";
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("tarih_baslangic", date));
			pairs.add(new BasicNameValuePair("ofset", offset));

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
					events = obj.getJSONArray(TAG_EVENTS);

					for (int i = 0; i < events.length(); i++) {
						JSONObject c = events.getJSONObject(i);
						ajanda = new RowItem();
						ajanda.setDate(c.optString("tarih_txt"));
						ajanda.setDesc(c.optString("aciklama"));
						ajanda.setPlace(c.optString("yer"));
						ajanda.setTitle(c.optString("isim"));
						ajanda.setTime(c.optString("saat"));
						ajanda.setImage(c.getString("resim_kucuk"));

						eventList.add(ajanda);
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
			adapter = new CustomListView(Ajanda.this, R.layout.list_item_1,
					eventList);
			listview.setAdapter(adapter);
			myListViewClicked = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					RowItem name = (RowItem) parent.getItemAtPosition(position);
					Intent i = new Intent(Ajanda.this, AjandaDisplay.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle extras = new Bundle();
					extras.putString("AjandaTitle", name.toString());
					extras.putString("RequestDate", date);
					i.putExtras(extras);
					startActivity(i);

				}
			};
			listview.setOnItemClickListener(myListViewClicked);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ajanda, menu);
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

		Intent profile = new Intent(Ajanda.this, Profile.class);
		startActivity(profile);
		finish();

	}

	public void showMessages() {
		Intent messages = new Intent(Ajanda.this, Messages.class);
		startActivity(messages);
		finish();

	}

	public void showFriends() {
		Intent friends = new Intent(Ajanda.this, Friends.class);
		startActivity(friends);
		finish();

	}

	public void logout() {
		session.logoutUser();
		finish();

	}

	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	public LocationManager getLocationUpdates() {
		locationManager.requestLocationUpdates(provider, 100, 1, this);

		return locationManager;
	}

	/*
	 * return the latitude of the current devices' location - last known
	 * location
	 */
	public double getLatitude() {
		double lat = 0;
		try {
			lat = locationManager.getLastKnownLocation(provider).getLatitude();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return lat;
	}

	/*
	 * returns the longitude of the current devices' location - last known
	 * location
	 */
	public double getLongitude() {
		double lng = 0;
		try {
			lng = locationManager.getLastKnownLocation(provider).getLongitude();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return lng;
	}

	@Override
	public void onLocationChanged(Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Log.e("GooseLib", "GeoLocation - Provider Disabled [" + provider + "]");
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Log.e("GooseLib", "GeoLocation - Provider Enabled [" + provider + "]");
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// We don't need to worry about this method - just yet
	}

}
