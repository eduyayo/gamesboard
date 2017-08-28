package com.pigdroid.gameboard.view.detail.contact;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.DialogFragmentCallback;
import com.pigdroid.gameboard.view.detail.ItemListAbstractActivity;
import com.pigdroid.gameboard.view.detail.game.GameListActivity;

import java.util.Map;

public class ContactListActivity extends ItemListAbstractActivity implements DialogFragmentCallback {
	
	protected static final int LOADER_GAME_COUNT = 1;
	private String id;
	
	protected Class<?> getItemDetailActivityClass() {
		return ContactDetailActivity.class;
	}

	protected Fragment getNewItemDetailFragment() {
		return new ContactDetailFragment();
	}
	
	protected int getItemDetailContainerId() {
		return R.id.item_detail_container;
	}

	protected int getContentViewId() {
		return R.layout.activity_contact_list;
	}

	@Override
	protected Query getDetailQuery(int id) {
		Query ret = null;
		switch (id) {
		case LOADER_GAME_COUNT:
			ret = dataService.getCountGameQuery();
			break;
		default:
			break;
		}
		return ret;
	}

	@Override
	protected Query getListQuery() {
		return dataService.getContactQuery();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		showDialogIf(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		showDialogIf(intent);
	}
	
	private void showDialogIf(Intent intent) {
		if (intent.hasExtra("id")) {
			id = intent.getExtras().getString("id");
			intent.removeExtra("id");
            showDialog();
		}
	}

    private void showDialog() {
        Bundle args = new Bundle();
        args.putString("id", id);
        AddContactDialogFragment fragment = new AddContactDialogFragment();
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
	protected void onKickOff() {
		super.onKickOff();
		initLoaders(LOADER_GAME_COUNT);
        findViewById(R.id.search).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
				Intent intent = new Intent(ContactListActivity.this, ContactSearchActivity.class);
				startActivityForResult(intent, 123);
            }
        });
	}

	@Override
	protected String getDetailTag(int id) {
		String ret = null;
		switch (id) {
		case LOADER_GAME_COUNT:
			ret = "GAME_COUNT";
			break;
		default:
			break;
		}
		return ret;
	}

	@Override
	public void fragmentDialogCallback(String tag, String command) {
		if ("ok".equals(command)) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					Map<String, Object> selectedContact = dataService.loadDocument(id).getProperties();
					restService.addContact(selectedContact.get("email").toString());
					return null;
				}
			}.execute();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					String email = data.getExtras().getString(Intent.EXTRA_EMAIL);
					restService.addContact(email);
                    //TODO should clear the list???
					initLoaders(LOADER_ITEM_LIST);
					return null;
				}
				
			}.execute();
		}
	}

    @Override
    public void onItemSelected(String id) {
        Document document = dataService.getDocument(id);
        Boolean accepted = (Boolean) document.getProperty("accepted");
        Boolean reverseAccepted = (Boolean) document.getProperty("reverseAccepted");

        accepted = accepted != null ? accepted : false;
        reverseAccepted = reverseAccepted != null ? reverseAccepted : false;

        if (accepted && !reverseAccepted) {
            SnackbarManager.show(Snackbar.with(this).position(Snackbar.SnackbarPosition.TOP).text("This buddy did not accept you yet!").color(Color.YELLOW).textColor(Color.BLACK));
        } else if (!accepted && reverseAccepted) {
            this.id = id;
            showDialog();
        } else if (reverseAccepted && accepted) {
            Intent intent = new Intent(ContactListActivity.this, GameListActivity.class);
            intent.putExtra(Intent.EXTRA_EMAIL, (String) document.getProperties().get("email"));
            startActivity(intent);
        }
    }

}
