package com.seenu.registration;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

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

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	private static final int RC_SIGN_IN = 0;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

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

		if (shPrfs.contains(LOGIN_STATUS)) {
			if (shPrfs.getBoolean(LOGIN_STATUS, false)) {
				Intent i = new Intent(MainActivity.this,
						HomeScreenActivity.class);
				startActivity(i);
				finish();
			}
		}

		uiHelper = new UiLifecycleHelper(MainActivity.this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		fbAuthButton = (LoginButton) findViewById(R.id.button2);

		// fb_bt = (Button) findViewById(R.id.button2);
		tw_bt = (Button) findViewById(R.id.button3);
		// gp_bt = (Button) findViewById(R.id.button4);
		googleSignIn = (SignInButton) findViewById(R.id.button4);

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
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.button3:

			Intent i2 = new Intent(MainActivity.this, TwitterLogin.class);
			startActivity(i2);

			break;

		case R.id.button4:

			// Signin button clicked
			signInWithGplus();

			break;

		default:
			break;
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
		getProfileInformation();
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

	private void getProfileInformation() {
		// TODO Auto-generated method stub

		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				String dob = currentPerson.getBirthday();
				String gender = String.valueOf(currentPerson.getGender());

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
	}

}
