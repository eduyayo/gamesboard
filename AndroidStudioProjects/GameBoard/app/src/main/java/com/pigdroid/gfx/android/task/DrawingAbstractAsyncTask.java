package com.pigdroid.gfx.android.task;

import com.pigdroid.gfx.android.GFXContext;
import com.pigdroid.gfx.android.SquareSurfaceView;

import android.graphics.Canvas;
import android.os.AsyncTask;

public abstract class DrawingAbstractAsyncTask extends
		AsyncTask<GFXContext, Void, Void> {

	@Override
	protected Void doInBackground(GFXContext... params) {
		GFXContext context = params[0];
		SquareSurfaceView view = context.getView();
		boolean drawn = false;
		do {
			Canvas canvas = view.lockCanvas();
			drawn = canvas != null;
			if (canvas != null) {
				drawFrame(context, canvas);
				drawn = true;
			}
		} while (!isDone(drawn));
		return null;
	}

	protected boolean isDone(boolean drawn) {
		return drawn;
	}

	protected abstract void drawFrame(GFXContext context, Canvas canvas);

}
