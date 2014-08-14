package com.seenu.registration;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

public class UserListActivity extends ActionBarActivity {

	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list_activity);

		lv = (ListView) findViewById(R.id.listView1);
	}
}
