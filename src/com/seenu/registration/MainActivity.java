package com.seenu.registration;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	private ProgressDialog pDia;

	// Google client to interact with Google API
	public static GoogleApiClient mGoogleApiClient;

	private static final int RC_SIGN_IN = 0;

	private static Twitter twitter;

	protected static final String AUTHENTICATION_URL_KEY = "AUTHENTICATION_URL_KEY";
	protected static final int LOGIN_TO_TWITTER_REQUEST = 1;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private Button reg_bt;
	private Button tw_bt;
	private SignInButton googleSignIn;
	private LoginButton fbAuthButton;

	private String TAG = "MainActivity";
	private UiLifecycleHelper uiHelper;

	// Internet Connection detector
	private ConnectionDetector cd;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	private SharedPreferences shPrfs;
	private Editor edit;
	public static String PREFERENCE_NAME = "MyPrefs";
	public static String LOGIN_STATUS = "loginStatus";
	public static String LOGIN_TYPE = "loginType";

	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		shPrfs = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		edit = shPrfs.edit();

		if (shPrfs.contains(LOGIN_STATUS)) {
			if (shPrfs.getBoolean(LOGIN_STATUS, false)) {

				if (shPrfs.getString(LOGIN_TYPE, "none").equals("gplus")) {
					return;
				}
				Intent i = new Intent(MainActivity.this,
						HomeScreenActivity.class);
				startActivity(i);
				finish();
			}
		}

		uiHelper = new UiLifecycleHelper(MainActivity.this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		reg_bt = (Button) findViewById(R.id.button1);
		fbAuthButton = (LoginButton) findViewById(R.id.button2);

		// fb_bt = (Button) findViewById(R.id.button2);
		tw_bt = (Button) findViewById(R.id.button3);
		// gp_bt = (Button) findViewById(R.id.button4);
		googleSignIn = (SignInButton) findViewById(R.id.button4);

		pDia = new ProgressDialog(MainActivity.this);

		reg_bt.setOnClickListener(this);
		// fb_bt.setOnClickListener(this);
		tw_bt.setOnClickListener(this);
		googleSignIn.setOnClickListener(this);

		fbAuthButton.setReadPermissions(Arrays.asList("email",
				"public_profile", "user_location", "user_birthday",
				"user_likes"));

		// Initializing google plus api client
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, null)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();

	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.button1:

			Intent i = new Intent(MainActivity.this, RegisterActivity.class);
			startActivity(i);

			break;

		case R.id.button3:

			loginToTwitter();
			/*
			 * Intent i2 = new Intent(MainActivity.this, TwitterLogin.class);
			 * startActivity(i2);
			 */

			break;

		case R.id.button4:

			// Signin button clicked

			new SingInGPlusAsync().execute();
			// signInWithGplus();

			break;

		default:
			break;
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*
		 * Session.getActiveSession().onActivityResult(this, requestCode,
		 * resultCode, data);
		 */

		switch (requestCode) {

		case Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE:

			uiHelper.onActivityResult(requestCode, resultCode, data);
			Session mCurrentSession = Session.getActiveSession();

			if (mCurrentSession.isOpened()) {

				// Request user data and show the results
				Request.newMeRequest(mCurrentSession,
						new Request.GraphUserCallback() {

							@Override
							public void onCompleted(GraphUser user,
									Response response) {
								// TODO Auto-generated method stub

								if (user != null) {
									// Display the parsed user info
									String userDetails = buildUserInfoDisplay(user);
									// System.out.println(user.toString());

									Intent i = new Intent(MainActivity.this,
											HomeScreenActivity.class);
									i.putExtra("UserDetails", userDetails);
									startActivity(i);

									if (shPrfs
											.contains(MainActivity.LOGIN_STATUS)) {
										edit.remove(MainActivity.LOGIN_STATUS);
										edit.remove(MainActivity.LOGIN_TYPE);
										edit.putBoolean(
												MainActivity.LOGIN_STATUS, true);
										edit.putString(MainActivity.LOGIN_TYPE,
												"facebook");
										edit.commit();
									} else {
										edit.putBoolean(
												MainActivity.LOGIN_STATUS, true);
										edit.putString(MainActivity.LOGIN_TYPE,
												"facebook");
										edit.commit();
									}

									finish();
								}
							}
						}).executeAsync();
			} else if (mCurrentSession.isClosed())
				;

			break;

		case RC_SIGN_IN:

			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
			break;

		case LOGIN_TO_TWITTER_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				getAccessToken(data
						.getStringExtra(LoginToTwitter.CALLBACK_URL_KEY));
				break;
			}
		}

	}

	protected void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// TODO Auto-generated method stub

		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");

			Request.newMeRequest(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser user, Response response) {
					// TODO Auto-generated method stub

					if (user != null) {
						// Display the parsed user info
						String userDetails = buildUserInfoDisplay(user);
						// System.out.println(user.toString());

						/*
						 * Intent i = new Intent(MainActivity.this,
						 * HomeScreenActivity.class); i.putExtra("UserDetails",
						 * userDetails); startActivity(i); finish();
						 */
					}
				}
			}).executeAsync();

		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

		// Get user's information
		String userDetails = getProfileInformation();

		/*
		 * Intent i = new Intent(MainActivity.this, HomeScreenActivity.class);
		 * i.putExtra("UserDetails", userDetails); startActivity(i); finish();
		 */
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	private void signInWithGplus() {
		// TODO Auto-generated method stub

		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	private void resolveSignInError() {
		// TODO Auto-generated method stub

		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	private void loginToTwitter() {
		// TODO Auto-generated method stub
		GetRequestTokenTask getRequestTokenTask = new GetRequestTokenTask();
		getRequestTokenTask.execute();
	}

	public void launchLoginWebView(RequestToken requestToken) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, LoginToTwitter.class);
		intent.putExtra(AUTHENTICATION_URL_KEY,
				requestToken.getAuthenticationURL());
		startActivityForResult(intent, LOGIN_TO_TWITTER_REQUEST);
	}

	private void getAccessToken(String callbackUrl) {
		Uri uri = Uri.parse(callbackUrl);
		String verifier = uri.getQueryParameter("oauth_verifier");

		GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask();
		getAccessTokenTask.execute(verifier);
	}

	protected String buildUserInfoDisplay(GraphUser user) {
		// TODO Auto-generated method stub

		StringBuilder userInfo = new StringBuilder("");

		userInfo.append(String.format("Name: %s\n\n", user.getName()));

		userInfo.append(String.format("Gender: %s\n\n",
				user.getProperty("gender")));

		userInfo.append(String.format("Birthday: %s\n\n",
				user.getProperty("birthday")));

		userInfo.append(String.format("Locale: %s\n\n",
				user.getProperty("locale")));

		JSONArray languages = (JSONArray) user.getProperty("favorite_athletes");
		if (languages.length() > 0) {
			ArrayList<String> languageNames = new ArrayList<String>();
			for (int i = 0; i < languages.length(); i++) {
				JSONObject language = languages.optJSONObject(i);
				// Add the language name to a list. Use JSON
				// methods to get access to the name field.
				languageNames.add(language.optString("name"));
			}
			userInfo.append(String.format("Likes: %s\n\n",
					languageNames.toString()));
		}

		return userInfo.toString();
	}

	private String getProfileInformation() {
		// TODO Auto-generated method stub

		StringBuilder userInfo = new StringBuilder("");

		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();

				userInfo.append(String.format("Name: %s\n\n", personName));

				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();

				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				userInfo.append(String.format("UserName: %s\n\n", email));

				String dob = currentPerson.getBirthday();
				userInfo.append(String.format("DOB: %s\n\n", dob));

				String gender = String.valueOf(currentPerson.getGender());
				userInfo.append(String.format("Gender: %s\n\n", gender));

				Log.e(TAG, "Name: " + personName + ", plusProfile: "
						+ personGooglePlusProfile + ", email: " + email
						+ ", Image: " + personPhotoUrl + ", DOB: " + dob
						+ ", Gender: " + gender);

			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo.toString();
	}

	private class SingInGPlusAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			pDia.setTitle("Logging In");
			pDia.setMessage("Please wait...");
			pDia.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			signInWithGplus();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDia.dismiss();
		}

	}

	private class GetRequestTokenTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(getString(R.string.TWITTER_CONSUMER_KEY));
			builder.setOAuthConsumerSecret(getString(R.string.TWITTER_CONSUMER_SECRET));
			Configuration configuration = builder.build();
			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();
			
			//twitter = TwitterFactory.getSingleton();
			/*
			 * System.out.println("TWITTER_CONSUMER_KEY: " +
			 * getString(R.string.TWITTER_CONSUMER_KEY) +
			 * ", TWITTER_CONSUMER_SECRET: " +
			 * getString(R.string.TWITTER_CONSUMER_SECRET));
			 */
			/*twitter.setOAuthConsumer(getString(R.string.TWITTER_CONSUMER_KEY),
					getString(R.string.TWITTER_CONSUMER_SECRET));*/

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

	private class GetAccessTokenTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... strings) {
			String verifier = strings[0];
			try {
				AccessToken accessToken = twitter.getOAuthAccessToken(verifier);
				Log.d(MainActivity.class.getSimpleName(),
						accessToken.getToken());

				long userID = accessToken.getUserId();

				User user = twitter.showUser(userID);

				final StringBuilder userInfo = new StringBuilder("");

				String userName = user.getName();
				userInfo.append(String.format("Name: %s\n\n", userName));

				String userId = user.getScreenName();
				userInfo.append(String.format("AccountName: %s\n\n", userId));

				String location = user.getLocation();
				userInfo.append(String.format("Location: %s\n\n", location));

				System.out.println(userName);
				System.out.println(userId);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						Intent i = new Intent(MainActivity.this,
								HomeScreenActivity.class);
						i.putExtra("UserDetails", userInfo.toString());
						startActivity(i);

						if (shPrfs.contains(MainActivity.LOGIN_STATUS)) {
							edit.remove(MainActivity.LOGIN_STATUS);
							edit.remove(MainActivity.LOGIN_TYPE);
							edit.putBoolean(MainActivity.LOGIN_STATUS, true);
							edit.putString(MainActivity.LOGIN_TYPE, "twitter");
							edit.commit();
						} else {
							edit.putBoolean(MainActivity.LOGIN_STATUS, true);
							edit.putString(MainActivity.LOGIN_TYPE, "twitter");
							edit.commit();
						}
						
						finish();
					}
				});

			} catch (Exception e) {

			}
			return null;
		}
	}

}
