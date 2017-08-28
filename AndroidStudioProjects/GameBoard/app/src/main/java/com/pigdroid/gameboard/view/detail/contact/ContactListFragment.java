package com.pigdroid.gameboard.view.detail.contact;

import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.QueryRow;
import com.pigdroid.gameboard.R;
import com.pigdroid.gameboard.app.GameBoardApplication;
import com.pigdroid.gameboard.util.StringUtils;
import com.pigdroid.gameboard.view.detail.ItemListAbstractFragment;
import com.squareup.picasso.Picasso;

public class ContactListFragment extends ItemListAbstractFragment {

	@Override
	protected int getItemLayoutId() {
//		return android.R.layout.simple_list_item_activated_1;
		return R.layout.item_contact;
	}

	@Override
	protected void mapItemView(View view, QueryRow item, String tag) {
		if (null == tag) {
			if (item.getDocument() == null) {
				throw new RuntimeException(tag + " -- " + item);
			}
			Map<String, Object> properties = item.getDocument().getProperties();
			TextView text = (TextView) view.findViewById(android.R.id.text1);
			text.setText(properties.get("email").toString());
			ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
			GameBoardApplication.getPicasso().with(getActivity()).load((String)properties.get("imageUrl")).resize(32, 32).into(imageView);
			text = (TextView) view.findViewById(R.id.game_count);
			text.setText(getData("GAME_COUNT" + item.getKey()));

			text = (TextView) view.findViewById(android.R.id.text2);
			text.setText(StringUtils.getAcceptedEstate(properties));
		} else {
			//Compound key, must be a detail view (nested view)
			if ("GAME_COUNT".equals(tag)) {
				TextView text = (TextView) view.findViewById(R.id.game_count);
				text.setText(item.getValue().toString());
			} else {
//				TextView text = (TextView) view.findViewById(R.id.invitation_count); //TODO invitation count??
//				hideByValue(text, "0");
			}
		}
	}

	private void hideByValue(final TextView text, String val) {
		if (val.equals(text.getText())) {
        	text.setVisibility(View.GONE);
		} else {
        	text.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected int getItemListLayout() {
		return R.layout.fragment_contact_list;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

}
