package com.pigdroid.game.chat.controller;

import java.io.InputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.pigdroid.game.annotation.Controller;
import com.pigdroid.game.chat.model.ChatModel;
import com.pigdroid.game.chat.model.ChatSelection;
import com.pigdroid.game.controller.GameController;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.resource.ResourceProvider;

@Controller(
		gameName = "Chat",
		codeVersion = 1,
		gameIcon = "")
public class ChatController extends GameController<ChatModel> {
	
	private static Comparator<Integer> inverseIntegerComparator = new Comparator<Integer>() {
		
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
	};

	@Override
	public boolean isReadyToStart() {
		return getModel().getPlayerCount() > 1;
	}

	@Override
	protected ChatModel createModel() {
		return new ChatModel();
	}

	@Override
	public void addPlayer(Player p) {
		getModel().addPlayer(p);
	}

	@Override
	protected Map<Integer, GameSelection> getUIElements() {
		Map<Integer, ChatSelection> messages = getModel().getMessages();
		Map<Integer, GameSelection> ret = new TreeMap<Integer, GameSelection>(inverseIntegerComparator);
		ret.putAll(messages);
		return ret;
	}

	@Override
	protected boolean isMove() {
		return getModel().getSelections().size() > 0;
	}
	
	@Override
	protected void move() {
		ChatModel model = getModel();
		model.setMessageCounter(model.getMessageCounter() + model.getSelections().size());
		super.move();
		List<GameSelection> moved = model.getMovedSelections();
		Map<Integer, ChatSelection> messages = model.getMessages();
		int max = ChatModel.MAX_MESSAGES - moved.size();
		int i = messages.size();
		if (i > max) {
			Iterator<Integer> it = new TreeSet<Integer>(messages.keySet()).iterator();
			for (; i > max; i--) {
				messages.remove(it.next());
			}
		}
		i = messages.size();
		for (GameSelection selection : moved) {
			messages.put(i ++, (ChatSelection) selection);
		}
		
	}

	@Override
	public InputStream getUIResource(int resourceId) {
		return null;
	}

	@Override
	public InputStream getUIResource(Long resourceAlias) {
		return null;
	}

	@Override
	protected void onInitResourceProvider(ResourceProvider resourceProvider) {
		
	}

}
