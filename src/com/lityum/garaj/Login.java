package com.lityum.garaj;

import java.util.ArrayList;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import com.lityum.main.CheckNetwork;
import com.lityum.main.ServiceHandler;
import com.lityum.main.SessionManager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private EditText mEmailView;
	private EditText mPasswordView;
	private UserLoginTask mAuthTask = null;
	private String mEmail;
	private String mPassword;
	@SuppressWarnings("unused")
	private View mLoginFormView;
	@SuppressWarnings("unused")
	private View mLoginStatusView;
	private SessionManager session;
	private String pass;
	private String mail;
	boolean cancel = false;
	private View focusView = null;
	private static String url = "https://www.garaj.org/api/uye-girisi-yap";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		Activity activity = this;
		activity.findViewById(android.R.id.content).setBackgroundColor(
				Color.LTGRAY);

		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		session = new SessionManager(getApplicationContext());

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();

						if (CheckNetwork.isInternetAvailable(Login.this)) {
							new UserLoginTask().execute();
						} else {
							int secs = 3000;
							Toast.makeText(Login.this,
									"No Internet Connection", secs).show();

						}

					}
				});

	}

	public class UserLoginTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog pDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog == null) {
				// Showing progress dialog
				pDialog = new ProgressDialog(Login.this);
				pDialog.setMessage("Kimlik Doğrulama!!! Lutfen Bekleyiniz...");
				pDialog.setCancelable(false);
				pDialog.show();
			} else {
				pDialog.dismiss();
			}

		}

		@Override
		protected Void doInBackground(Void... params) {

			Log.i("thread", "Doing Something...");

			pass = mPasswordView.getText().toString();
			mail = mEmailView.getText().toString();
			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("eposta", mail));
			pairs.add(new BasicNameValuePair("sifre", pass));
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET, pairs);

			Log.d("Response: ", "> " + jsonStr);
			session.createLoginSession(pass, mail);
			if (jsonStr != null) {
				session.createLoginSession(pass, mail);
				Intent myIntent = new Intent(Login.this, GarajHome.class);
				startActivity(myIntent);
				finish();

			}
			else{
				
			}

			return null;

		}

		protected void onPostExecute(Void result) {
			Log.i("thread", "Done...");
			if (pDialog != null) {
				pDialog.dismiss();
			} else {
				
			}

		}

	}

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {

		}
	}

	protected void onRestart() {

		// TODO Auto-generated method stub
		super.onRestart();
		Intent i = getIntent();
		startActivity(i);
		finish();

	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Really Exit?")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						Login.super.onBackPressed();
					}
				}).create().show();
	}

}
