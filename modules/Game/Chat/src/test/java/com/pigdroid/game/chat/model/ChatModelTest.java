package com.pigdroid.game.chat.model;

import org.junit.Assert;
import org.junit.Test;

import com.pigdroid.game.model.memento.Memento;

public class ChatModelTest {
	
	@Test
	public void testUndo() {
		ChatModel model = new ChatModel();
		ChatSelection selection = new ChatSelection();
		selection.setPlayerName("eduyayo");
		selection.setText("text");
		model.getMessages().put(1, selection);
		Memento memento = model.memento();
		model = new ChatModel();
		model.from(memento);
		Assert.assertEquals("eduyayo", model.getMessages().get(1).getPlayerName());
	}

}
