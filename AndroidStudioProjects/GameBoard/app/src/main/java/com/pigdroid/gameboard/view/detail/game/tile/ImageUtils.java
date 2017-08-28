package com.pigdroid.gameboard.view.detail.game.tile;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.Rect;

import com.larvalabs.svgandroid.SVGBuilder;

public class ImageUtils {

	public static Bitmap createBitmap(InputStream in, int w, int h) {
		if (in != null) {
			return createBitmap(in, w, h, false);
		} else {
			return null;
		}
	}

	public static Bitmap createBitmap(InputStream in, int w, int h, boolean closeStream) {
		if (in == null) {
			return null;
		}
		com.larvalabs.svgandroid.SVG svg;
		svg = new SVGBuilder().readFromInputStream(in).build();
		
		Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Picture pic = svg.getPicture();
//	      svg.renderToCanvas(canvas/*, new RectF(0f, 0f, (float)w, (float)h)*/);
//	      svg.renderToCanvas(canvas, new RectF(0f, 0f, (float)w, (float)h));
		canvas.drawPicture(pic, new Rect(0, 0, w, h));
		if (closeStream) {
			try {
				in.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return bitmap;
	}

    public static Bitmap resizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean recycle) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        if (recycle) {
            bm.recycle();
        }
        return resizedBitmap;
    }

    public static Bitmap resizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        return resizedBitmap(bm, newWidth, newHeight, true);
    }
	
}
