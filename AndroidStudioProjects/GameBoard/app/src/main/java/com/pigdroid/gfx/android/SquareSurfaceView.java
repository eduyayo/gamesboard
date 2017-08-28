package com.pigdroid.gfx.android;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class SquareSurfaceView extends SurfaceView {
	
	public static interface SquareSurfaceViewListener {
		void onChangeSize(int size);

		void onTouchDown(float x, float y);

		void onTouchUp(float x, float y);
	}
	
	private SquareSurfaceViewListener squareSurfaceViewListener = null;
	
	public void setSquareSurfaceViewListener(
			SquareSurfaceViewListener squareSurfaceViewListener) {
		this.squareSurfaceViewListener = squareSurfaceViewListener;
	}
	
	public SquareSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquareSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareSurfaceView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		int size = w;
		if (w < h) {
			setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
		} else {
			size = h;
			setMeasuredDimension(heightMeasureSpec, heightMeasureSpec);
		}
		if (h > 0 && squareSurfaceViewListener != null) {
			squareSurfaceViewListener.onChangeSize(size);
		}
	}
	
	public Canvas lockCanvas() {
		return getHolder().lockCanvas();
	}
	
	public void unlockCanvas(Canvas canvas) {
		if (canvas != null) {
			getHolder().unlockCanvasAndPost(canvas);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int masked = event.getActionMasked();
		if (masked == MotionEvent.ACTION_DOWN) {
			if (squareSurfaceViewListener != null) {
				squareSurfaceViewListener.onTouchDown(event.getX(), event.getY());
			}
			return true;
		} else if (masked == MotionEvent.ACTION_UP) {
			if (squareSurfaceViewListener != null) {
				squareSurfaceViewListener.onTouchUp(event.getX(), event.getY());
			}
			return true;
		}
		return super.onTouchEvent(event);
	}
	
}
