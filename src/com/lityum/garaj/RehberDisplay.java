package com.lityum.garaj;

import java.io.IOException;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lityum.classes.Rehberlar;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class RehberDisplay extends Activity {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rehber_display);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
		ImageView img = (ImageView) findViewById(R.id.imgRehber);
		TextView txtDate = (TextView) findViewById(R.id.txtDateRehber);
		TextView txtDesc = (TextView) findViewById(R.id.txtDescRehber);
		txtDesc.setMovementMethod(LinkMovementMethod.getInstance());
		
		Intent intent = RehberDisplay.this.getIntent();
		String rehberIntent = (String) intent.getExtras().get("RehberTitle");
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

				rehber.setTitle(title);
				rehber.setDate(date);
				rehber.setImage(image);
				rehber.setDescription(description);

				if (rehberIntent.contains(rehber.getTitle())) {
					setTitle(rehber.getTitle());
					txtDate.setText(rehber.getDate().toString());
					txtDesc.setText(Html.fromHtml(rehber.getDescription()));

				} else {

					System.out.println("ooppss!! did not receive intent");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rehber_display, menu);
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

		Intent profile = new Intent(RehberDisplay.this, Profile.class);
		startActivity(profile);

	}

	public void showMessages() {
		Intent messages = new Intent(RehberDisplay.this, Messages.class);
		startActivity(messages);

	}

	public void showFriends() {
		Intent friends = new Intent(RehberDisplay.this, Friends.class);
		startActivity(friends);

	}

	public String loadJSONFromAsset() {
		String json;
		try {

			InputStream is = RehberDisplay.this.getAssets().open("rehber.json");
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
