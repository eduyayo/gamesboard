package com.pigdroid.gameboard.view.detail.contact;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.DialogFragmentCallback;
import com.pigdroid.gameboard.view.KillableActivity;
import com.pigdroid.gameboard.view.ServicesActivity;

public class ContactSearchActivity extends ServicesActivity implements DialogFragmentCallback {
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private ContactSearchTask mAuthTask = null;

	private String searchTerm;
	private EditText searchTermEditText;
	private TextView mStatusMessageView;
	private View mFormView;
	private View mStatusView;
	

	private ListView listView;
	
	private JsonNode results;
	private JsonNode selectedContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search_contact);
		
		searchTermEditText = (EditText) findViewById(R.id.search_term);
		searchTermEditText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							doSearch();
							return true;
						}
						return false;
					}
				});


		findViewById(R.id.search).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					doSearch();
				}
			});

		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				selectedContact = (JsonNode) adapterView.getItemAtPosition(position);
//				showDialog(123);
				Bundle args = new Bundle();
				args.putString("email", selectedContact.get("email").toString());
				AddContactDialogFragment fragment = new AddContactDialogFragment();
				fragment.setArguments(args);
				fragment.show(getSupportFragmentManager(), "dialog");
			}
		});
		mFormView = findViewById(R.id.form);
		mStatusView = findViewById(R.id.status);
		mStatusMessageView = (TextView) findViewById(R.id.status_message);
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
	public void doSearch() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		searchTermEditText.setError(null);

		// Store values at the time of the login attempt.
		searchTerm = searchTermEditText.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(searchTerm)) {
			searchTermEditText.setError(getString(R.string.error_field_required));
			focusView = searchTermEditText;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task
			mStatusMessageView.setText(R.string.searching);
			showProgress(true);
			mAuthTask = new ContactSearchTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mStatusView.setVisibility(View.VISIBLE);
			mStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mFormView.setVisibility(View.VISIBLE);
			mFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ContactSearchTask extends AsyncTask<Void, Void, Boolean> {
		
		Boolean retValue = Boolean.FALSE;
		Boolean error = Boolean.FALSE;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			GameBoardApplication application = (GameBoardApplication) getApplication();
			try {
				results = restService.searchContacts(searchTerm);
			} catch (Exception e) {
				error = true;
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (error) {
						mStatusMessageView.setText(R.string.no_results);
					}
				}
			});
			
			if (error) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
			}
			
			updateResults();
			
			if (!retValue) {
//				clearConfig();
			}
			
			return retValue;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	private void updateResults() {
		if (results != null && results.size() > 0) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					int max = results.size();
					List<JsonNode> list = new ArrayList<JsonNode>();
					for (int i = 0; i < max; i++) {
						list.add(results.get(i));
					}
					listView.setAdapter(new ArrayAdapter<JsonNode>(
							ContactSearchActivity.this, R.layout.item_contact,
							list) {

						@Override
						public View getView(int position, View convertView,
								ViewGroup parent) {
							View view = convertView;
							if (view == null) {
								view = getLayoutInflater()
										.inflate(
												com.pigdroid.gameboard.R.layout.item_contact_simple,
												null);
							}
							JsonNode item = getItem(position);
							((TextView) view.findViewById(android.R.id.text1))
									.setText(
											item.get("firstName") != null ? item.get("firstName").getTextValue() : ""
											+ " "
													+ item.get("lastName") != null ? item.get("lastName").getTextValue() : "");
							((TextView) view.findViewById(android.R.id.text2))
									.setText(item.get("email").getTextValue());

							return view;
						}

					});
				}
			});
		}
	}
	
//	@Override
//	@Deprecated
//	protected Dialog onCreateDialog(int id) {
//		AlertDialog.Builder adb = new AlertDialog.Builder(this);
//		adb.setTitle("Add contact?");
//		adb.setMessage("Add contact?");
//		adb.setIcon(android.R.drawable.ic_dialog_alert);
//		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				Intent intent = new Intent();
//				intent.putExtra(Intent.EXTRA_EMAIL, selectedContact.get("email").getTextValue());
//				setResult(Activity.RESULT_OK, intent);
//				finish();
//			}
//		});
//		adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				dismissDialog(123);
//			}
//		});
//		return adb.create();
//	}
	
//	@Override
//	@Deprecated
//	protected void onPrepareDialog(int id, Dialog dialog) {
////		((AlertDialog) dialog).setMessage(
////				String.format("Sure to add %s as your contact?",
////				selectedContact.getNickName()));
//	}

	@Override
	public void fragmentDialogCallback(String tag, String command) {
		if ("ok".equals(command)) {
			Intent intent = new Intent();
			intent.putExtra(Intent.EXTRA_EMAIL, selectedContact.get("email").getTextValue());
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}
}
