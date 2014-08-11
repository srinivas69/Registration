package com.seenu.registration;

import java.io.InputStream;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class GooglePlusLogin extends ActionBarActivity implements
		OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

	private static final int RC_SIGN_IN = 0;

	// Profile pic image size in pixels
	private static final int PROFILE_PIC_SIZE = 400;

	// Logcat tag
	private static final String TAG = "GooglePlusLogin";

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private SignInButton btnSignIn;
	private Button btnSignOut, btnRevokeAccess;
	private ImageView imgProfilePic;
	private TextView txtName, txtEmail;
	private LinearLayout llProfileLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.google_plus_activity);

		btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
		btnSignOut = (Button) findViewById(R.id.btn_sign_out);
		btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
		imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
		txtName = (TextView) findViewById(R.id.txtName);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

		// Button click listeners
		btnSignIn.setOnClickListener(this);
		btnSignOut.setOnClickListener(this);
		btnRevokeAccess.setOnClickListener(this);

		// Initializing google plus api client
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, null)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Button on click listener
	 * */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_sign_in:
			// Signin button clicked
			signInWithGplus();
			break;
		case R.id.btn_sign_out:
			// Signout button clicked
			signOutFromGplus();
			break;
		case R.id.btn_revoke_access:
			// Revoke access button clicked
			revokeGplusAccess();
			break;
		}
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

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

		// Get user's information
		getProfileInformation();

		// Update the UI after signin
		updateUI(true);

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
		updateUI(false);
	}

	/**
	 * Revoking access from google
	 * */
	private void revokeGplusAccess() {
		// TODO Auto-generated method stub
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e(TAG, "User access revoked!");
							mGoogleApiClient.connect();
							updateUI(false);
						}

					});
		}
	}

	/**
	 * Sign-out from google
	 * */
	private void signOutFromGplus() {
		// TODO Auto-generated method stub

		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			updateUI(false);
		}
	}

	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
		// TODO Auto-generated method stub
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	/**
	 * Method to resolve any signin errors
	 * */
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
						+ ", Image: " + personPhotoUrl + ", DOB: "+dob+ ", Gender: "+ gender);

				txtName.setText(personName);
				txtEmail.setText(email);

				// by default the profile url gives 50x50 px image only
				// we can replace the value with whatever dimension we want by
				// replacing sz=X
				personPhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2)
						+ PROFILE_PIC_SIZE;

				new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 * */
	private void updateUI(boolean isSignedIn) {
		// TODO Auto-generated method stub

		if (isSignedIn) {
			btnSignIn.setVisibility(View.GONE);
			btnSignOut.setVisibility(View.VISIBLE);
			btnRevokeAccess.setVisibility(View.VISIBLE);
			llProfileLayout.setVisibility(View.VISIBLE);
		} else {
			btnSignIn.setVisibility(View.VISIBLE);
			btnSignOut.setVisibility(View.GONE);
			btnRevokeAccess.setVisibility(View.GONE);
			llProfileLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * Background Async task to load user profile picture from url
	 * */
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public LoadProfileImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

}
