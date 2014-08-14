package com.seenu.registration;

import com.facebook.Session;
import com.google.android.gms.plus.Plus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreenActivity extends ActionBarActivity {

	private TextView detailsTv;
	private Button logoutBt;
	private Button othUsrBt;

	private SharedPreferences sh_Pref;
	private Editor edit;
	private DBAdapter db;
	private Cursor c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen_activity);

		detailsTv = (TextView) findViewById(R.id.textView2);
		logoutBt = (Button) findViewById(R.id.button1);
		othUsrBt = (Button) findViewById(R.id.button2);

		sh_Pref = getSharedPreferences(MainActivity.PREFERENCE_NAME,
				MODE_PRIVATE);
		edit = sh_Pref.edit();

		db = new DBAdapter(getApplicationContext());
		db.openDatabase();

		if (getIntent().getExtras() != null) {
			String userdetails = getIntent().getExtras().getString(
					"UserDetails");
			detailsTv.setText(userdetails);
		} else {
			c = db.getAllRecords();
			if (c.moveToLast()) {
				String userDetails = getUserDetails();
				detailsTv.setText(userDetails);
			}
		}

		logoutBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (sh_Pref.getString(MainActivity.LOGIN_TYPE, "none").equals(
						"facebook")) {

					if (Session.getActiveSession() != null) {
						Session.getActiveSession()
								.closeAndClearTokenInformation();
					}
					Session.setActiveSession(null);

				}

				edit.remove(MainActivity.LOGIN_STATUS);
				edit.remove(MainActivity.LOGIN_TYPE);
				edit.putBoolean(MainActivity.LOGIN_STATUS, false);
				edit.putString(MainActivity.LOGIN_TYPE, "none");
				edit.commit();

				finish();

			}
		});

		othUsrBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(HomeScreenActivity.this,
						UserListActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		MainActivity.mGoogleApiClient.connect();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (c != null)
			c.close();
		db.close();
	}

	// user details method
	private String getUserDetails() {
		// TODO Auto-generated method stub

		StringBuilder userInfo = new StringBuilder("");

		String userName = c.getString(1);
		userInfo.append(String.format("Name: %s\n\n", userName));

		String accName = c.getString(2);
		userInfo.append(String.format("AccountName: %s\n\n", accName));

		String gender = c.getString(3);
		userInfo.append(String.format("Gender: %s\n\n", gender));

		String dob = c.getString(4);
		userInfo.append(String.format("Date of Birth: %s\n\n", dob));

		String location = c.getString(5);
		userInfo.append(String.format("Location: %s\n\n", location));

		return userInfo.toString();
	}
}
