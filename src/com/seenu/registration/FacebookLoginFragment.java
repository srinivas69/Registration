package com.seenu.registration;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class FacebookLoginFragment extends Fragment {

	private String TAG = "FacebookLoginFragment";

	private View view;

	private UiLifecycleHelper uiHelper;

	private TextView userInfoTextView;

	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.fb_login_fragment, container, false);

		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_location",
				"user_birthday", "user_likes"));
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();
		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			// Log.i(TAG, "Logged in...");
			userInfoTextView.setVisibility(View.VISIBLE);

			// Request user data and show the results
			Request.newMeRequest(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser user, Response response) {
					// TODO Auto-generated method stub

					if (user != null) {
						// Display the parsed user info
						userInfoTextView.setText(buildUserInfoDisplay(user));
						// System.out.println(user.toString());
					}
				}
			}).executeAsync();
		} else if (state.isClosed()) {
			// Log.i(TAG, "Logged out...");
			userInfoTextView.setVisibility(View.INVISIBLE);
		}
	}

	protected String buildUserInfoDisplay(GraphUser user) {
		// TODO Auto-generated method stub

		StringBuilder userInfo = new StringBuilder("");

		// Example: typed access (name)
		// - no special permissions required
		userInfo.append(String.format("Name: %s\n\n", user.getName()));

		userInfo.append(String.format("Gender: %s\n\n",
				user.getProperty("gender")));

		// Example: typed access (birthday)
		// - requires user_birthday permission
		userInfo.append(String.format("Birthday: %s\n\n",
				user.getProperty("birthday")));

		// Example: partially typed access, to location field,
		// name key (location)
		// - requires user_location permission
		/*
		 * userInfo.append(String.format("Location: %s\n\n", user.getLocation()
		 * .getProperty("name")));
		 */

		// Example: access via property name (locale)
		// - no special permissions required
		userInfo.append(String.format("Locale: %s\n\n",
				user.getProperty("locale")));

		// Example: access via key for array (languages)
		// - requires user_likes permission
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

}
