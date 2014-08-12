package com.seenu.registration;

import com.facebook.Session;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreenActivity extends ActionBarActivity {

	private TextView detailsTv;
	private Button logoutBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen_activity);

		detailsTv = (TextView) findViewById(R.id.textView2);
		logoutBt = (Button) findViewById(R.id.button1);

		if (getIntent().getExtras() != null) {
			String userdetails = getIntent().getExtras().getString(
					"UserDetails");
			detailsTv.setText(userdetails);
		}

		logoutBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (Session.getActiveSession() != null) {
					Session.getActiveSession().closeAndClearTokenInformation();
				}
				Session.setActiveSession(null);
				finish();
			}
		});
	}
}
