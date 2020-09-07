package com.lityum.main;



import com.lityum.garaj.Login;
import com.lityum.garaj.R;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

	SessionManager session;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		if (!session.isLoggedIn()) {
			finish();
		}
		// get user data from session
		//HashMap<String, String> user = session.getUserDetails();
		//String nick = user.get(SessionManager.KEY_NAME);
		//String email = user.get(SessionManager.KEY_EMAIL);

		new Handler().postDelayed(new Runnable() {
	        @Override
	        public void run() {
	           final Intent mainIntent = new Intent(MainActivity.this, Login.class);
	           MainActivity.this.startActivity(mainIntent);
	           MainActivity.this.finish();
	        }
	    }, 3000);

	}

	/*
	 * public boolean onCreateOptionsMenu(Menu menu) { // Inflate the menu; this
	 * adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */

}
