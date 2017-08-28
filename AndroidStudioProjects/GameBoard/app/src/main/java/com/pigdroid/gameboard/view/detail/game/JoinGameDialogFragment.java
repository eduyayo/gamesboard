package com.pigdroid.gameboard.view.detail.game;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.DialogFragmentCallback;
import com.pigdroid.gameboard.util.PreferenceUtils;
import com.pigdroid.gameboard.view.ServiceDialogFragment;

public class JoinGameDialogFragment extends ServiceDialogFragment {
	
	
	  @Override
      public Dialog onCreateDialog(Bundle savedInstanceState) {
          AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
          adb.setTitle("Join this game?");
          adb.setMessage("Do you want to join the game?");
          adb.setIcon(android.R.drawable.ic_dialog_alert);
          adb.setPositiveButton("Join", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                  ((DialogFragmentCallback) getActivity()).fragmentDialogCallback(getTag(), "ok");
                  dismiss();
              }
          });
          adb.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                  ((DialogFragmentCallback) getActivity()).fragmentDialogCallback(getTag(), "reject");
                  dismiss();
              }
          });
          adb.setCancelable(true);
          return adb.create();
      }

	@Override
	protected void onKickOff() {
		super.onKickOff();
		String id = getArguments().getString("id");
		Document document = dataService.loadDocument(id);
        ((TextView) getDialog().findViewById(android.R.id.message)).setText("Do you want to join the game with " + getOpponent(document) + " as opponent?");
	}

	private String getOpponent(Document document) {
		String ret = null;
		List<Map<String, String>> players = (List<Map<String, String>>) document.getProperty("players");
		String email = PreferenceUtils.getUserEmail(getActivity());
		for (Map<String, String> player : players) {
			if (email.equals(player.get("email"))) {
				continue;
			}
			ret = player.get("email");
			break;
		}
		return ret;
	}

}
