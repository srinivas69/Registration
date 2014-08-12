package com.seenu.registration;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TwitterLogin extends ActionBarActivity {

	/*
	 * private static Twitter twitter;
	 * 
	 * private Button loginBt;
	 * 
	 * protected static final String AUTHENTICATION_URL_KEY =
	 * "AUTHENTICATION_URL_KEY"; protected static final int
	 * LOGIN_TO_TWITTER_REQUEST = 0;
	 * 
	 * private AccessToken accessToken;
	 * 
	 * @Override protected void onCreate(Bundle savedInstanceState) { // TODO
	 * Auto-generated method stub super.onCreate(savedInstanceState);
	 * setContentView(R.layout.twitter_login_activity);
	 * 
	 * loginBt = (Button) findViewById(R.id.btnLoginToTwitter);
	 * 
	 * loginBt.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub loginToTwitter(); }
	 * 
	 * protected static final String AUTHENTICATION_URL_KEY =
	 * "AUTHENTICATION_URL_KEY"; protected static final int
	 * LOGIN_TO_TWITTER_REQUEST = 0; }); }
	 * 
	 * protected void loginToTwitter() { // TODO Auto-generated method stub
	 * 
	 * GetRequestTokenTask getRequestTokenTask = new GetRequestTokenTask();
	 * getRequestTokenTask.execute();
	 * 
	 * }
	 * 
	 * private class GetRequestTokenTask extends AsyncTask<Void, Void, Void> {
	 * 
	 * @Override protected Void doInBackground(Void... voids) {
	 * 
	 * if (accessToken == null) { twitter = TwitterFactory.getSingleton();
	 * twitter.setOAuthConsumer( getString(R.string.TWITTER_CONSUMER_KEY),
	 * getString(R.string.TWITTER_CONSUMER_SECRET));
	 * 
	 * try { RequestToken requestToken = twitter
	 * .getOAuthRequestToken(getString(R.string.TWITTER_CALLBACK_URL));
	 * launchLoginWebView(requestToken); } catch (TwitterException e) {
	 * e.printStackTrace(); } } else ; return null; } }
	 * 
	 * public void launchLoginWebView(RequestToken requestToken) { // TODO
	 * Auto-generated method stub
	 * 
	 * Intent intent = new Intent(this, LoginToTwitter.class);
	 * intent.putExtra(AUTHENTICATION_URL_KEY,
	 * requestToken.getAuthenticationURL()); startActivityForResult(intent,
	 * LOGIN_TO_TWITTER_REQUEST); }
	 * 
	 * protected void onActivityResult(int requestCode, int resultCode, Intent
	 * data) { if (requestCode == LOGIN_TO_TWITTER_REQUEST) { if (resultCode ==
	 * Activity.RESULT_OK) { getAccessToken(data
	 * .getStringExtra(LoginToTwitter.CALLBACK_URL_KEY)); } } }
	 * 
	 * private void getAccessToken(String callbackUrl) { Uri uri =
	 * Uri.parse(callbackUrl); String verifier =
	 * uri.getQueryParameter("oauth_verifier");
	 * 
	 * GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask();
	 * getAccessTokenTask.execute(verifier); }
	 * 
	 * private class GetAccessTokenTask extends AsyncTask<String, Void, Void> {
	 * 
	 * @Override protected Void doInBackground(String... strings) { String
	 * verifier = strings[0]; try { accessToken =
	 * twitter.getOAuthAccessToken(verifier);
	 * Log.d(TwitterLogin.class.getSimpleName(), accessToken.getToken()); }
	 * catch (Exception e) {
	 * 
	 * } return null; } }
	 */

	private static Twitter twitter;

	protected static final String AUTHENTICATION_URL_KEY = "AUTHENTICATION_URL_KEY";
	protected static final int LOGIN_TO_TWITTER_REQUEST = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login_activity);

		findViewById(R.id.btnLoginToTwitter).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						loginToTwitter();
					}
				});

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//logoutFromTwitter();
			}
		});
	}

	/*protected void logoutFromTwitter() {
		// TODO Auto-generated method stub
		
		twitter.setOAuthConsumer(getString(R.string.TWITTER_CONSUMER_KEY), getString(R.string.TWITTER_CONSUMER_SECRET));
		twitter.setOAuthAccessToken(null);
		twitter.shutdown();
	}*/

	private void loginToTwitter() {
		GetRequestTokenTask getRequestTokenTask = new GetRequestTokenTask();
		getRequestTokenTask.execute();
	}

	private class GetRequestTokenTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			twitter = TwitterFactory.getSingleton();
			System.out.println("TWITTER_CONSUMER_KEY: "
					+ getString(R.string.TWITTER_CONSUMER_KEY)
					+ ", TWITTER_CONSUMER_SECRET: "
					+ getString(R.string.TWITTER_CONSUMER_SECRET));
			twitter.setOAuthConsumer(getString(R.string.TWITTER_CONSUMER_KEY),
					getString(R.string.TWITTER_CONSUMER_SECRET));

			try {
				RequestToken requestToken = twitter
						.getOAuthRequestToken(getString(R.string.TWITTER_CALLBACK_URL));
				launchLoginWebView(requestToken);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private void launchLoginWebView(RequestToken requestToken) {
		Intent intent = new Intent(this, LoginToTwitter.class);
		intent.putExtra(AUTHENTICATION_URL_KEY,
				requestToken.getAuthenticationURL());
		startActivityForResult(intent, LOGIN_TO_TWITTER_REQUEST);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGIN_TO_TWITTER_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				getAccessToken(data
						.getStringExtra(LoginToTwitter.CALLBACK_URL_KEY));
			}
		}
	}

	private void getAccessToken(String callbackUrl) {
		Uri uri = Uri.parse(callbackUrl);
		String verifier = uri.getQueryParameter("oauth_verifier");

		GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask();
		getAccessTokenTask.execute(verifier);
	}

	private class GetAccessTokenTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... strings) {
			String verifier = strings[0];
			try {
				AccessToken accessToken = twitter.getOAuthAccessToken(verifier);
				Log.d(TwitterLogin.class.getSimpleName(),
						accessToken.getToken());

				long userID = accessToken.getUserId();

				User user = twitter.showUser(userID);

				String userName = user.getName();
				String userId = user.getScreenName();

				System.out.println(userName);
				System.out.println(userId);

			} catch (Exception e) {

			}
			return null;
		}
	}
}
