package com.seenu.registration;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserListActivity extends ActionBarActivity {

	private ListView lv;

	private DBAdapter db;
	private Cursor c;

	private ArrayAdapter<String> adapter;
	private ArrayList<String> usrNams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list_activity);

		db = new DBAdapter(getApplicationContext());
		db.openDatabase();
		c = db.getAllRecords();

		lv = (ListView) findViewById(R.id.listView1);

		usrNams = getUserNames();

		adapter = new ArrayAdapter<String>(UserListActivity.this,
				android.R.layout.simple_list_item_1, usrNams);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				c.moveToPosition(arg2);
				Bundle b = new Bundle();
				b.putString("NAME", c.getString(1));
				b.putString("ACCOUNT_NAME", c.getString(2));
				b.putString("GENDER", c.getString(3));
				b.putString("DOB", c.getString(4));
				b.putString("LOCATION", c.getString(5));

//				System.out.println(b.toString());
				Intent i = new Intent(UserListActivity.this, UserDetails.class);
				i.putExtras(b);
				startActivity(i);
			}
		});
	}

	private ArrayList<String> getUserNames() {
		// TODO Auto-generated method stub

		ArrayList<String> usrs = new ArrayList<String>();

		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			usrs.add(i, c.getString(1));
		}
		return usrs;
	}
}
