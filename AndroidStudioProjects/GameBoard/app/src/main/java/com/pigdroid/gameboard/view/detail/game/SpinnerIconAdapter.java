package com.pigdroid.gameboard.view.detail.game;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.UnitUtils;

public class SpinnerIconAdapter extends ArrayAdapter<String> {

    private String[] contentArray;
    private String[] imageUris;

    public SpinnerIconAdapter(Context context, int resource, String[] objects,
    		String[] imageUris) {
        super(context,  R.layout.spinner_icon_value_layout, R.id.spinnerTextView, objects);
        this.contentArray = objects;
        this.imageUris = imageUris;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
    	View row = null;
    	if (convertView != null) {
    		row = convertView;
    	} else {
	        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        row = inflater.inflate(R.layout.spinner_icon_value_layout, parent, false);
    	}

        TextView textView = (TextView) row.findViewById(R.id.spinnerTextView);
        textView.setText(contentArray[position]);

        ImageView imageView = (ImageView)row.findViewById(R.id.spinnerImages);
        Uri uri = Uri.parse(imageUris[position]);
		GameBoardApplication.getPicasso().load(uri).resize(UnitUtils.dpToPx(getContext(), imageView.getLayoutParams().width), UnitUtils.dpToPx(getContext(), imageView.getLayoutParams().height)).into(imageView);

        return row;    
    }    
    
    
}