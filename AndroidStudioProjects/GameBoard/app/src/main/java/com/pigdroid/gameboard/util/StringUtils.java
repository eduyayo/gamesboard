package com.pigdroid.gameboard.util;

import android.content.Context;

import java.util.List;
import java.util.Map;

public class StringUtils {
	
	private StringUtils() {
	}
	
	public static String getGameEstateDesc(String key) {
		String ret = null;
		if (key == null) {
			key = "";
		}
		switch (key) {
		case "WAITING_PLAYERS":// Server responded or user accepted an invitation.
			ret = "Still waiting for players...";
			break;
		case "STARTED":// All players are in and we can start or continue.
			ret = "Playing";
			break;
		case "FINISHED":// win or lose conditions.
			ret = "Game finished.";
			break;
		case "INVALID":
			ret = "Game Defunct.";
			break;
		default:
			ret = "Still refreshing...";
			break;
		}
		return ret;
	}

	public static String getGameEstateDesc(String value, Map<String, Object> properties, Context context) {
        String ret = null;
        if ("WAITING_PLAYERS".equals(value)) {
            String localEmail = PreferenceUtils.getUserEmail(context);
            for (Map<String, Object> player : (List<Map<String, Object>>) properties.get("players")) {
                if (localEmail.equals(player.get("email"))) {
                    if ("INVITED".equals(player.get("status"))) {
                        ret = "Waiting for you!";
                    }
                    break;
                }
            }
		}
        if (ret == null) {
            ret = getGameEstateDesc(value);
        }
		return ret;
	}

	public static String crop(String in, int length) {
		String ret = in;
		if (length < ret.length()) {
			ret = ret.substring(0, length);
		}
		return ret;
	}

	public static String crop(String in, int length, int c) {
		String ret = crop(in, length);
		int indexOf = ret.indexOf(c);
		if (indexOf > 0 && indexOf < ret.length()) {
			ret = ret.substring(0, indexOf);
		}
		return ret;
	}

	public static String shortenEmail(String in) {
		return crop(in, 8, '@');
	}

	public static CharSequence getAcceptedEstate(Map<String, Object> properties) {
		String ret = null;
		Boolean accepted = (Boolean) properties.get("accepted");
		Boolean iaccepted = (Boolean) properties.get("reverseAccepted");
		if (accepted == null) {
			accepted = false;
		}
		if (iaccepted == null) {
			iaccepted = false;
		}
		if (accepted && iaccepted) {
			ret = "Available.";
		} else if (accepted) {
			ret = "Did not respond yet.";
		} else if (iaccepted) {
			ret = "Waiting for you to respond!";
		}
		return ret;
	}

}
