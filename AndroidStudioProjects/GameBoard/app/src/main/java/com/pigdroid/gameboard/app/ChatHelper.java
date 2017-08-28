package com.pigdroid.gameboard.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.util.PreferenceUtils;
import com.pigdroid.hub.model.message.HubMessage;

public class ChatHelper {
	
	private ChatHelper() {
		
	}

	public static BroadcastReceiver initChatController(final Activity activity, final View view, final String email) {
		final TextView textView = (TextView) view.findViewById(R.id.message);
		ListView listView = (ListView) view.findViewById(android.R.id.list);
		final ArrayAdapter<String[]> adapter = new ArrayAdapter<String[]>(activity, android.R.layout.simple_list_item_2, android.R.id.text1) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null) {
					view = super.getDropDownView(position, convertView, parent);
				}
				String[] item = getItem(position);
				((TextView) view.findViewById(android.R.id.text1)).setText(item[0]);
				((TextView) view.findViewById(android.R.id.text2)).setText(item[1]);
				return view;
			}
		};
		listView.setAdapter(adapter);
		view.findViewById(R.id.send).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String text = textView.getText().toString();
				if (!TextUtils.isEmpty(text)) {
					textView.setText("");
					new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							final String sessionEmail = PreferenceUtils.getUserEmail(activity);
							HubMessage message = HubMessage.builder().type(HubMessage.TYPE_MSG_CHAT)
									.from(sessionEmail)
									.to(email).payload(text).build();


							//TODO SEND MESSAGE!
//                            RestService pushService = Acacia.createService(activity, RestService.class);
//							final String result = pushService.sendMessage(message).trim();
final String result = null;

							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if ("OK".equals(result)) {
										String[] item = {sessionEmail, text};
										adapter.add(item);
									} else {
										String[] item = {"SYS", "Your party is not online."};
										adapter.add(item);
									}
									adapter.notifyDataSetChanged();
								}
							});
							return null;
						}
						
					}.execute();
				}
			}
		});
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle extras = intent.getExtras();
				String fromEmail = extras.getString(Intent.EXTRA_EMAIL);
				if (fromEmail != null && fromEmail.equals(email)) {
					String msg = extras.getString(Intent.EXTRA_TEXT);
					final String[] item = {email, msg};
					intent.putExtra(Intent.EXTRA_DATA_REMOVED, true);
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.add(item);
							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		};
		LocalBroadcastManager.getInstance(activity).registerReceiver(
				receiver, 
				new IntentFilter(GameBoardApplication.INTENT_ACTION_CHAT));
		return receiver;
	}

}
