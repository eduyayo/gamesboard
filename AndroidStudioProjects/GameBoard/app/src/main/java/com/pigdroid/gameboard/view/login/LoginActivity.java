package com.pigdroid.gameboard.view.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.pigdroid.android.hateaidl.HateAIDLConnection;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.service.RestService;
import com.pigdroid.gameboard.util.PreferenceUtils;
import com.pigdroid.gameboard.view.KillableActivity;
import com.pigdroid.gameboard.view.MainActivity;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends KillableActivity {


	protected RestService restService;
    private HateAIDLConnection<RestService> restServiceConnection;

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	private boolean isRetry() {
		Intent intent = getIntent();
		if (intent != null) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				Boolean isIt = extras.getBoolean("isRetry");
				return isIt != null ? isIt : false;
			}
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		findViewById(R.id.register_link).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.from_right, R.anim.to_left);
				finish();
			}
		});

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		findViewById(R.id.cancel_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		});

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		if (isRetry()) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					showProgress(true);
					mLoginStatusMessageView.setText("Login failed, please, try again!");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
					showProgress(false);
					return null;
				}
				
			}.execute();
		}

        restServiceConnection = new HateAIDLConnection<RestService>(this, new HateAIDLConnection.Listener<RestService>() {
            @Override
            public void bound(RestService proxy) {
                restService = proxy;
                kickOffIf();
            }
        }, RestService.class);
	}


    private void kickOffIf() {
        if (restService != null) {
            onKickOff();
        }
    }

    protected void onKickOff() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restServiceConnection.disconnect();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
//		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
				// for very easy animations. If available, use these APIs to fade-in
				// the progress spinner.
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					int shortAnimTime = getResources().getInteger(
							android.R.integer.config_shortAnimTime);

					mLoginStatusView.setVisibility(View.VISIBLE);
					mLoginStatusView.animate().setDuration(shortAnimTime)
							.alpha(show ? 1 : 0)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									mLoginStatusView.setVisibility(show ? View.VISIBLE
											: View.GONE);
								}
							});

					mLoginFormView.setVisibility(View.VISIBLE);
					mLoginFormView.animate().setDuration(shortAnimTime)
							.alpha(show ? 0 : 1)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									mLoginFormView.setVisibility(show ? View.GONE
											: View.VISIBLE);
								}
							});
				} else {
					// The ViewPropertyAnimator APIs are not available, so simply show
					// and hide the relevant UI components.
					mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			}
		});
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		
		Boolean retValue = Boolean.FALSE;
		Boolean error = Boolean.FALSE;
		
		@Override
		protected Boolean doInBackground(Void... params) {

			PreferenceUtils.setUserEmail(getApplicationContext(), mEmail);
			PreferenceUtils.setUserPassword(getApplicationContext(), mPassword);

			retValue = restService.doLogin();
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (error) {
						mLoginStatusMessageView.setText(R.string.error_connection_error);
					} else if(!retValue) {
						mLoginStatusMessageView.setText(R.string.error_login_fail);
					}
				}
			});
			if (!retValue) {
				clearConfig();
			}

			if (error || !retValue) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
			
			return retValue;
		}

		private void clearConfig() {
			PreferenceUtils.setUserEmail(getApplicationContext(), null);
			PreferenceUtils.setUserPassword(getApplicationContext(), null);
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			if (success) {
				setResult(RESULT_OK);
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			} else if (!error) {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
			showProgress(false);
		}

		@Override
		protected void onCancelled() {
			clearConfig();
			mAuthTask = null;
			showProgress(false);
		}
	}
	
}
