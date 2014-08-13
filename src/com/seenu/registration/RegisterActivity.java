package com.seenu.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends ActionBarActivity {

	private EditText fnEt;
	private EditText lnEt;
	private EditText emailEt;
	private EditText pdEt;
	private Button regBt;

	private String firstName;
	private String lastName;
	private String email;
	private String password;

	private String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_activity);

		fnEt = (EditText) findViewById(R.id.editText1);
		lnEt = (EditText) findViewById(R.id.editText2);
		emailEt = (EditText) findViewById(R.id.editText3);
		pdEt = (EditText) findViewById(R.id.editText4);
		regBt = (Button) findViewById(R.id.button1);

		fnEt.addTextChangedListener(textWatcher);
		lnEt.addTextChangedListener(textWatcher);
		emailEt.addTextChangedListener(textWatcher);
		pdEt.addTextChangedListener(textWatcher);

		// run once to disable if empty
		checkFieldsForEmptyValues();

		regBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (email.matches(emailPattern) && password.length() >= 4) {

					String userDetails = getUserDetails();

					Intent i = new Intent(RegisterActivity.this,
							HomeScreenActivity.class);
					i.putExtra("UserDetails", userDetails);
					startActivity(i);
					finish();
				}
			}
		});
	}

	protected String getUserDetails() {
		// TODO Auto-generated method stub
		StringBuilder userInfo = new StringBuilder("");

		userInfo.append(String.format("Name: %s\n\n", firstName + " "
				+ lastName));

		userInfo.append(String.format("AccountName: %s\n\n", email));

		return userInfo.toString();
	}

	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			checkFieldsForEmptyValues();
		}
	};

	protected void checkFieldsForEmptyValues() {
		// TODO Auto-generated method stub

		firstName = fnEt.getText().toString();
		lastName = lnEt.getText().toString();
		email = emailEt.getText().toString();
		password = pdEt.getText().toString();

		if (firstName.equals("") || lastName.equals("") || email.equals("")
				|| password.equals("")) {
			regBt.setEnabled(false);
		} else {
			regBt.setEnabled(true);
		}
	}

}
