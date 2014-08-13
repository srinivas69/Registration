package com.seenu.registration;

import com.facebook.Session;
import com.google.android.gms.plus.Plus;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreenActivity extends ActionBarActivity {

	private TextView detailsTv;
	private Button logoutBt;

	private SharedPreferences sh_Pref;
	private Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen_activity);

		detailsTv = (TextView) findViewById(R.id.textView2);
		logoutBt = (Button) findViewById(R.id.button1);

		sh_Pref = getSharedPreferences(MainActivity.PREFERENCE_NAME,
				MODE_PRIVATE);
		edit = sh_Pref.edit();

		if (getIntent().getExtras() != null) {
			String userdetails = getIntent().getExtras().getString(
					"UserDetails");
			detailsTv.setText(userdetails);
		}

		logoutBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				/*
				 * if (MainActivity.mGoogleApiClient.isConnected()) {
				 * Plus.AccountApi
				 * .clearDefaultAccount(MainActivity.mGoogleApiClient);
				 * MainActivity.mGoogleApiClient.disconnect();
				 * MainActivity.mGoogleApiClient.connect(); finish(); }
				 */

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

				/*
				 * if (sh_Pref.contains(MainActivity.LOGIN_STATUS)) {
				 * edit.remove(MainActivity.LOGIN_STATUS);
				 * edit.remove(MainActivity.LOGIN_TYPE);
				 * edit.putBoolean(MainActivity.LOGIN_STATUS, false);
				 * edit.putString(MainActivity.LOGIN_TYPE, "none");
				 * edit.commit(); } else {
				 * edit.putBoolean(MainActivity.LOGIN_STATUS, false);
				 * edit.putString(MainActivity.LOGIN_TYPE, "none");
				 * edit.commit(); } finish();
				 */

			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		MainActivity.mGoogleApiClient.connect();
	}
}
