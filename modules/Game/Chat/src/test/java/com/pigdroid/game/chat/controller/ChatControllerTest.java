package com.pigdroid.game.chat.controller;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pigdroid.game.chat.model.ChatModel;
import com.pigdroid.game.chat.model.ChatSelection;
import com.pigdroid.game.controller.GameController.GameControllerListener;
import com.pigdroid.game.controller.GameController.GameUIControllerListener;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.UIGameContext;

public class ChatControllerTest {
	
	private ChatController controller;
	private ChatModel model;
	private UIGameContext graphicContext;
	private List<GameSelection> sentSelections;

	@Before
	public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		controller = new ChatController();
		Field field = controller.getClass().getSuperclass().getDeclaredField("model");
		field.setAccessible(true);
		model = (ChatModel) field.get(controller);
		controller.setGameControllerListener(new GameControllerListener() {

			public void onGamePaint(UIGameContext newgfx) {
				graphicContext = newgfx;
			}

			public void onPlayerJoined(Player invited) {
				// TODO Auto-generated method stub
				
			}

			public void onPlayerLeft(Player found) {
				// TODO Auto-generated method stub
				
			}

			public void onMove(List<GameSelection> selections) {
				// TODO Auto-generated method stub
				
			}

			public void onStartGame() {
				// TODO Auto-generated method stub
				
			}

			public void onSendSelections(List<GameSelection> selectionsCopy) {
				sentSelections = selectionsCopy;
			}

			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				return true;
			}

			public void onEndGame() {
				// TODO Auto-generated method stub
				
			}
		});

		controller.setGameUIControllerListener(new GameUIControllerListener() {

			public void onGamePaint(UIGameContext graphicContext) {
				ChatControllerTest.this.graphicContext = graphicContext;
			}
		});
	}

	@Test
	public void testOneMessage() {
		ChatSelection selection = new ChatSelection();
		selection.setPlayerName("eduyayo");
		selection.setText("Hello world!");
		controller.select(selection);
		Assert.assertEquals(1, graphicContext.getUiElements().size());
		Assert.assertEquals(1, sentSelections.size());
	}

	@Test
	public void testTwoMessage() {
		ChatSelection selection = new ChatSelection();
		selection.setPlayerName("eduyayo");
		selection.setText("Hello world!");
		controller.select(selection);
		Assert.assertEquals(1, sentSelections.size());
		sentSelections = null;
		
		selection = new ChatSelection();
		selection.setPlayerName("pigdroid");
		selection.setText("Hello there!");
		controller.select(selection);
		Assert.assertEquals(1, sentSelections.size());
		
		Assert.assertNotNull(graphicContext);
		Assert.assertEquals(2, graphicContext.getUiElements().size());
	}
	

	@Test
	public void testMaxMessage() {
		ChatSelection selection = null;
		
		for (int i = 0; i < ChatModel.MAX_MESSAGES + 50; i++) {
			selection = new ChatSelection();
			selection.setPlayerName("eduyayo");
			selection.setText("Hello world!");
			controller.select(selection);
			Assert.assertEquals(1, sentSelections.size());
			sentSelections = null;
		}
		
		Assert.assertTrue(ChatModel.MAX_MESSAGES > graphicContext.getUiElements().size());
	}

}
