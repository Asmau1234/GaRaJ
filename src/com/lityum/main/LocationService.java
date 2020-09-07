package com.lityum.main;

import java.util.ArrayList;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.location.LocationListener;
import com.lityum.adapters.RowItem;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
	public static final String BROADCAST_ACTION = "Hello World";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public LocationManager locationManager;
	public MyLocationListener listener;
	public Location previousBestLocation = null;
	private JSONArray events;
	private RowItem ajanda;
	private static String url = "https://www.garaj.org/api/etkinlik-listesi-al";
	private static final String TAG_EVENTS = "etkinlikler";
	private ArrayList<RowItem> eventList = new ArrayList<RowItem>();
	NotificationManager NM;

	Intent intent;
	int counter = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		new RetrieveAjandaTask().execute();
		intent = new Intent(BROADCAST_ACTION);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				4000, 0, listener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	@Override
	public void onDestroy() {
		// handler.removeCallbacks(sendUpdatesToUI);
		super.onDestroy();
		Log.v("STOP_SERVICE", "DONE");
		locationManager.removeUpdates(listener);
	}

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}

	public class MyLocationListener implements LocationListener,
			android.location.LocationListener {

		public void onLocationChanged(final Location loc) {
			Log.i("**************************************", "Location changed");
			if (isBetterLocation(loc, previousBestLocation)) {
				loc.getLatitude();
				loc.getLongitude();
				intent.putExtra("Latitude", loc.getLatitude());
				intent.putExtra("Longitude", loc.getLongitude());
				intent.putExtra("Provider", loc.getProvider());
				sendBroadcast(intent);

			}
		}

		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();
		}

		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}

	class RetrieveAjandaTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog == null) {
				// Showing progress dialog
				pDialog = new ProgressDialog(LocationService.this);
				pDialog.setMessage("Please wait...");
				pDialog.setCancelable(false);
				pDialog.show();
			} else {
				pDialog.dismiss();
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
						ajanda.setDate(c.optString("tarih"));
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
			/*
			 * public void notify(View vobj) { String title =
			 * "Garaj Notification"; String subject = "Events near you."; String
			 * body = ""; if (ajanda.getPlace().contains(loc)) { body = loc; }
			 * NM = (NotificationManager)
			 * getSystemService(Context.NOTIFICATION_SERVICE);
			 * 
			 * Notification notify = new Notification(
			 * android.R.drawable.stat_notify_more, title,
			 * System.currentTimeMillis()); PendingIntent pending =
			 * PendingIntent.getActivity( getApplicationContext(), 0, new
			 * Intent(), 0); notify.setLatestEventInfo(getApplicationContext(),
			 * subject, body, pending); NM.notify(0, notify); }
			 */
		}
	}

}