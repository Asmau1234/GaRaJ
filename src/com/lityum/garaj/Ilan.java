package com.lityum.garaj;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lityum.adapters.CustomListView;
import com.lityum.adapters.RowItem;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

public class Ilan extends Activity implements OnItemSelectedListener {

	private SessionManager session;
	private CustomListView adapter;
	private ListView listview;
	private JSONArray ilans;
	private RowItem ilanlar;
	private Spinner spinnerpano;
	private Spinner spinneryetkin;
	private Spinner spinnersehir;
	String pano;
	String sehir;
	private String oncesi;
	private String sonrasi;
	private Button btnprev;
	private Button btnnext;
	private List<NameValuePair> pairs = new ArrayList<NameValuePair>();
	private ArrayList<RowItem> ilanList = new ArrayList<RowItem>();
	private static String url = "https://www.garaj.org/api/ilan-listesi-al";
	private static final String TAG_ITEMS = "ilanlar";
	private OnItemClickListener myListViewClicked;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ilanlar);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		Activity activity= this;
		activity.findViewById(android.R.id.content).setBackgroundColor(Color.LTGRAY);
		
		new RetrieveIlanTask().execute();
		listview = (ListView) findViewById(R.id.ListIlanlar);
		spinnerpano = (Spinner) findViewById(R.id.Sppano);
		spinneryetkin = (Spinner) findViewById(R.id.Spyetkin);
		spinnersehir = (Spinner) findViewById(R.id.Spsehir);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.pano_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnerpano.setAdapter(adapter);
		spinnerpano.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				this, R.array.yetkin_array,
				android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinneryetkin.setAdapter(adapter1);
		spinneryetkin.setOnItemSelectedListener(this);

		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter
				.createFromResource(this, R.array.sehir_array,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnersehir.setAdapter(adapter2);
		spinnersehir.setOnItemSelectedListener(this);

		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				 pano = spinnerpano.getSelectedItem().toString();
				 sehir = spinnersehir.getSelectedItem().toString();
				// String yetkin = spinneryetkin.getSelectedItem().toString();
				listview.setAdapter(null);
				ilanList.clear();
				Intent i = new Intent(Ilan.this, SearchIlan.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle extras = new Bundle();
				extras.putString("Pano", pano);
				extras.putString("Sehir", sehir);
				i.putExtras(extras);
				startActivity(i);

			}
		});

		 btnnext = (Button) findViewById(R.id.btnNext);
		btnnext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				btnprev.setVisibility(View.VISIBLE);
				listview.setAdapter(null);
				ilanList.clear();
				pairs.clear();
				pairs.add(new BasicNameValuePair("sonrasi", sonrasi));
				new IlanButtonsTask().execute();

			}
		});
		 btnprev = (Button) findViewById(R.id.btnPrevious);
		btnprev.setVisibility(View.GONE);
		btnprev.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				listview.setAdapter(null);
				ilanList.clear();
				pairs.clear();
				pairs.add(new BasicNameValuePair("oncesi", oncesi));
				new IlanButtonsTask().execute();

			}
		});

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
			if (pDialog == null) {
				// Showing progress dialog
				pDialog = new ProgressDialog(Ilan.this);
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
			ServiceHandler sh = new ServiceHandler();

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
					ilans = obj.getJSONArray(TAG_ITEMS);
					oncesi = obj.getString("oncesi");
					sonrasi = obj.getString("sonrasi");

					for (int i = 0; i < ilans.length(); i++) {
						JSONObject c = ilans.getJSONObject(i);
						ilanlar = new RowItem();
						ilanlar.setDate(c.optString("fiyat")); // using setDate in
																// place of
																// Fiyat so as
																// not to
																// create new
																// RowItem class
						ilanlar.setDesc(c.optString("aciklama"));
						ilanlar.setPlace(c.optString("yer"));
						ilanlar.setTitle(c.optString("baslik"));
						ilanlar.setTime(c.optString("tarih"));
						ilanlar.setImage(c.getString("resim"));

						ilanList.add(ilanlar);
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
			adapter = new CustomListView(Ilan.this, R.layout.list_item_1,
					ilanList);
			listview.setAdapter(adapter);
			myListViewClicked = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					RowItem name = (RowItem) parent.getItemAtPosition(position);
					Intent i = new Intent(Ilan.this, IlanDisplay.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("IlanTitle", name.toString());
					startActivity(i);

				}
			};
			listview.setOnItemClickListener(myListViewClicked);

		}
	}
	

	class IlanButtonsTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog == null) {
				// Showing progress dialog
				pDialog = new ProgressDialog(Ilan.this);
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
			ServiceHandler sh = new ServiceHandler();

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
					ilans = obj.getJSONArray(TAG_ITEMS);
					oncesi = obj.getString("oncesi");
					sonrasi = obj.getString("sonrasi");

					for (int i = 0; i < ilans.length(); i++) {
						JSONObject c = ilans.getJSONObject(i);
						ilanlar = new RowItem();
						ilanlar.setDate(c.optString("fiyat")); // using Date in
																// place of
																// Fiyat so as
																// not to
																// create new
																// RowItem class
						ilanlar.setDesc(c.optString("aciklama"));
						ilanlar.setPlace(c.optString("yer"));
						ilanlar.setTitle(c.optString("baslik"));
						ilanlar.setTime(c.optString("tarih"));
						ilanlar.setImage(c.getString("resim"));

						ilanList.add(ilanlar);
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
			adapter = new CustomListView(Ilan.this, R.layout.list_item_1,
					ilanList);
			listview.setAdapter(adapter);
			myListViewClicked = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					RowItem name = (RowItem) parent.getItemAtPosition(position);
					Intent i = new Intent(Ilan.this, IlanDisplay.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("IlanTitle", name.toString());
					startActivity(i);

				}
			};
			listview.setOnItemClickListener(myListViewClicked);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ilanlar, menu);
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

		Intent profile = new Intent(Ilan.this, Profile.class);
		startActivity(profile);

	}

	public void showMessages() {
		Intent messages = new Intent(Ilan.this, Messages.class);
		startActivity(messages);

	}

	public void showFriends() {
		Intent friends = new Intent(Ilan.this, Friends.class);
		startActivity(friends);

	}

	public void logout() {
		session.logoutUser();
		finish();

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
