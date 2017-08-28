package com.pigdroid.gameboard.view;

import com.pigdroid.gameboard.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public final class LoadingViewHandleHelper {
	
	private LoadingViewHandleHelper() {
	}
	
	public static void showLoading(final View rootView) {
		if (rootView == null) {
			return;
		}
		View content = rootView.findViewById(R.id.content_frame);
		View loadingView = rootView.findViewById(R.id.loading_spinner);
		if (content != null && loadingView != null) {
			content.setVisibility(View.GONE);
			loadingView.setVisibility(View.VISIBLE);
//			animDuration = getResources().getInteger(
//	                android.R.integer.config_longAnimTime);
		}
		
	}
	
	public static void hideLoading(final View rootView) {
		if (rootView == null) {
			return;
		}
		final View content = rootView.findViewById(R.id.content_frame);
		final View loadingView = rootView.findViewById(R.id.loading_spinner);
		if (content != null && loadingView != null) {
			int animDuration = rootView.getContext().getResources().getInteger(
            android.R.integer.config_longAnimTime);
			content.setAlpha(0f);
			content.setVisibility(View.VISIBLE);
			content.animate()
		            .alpha(1f)
		            .setDuration(animDuration)
		            .setListener(null);
			loadingView.animate()
		            .alpha(0f)
		            .setDuration(animDuration)
		            .setListener(new AnimatorListenerAdapter() {
		                @Override
		                public void onAnimationEnd(Animator animation) {
		                	loadingView.setVisibility(View.GONE);
		                }
		            });
		}
	}

}
