package com.seenu.registration;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserDetails extends ActionBarActivity {

	private Bundle b;

	private TextView detailsTv;
	private Button logoutBt;
	private Button othUsrBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen_activity);

		detailsTv = (TextView) findViewById(R.id.textView2);
		logoutBt = (Button) findViewById(R.id.button1);
		othUsrBt = (Button) findViewById(R.id.button2);

		// TextView t = new TextView(UserDetails.this);

		b = getIntent().getExtras();
		String details = getDetails();

		logoutBt.setVisibility(View.GONE);
		othUsrBt.setVisibility(View.GONE);
		detailsTv.setText(details);

		// setContentView(R.layout.t);
	}

	private String getDetails() {
		// TODO Auto-generated method stub

		StringBuilder userInfo = new StringBuilder("");

		String userName = b.getString("NAME");
		userInfo.append(String.format("Name: %s\n\n", userName));

		String accName = b.getString("ACCOUNT_NAME");
		userInfo.append(String.format("AccountName: %s\n\n", accName));

		String gender = b.getString("GENDER");
		userInfo.append(String.format("Gender: %s\n\n", gender));

		String dob = b.getString("DOB");
		userInfo.append(String.format("Date of Birth: %s\n\n", dob));

		String location = b.getString("LOCATION");
		userInfo.append(String.format("Location: %s\n\n", location));

		return userInfo.toString();

	}
}
