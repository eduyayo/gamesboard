package com.pigdroid.gameboard.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class UnitUtils {

	private UnitUtils() {

	}

	public static final int dpToPx(Context ctx, int dp) {
		DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	public int pxToDp(Context ctx, int px) {
		DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
		int dp = Math.round(px
				/ (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return dp;
	}

}
