package com.seenu.registration;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class FacebookLogin extends ActionBarActivity {

	private FacebookLoginFragment fbFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			fbFragment = new FacebookLoginFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, fbFragment).commit();
		} else {
			// Or set the fragment from restored state info
			fbFragment = (FacebookLoginFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}
	}

}
