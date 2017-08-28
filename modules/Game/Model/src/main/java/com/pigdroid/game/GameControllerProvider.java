package com.pigdroid.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pigdroid.game.controller.GameController;


@SuppressWarnings("unchecked")
public class GameControllerProvider {

	private static Map<String, Class<GameController<?>>> CONTROLLERS;
	private static Map<String, String> ICONS;

	static {
		
		CONTROLLERS = new TreeMap<String, Class<GameController<?>>>();

		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(GameControllerProvider.class.getResourceAsStream("/controllers.txt")));
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				Class<?> clazz = Class.forName(line);
//				@SuppressWarnings("unchecked")
//				Class<GameController<?>> controllerClass = (Class<GameController<?>>) clazz;
//				Controller annotation = controllerClass.getAnnotation(Controller.class);
//				CONTROLLERS.put(annotation.gameName(), controllerClass);
//			}
//			reader.close();
			CONTROLLERS.put("Checkers", (Class<GameController<?>>) Class.forName("com.pigdroid.game.board.tile.checkers.controller.CheckersController"));
			CONTROLLERS.put("Dots", (Class<GameController<?>>) Class.forName("com.pigdroid.game.board.tile.dots.controller.DotsController"));
			CONTROLLERS.put("Connect4", (Class<GameController<?>>) Class.forName("com.pigdroid.game.board.tile.connect4.controller.Connect4Controller"));
			CONTROLLERS.put("Madelinette", (Class<GameController<?>>) Class.forName("com.pigdroid.game.board.tile.madelinette.controller.MadelinetteController"));
//			CONTROLLERS.put("Chess", (Class<GameController<?>>) Class.forName("com.pigdroid.game.board.tile.chess.controller.ChessController"));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} 
//		catch (IOException e) {
//			throw new RuntimeException(e);
//		}
		
		
		ICONS = new TreeMap<String, String>();
		ICONS.put("Checkers",		"svg://com/pigdroid/game/checkers/blue_queen.svg");
		ICONS.put("Dots",			"svg://com/pigdroid/game/board/tile/dots/icon.svg");
		ICONS.put("Connect4",		"svg://com/pigdroid/game/board/tile/connect4/icon.svg");
		ICONS.put("Madelinette",	"svg://com/pigdroid/game/board/tile/madelinette/icon.svg");
//		ICONS.put("Chess",			"svg://com/pigdroid/game/checkers/blue_queen.svg");
		
	}

	@SuppressWarnings("unchecked")
	public static <T> T createController(String gameName) {
		Class<GameController<?>> clazz = CONTROLLERS.get(gameName);
		GameController<?> controller = null;
		try {
			controller = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return (T) controller;
	}
	
	public static boolean isGame(String gameName) {
		return CONTROLLERS.get(gameName) != null;
	}
	
	public static String[] getGameNameList() {
		List<String> names = new ArrayList<String>(CONTROLLERS.size());
		for (String name : CONTROLLERS.keySet()) {
			names.add(name);
		}
		return names.toArray(new String[names.size()]);
	}
	
	public static String[] getGameIconList() {
		List<String> icons = new ArrayList<String>(ICONS.size());
		for (Map.Entry<String, String> entry : ICONS.entrySet()) {
			icons.add(entry.getValue());
		}
		return icons.toArray(new String[icons.size()]);
	}
	
}
