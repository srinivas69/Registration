package com.seenu.registration;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private Button fb_bt;
	private Button gp_bt;
	private Button tw_bt;

	// Internet Connection detector
	private ConnectionDetector cd;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	private SharedPreferences shPrfs;
	public static String PREFERENCE_NAME = "MyPrefs";
	public static String LOGIN_STATUS = "loginStatus";
	public static String LOGIN_TYPE = "loginType";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		shPrfs = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		
		if(shPrfs.contains(LOGIN_STATUS)){
			if(shPrfs.getBoolean(LOGIN_STATUS, false)){
				Intent i = new Intent(MainActivity.this,HomeScreenActivity.class);
				startActivity(i);
				finish();
			}
		}
		setContentView(R.layout.activity_main);

		fb_bt = (Button) findViewById(R.id.button2);
		tw_bt = (Button) findViewById(R.id.button3);
		gp_bt = (Button) findViewById(R.id.button4);

		fb_bt.setOnClickListener(this);
		tw_bt.setOnClickListener(this);
		gp_bt.setOnClickListener(this);

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.button2:

			Intent i = new Intent(MainActivity.this, FacebookLogin.class);
			startActivity(i);

			break;

		case R.id.button3:

			Intent i2 = new Intent(MainActivity.this, TwitterLogin.class);
			startActivity(i2);

			break;

		case R.id.button4:

			Intent i3 = new Intent(MainActivity.this, GooglePlusLogin.class);
			startActivity(i3);

			break;

		default:
			break;
		}

	}

}
