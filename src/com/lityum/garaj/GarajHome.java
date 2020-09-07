package com.lityum.garaj;

import java.util.HashMap;


import com.lityum.garaj.R;
import com.lityum.main.SessionManager;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;


@SuppressWarnings("unused")
public class GarajHome extends Activity {

	private Button btnAjanda;
	private Button btnRehber;
	private Button btnIlanlar;
	SessionManager session;
	private String nick;
	private String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_garaj_home);
		
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		btnAjanda = (Button) findViewById(R.id.btnAjanda);
		btnRehber = (Button) findViewById(R.id.btnRehber);
		btnIlanlar = (Button) findViewById(R.id.btnIlanlar);
		session = new SessionManager(getApplicationContext());

		WebView webview;
		webview = (WebView) findViewById(R.id.webview);
		webview.loadUrl("http://www.garaj.org/reklam/partner/300x250");
		webview.setBackgroundColor(0x00000000);
		
		
		

		btnAjanda.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				populateAjanda();

			}

		});

		btnRehber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				populateRehber();

			}
		});

		btnIlanlar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				populateIlanlar();

			}
		});
		session.checkLogin();
		if (!session.isLoggedIn()) {
			finish();
		}
		// get user data from session
		HashMap<String, String> user = session.getUserDetails();
		nick = user.get(SessionManager.KEY_NAME);
		email = user.get(SessionManager.KEY_EMAIL);

	}

	public void populateAjanda() {

		Intent ajandaIntent = new Intent(GarajHome.this, Ajanda.class);
		startActivity(ajandaIntent);

	}

	public void populateRehber() {
		Intent rehberIntent = new Intent(GarajHome.this, Rehber.class);
		startActivity(rehberIntent);

	}

	public void populateIlanlar() {
		Intent ilanlarIntent = new Intent(GarajHome.this, Ilan.class);
		startActivity(ilanlarIntent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.garaj_home, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
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
		case R.id.refresh:
			onRestart();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onRestart() {

		// TODO Auto-generated method stub
		super.onRestart();
		Intent i = getIntent();
		startActivity(i);
		finish();

	}

	public void showProfile() {

		Intent profile = new Intent(GarajHome.this, Profile.class);
		startActivity(profile);

	}

	public void showMessages() {
		Intent messages = new Intent(GarajHome.this, Messages.class);
		startActivity(messages);
	}

	public void showFriends() {
		Intent friends = new Intent(GarajHome.this, Friends.class);
		startActivity(friends);
	}

	
	public void logout() {
		session.logoutUser();
		finish();

	}

}
