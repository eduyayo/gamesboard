package com.pigdroid.game.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pigdroid.game.model.GameModel;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.PlayerStatusEnum;
import com.pigdroid.game.model.UIGameContext;
import com.pigdroid.game.model.memento.Memento;
import com.pigdroid.game.resource.ResourceProvider;
import com.pigdroid.util.Base64;

public abstract class GameController<G extends GameModel> {
	
	private G model;

	private ResourceProvider resourceProvider;
	
	protected GameControllerListener gameControllerListener = null;
	protected GameUIControllerListener gameUIControllerListener = null;
	
	public static interface GameUIControllerListener {
		void onGamePaint(UIGameContext graphicContext);
	}
	
	public static interface GameControllerListener {
		void onPlayerJoined(Player invited);
		void onPlayerLeft(Player found);
		void onMove(List<GameSelection> selections);
		void onStartGame();
		void onSendSelections(List<GameSelection> selectionsCopy);
		boolean onIsPlayerLocal(List<GameSelection> moved);
		boolean onIsPlayerLocal(String email);
		void onEndGame(Player player, boolean winner, boolean loser, boolean tie);
		void onNeverReadyToStart();
		void onSelect(List<GameSelection> selections);
	}

	protected boolean isPlayerLocal(int playerIndex) {
		if (playerIndex > -1 && model.getPlayers() != null && model.getPlayers().size() > playerIndex) {
			Player player = model.getPlayers().get(playerIndex);
			if (player instanceof HumanPlayer) {
				HumanPlayer human = (HumanPlayer) player;
				return isPlayerLocal(human.getEmail());
			}
		}
		return false;
	}
	
	protected boolean isPlayerLocal(String email) {
		if (gameControllerListener != null) {
			return gameControllerListener.onIsPlayerLocal(email);
		}
		return false;
	}

	protected ResourceProvider getResourceProvider() {
		if (resourceProvider == null) {
			resourceProvider = new ResourceProvider();
				//TODO Add game default resources? Icon?
			onInitResourceProvider(resourceProvider);
		}
		return resourceProvider;
	}
	
	protected abstract void onInitResourceProvider(ResourceProvider resourceProvider);

	public void setGameUIControllerListener(
			GameUIControllerListener gameUIControllerListener) {
		this.gameUIControllerListener = gameUIControllerListener;
	}
	
	public GameController() {
		model = createModel();
		onInitModel(model);
	}

	protected void onInitModel(G game) {
		game.setDirty(true);		
	}

	public void addPlayer(Player player) {
		player.setStatus(PlayerStatusEnum.INVITED);
		model.addPlayer(player);
	}
	
	public Iterator<Player> getPlayers() {
		return model.getPlayers().iterator();
	}
	
	public void loadModelFromSerialized(String save) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decode(save));
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			Memento memento = (Memento) objectInputStream.readObject();
			objectInputStream.close();
			model.from(memento);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		model.setDirty(true);
		doPaint();
	}

	public String getSerializedModel() {
		Memento memento = model.memento();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(memento);
			objectOutputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return Base64.encodeToString(byteArrayOutputStream.toByteArray());
	}
	
	protected G getModel() {
		return model;
	}
	
	public void checkpoint() {
		G game = getModel();
		game.checkpoint();
	}
	
	public boolean rollback() {
		G game = getModel();
		boolean ret = game.rollback();
		game.setDirty(true);
		doPaint();
		return ret;
	}
	
	public void commit() {
		G game = getModel();
		game.commit();
	}
	
	public void newGame() {
		model = createModel();
	}

	public abstract boolean isReadyToStart();
	
	public abstract boolean isNeverReadyToStart();
	
	protected abstract G createModel();
	
	protected abstract Map<Integer, GameSelection> getUIElements();
	
	protected void move() {
		List<GameSelection> selections = getSelections();
		List<GameSelection> moved = getMovedSelections();
		moved.addAll(selections);
		if (gameControllerListener != null) {
			gameControllerListener.onMove(selections);
		}
		selections.clear();
		model.setDirty(true);
	}

	protected List<GameSelection> getMovedSelections() {
		return model.getMovedSelections();
	}

	protected List<GameSelection> getSelections() {
		return model.getSelections();
	}
	
	public void select(GameSelection ... gameSelections) {
		List<GameSelection> selections = getSelections();
		for (GameSelection gameSelection : gameSelections) {
			model.setDirty(true);
			selectableOrThrow(gameSelection);
			selections.add(gameSelection);
			doTick(getModel().getPlayer(gameSelection.getPlayerName()));
		}
	}

	public abstract int getIndex(GameSelection gameSelection);
	
	protected void selectableOrThrow(GameSelection gameSelection) {
		Map<Integer, GameSelection> selectables = getSelectables();
		if (selectables.get(getIndex(gameSelection)) == null) {
			throw new IllegalArgumentException("Cannot select, the selection is not a selectable.");
		}
	}

	public void doTick(Player currentPlayer) {
		doMove(currentPlayer);
		doPaint();
	}

	public void doForcePaint() {
		model.setDirty(true);
		doPaint();
	}
	
	protected void doPaint() {
		if (model.isDirty()) {
			if (gameUIControllerListener != null) {
				gameUIControllerListener.onGamePaint(createUIContext());
			}
			model.setDirty(false);
		}
	}

	protected void doMove(Player currentPlayer) {
		if (isMove()) {
			move();
			clearSelections(true);
			if (isGameEnd(currentPlayer)) {
				doEndGame(currentPlayer);
			}
		}
	}

	protected boolean isGameEnd(Player currentPlayer) {
		boolean ret = isWinner(currentPlayer) || isLoser(currentPlayer) || isTie(currentPlayer);
		return ret;
	}

	protected void doEndGame(Player currentPlayer) {
		if (gameControllerListener != null) {
			gameControllerListener.onEndGame(currentPlayer, isWinner(currentPlayer), isLoser(currentPlayer), isTie(currentPlayer));
		}
	}

	public boolean isLoser(Player currentPlayer) {
		return false;
	}

	public boolean isTie(Player currentPlayer) {
		return false;
	}

	public boolean isWinner(Player currentPlayer) {
		return true;
	}

	protected UIGameContext createUIContext() {
		return new UIGameContext(getUIElements(), getSelections());
	}

	protected abstract boolean isMove();

	public void setGameControllerListener(GameControllerListener listener) {
		this.gameControllerListener = listener;
	}

	protected void clearSelections(boolean send) {
		List<GameSelection> selections = getSelections();
		List<GameSelection> moved = getMovedSelections();
		if (send && !moved.isEmpty() && gameControllerListener != null) {
			if (gameControllerListener.onIsPlayerLocal(moved)) {
				List<GameSelection> selectionsCopy = new ArrayList<GameSelection>(moved);
				gameControllerListener.onSendSelections(selectionsCopy);
			}
		}
		selections.clear();
		moved.clear();
	}

	public Player getInvitedPlayer() {
		Player invited = null;
		for (Player p : model.getPlayers()) {
			if (PlayerStatusEnum.INVITED.equals(p.getStatus())) {
				invited = p;
				break;
			}
		}
		return invited;
	}

	public void joinPlayer(String email) {
		Player invited = null;
		for (Player p : model.getPlayers()) {
			if (p instanceof HumanPlayer) {
				HumanPlayer human = (HumanPlayer) p;
				if (human.getEmail().equals(email)) {
					invited = p;
					break;
				}
			}
		}
		if (invited != null && PlayerStatusEnum.INVITED.equals(invited.getStatus()) ) {
			invited.setStatus(PlayerStatusEnum.PRESENT);
			if (gameControllerListener != null) {
				gameControllerListener.onPlayerJoined(invited);
			}
		}
		if (isReadyToStart()) {
			if (gameControllerListener != null) {
				gameControllerListener.onStartGame();
			}
		}
		model.setDirty(true);
		doPaint();
	}

	public void leavePlayer(String email) {
		Player found = null;
		for (Player p : model.getPlayers()) {
			if (p instanceof HumanPlayer) {
				HumanPlayer human = (HumanPlayer) p;
				if (human.getEmail().equals(email) & !PlayerStatusEnum.GONE.equals(human.getStatus())) {
					found = p;
					break;
				}
			}
		}
		if (found != null) {
			found.setStatus(PlayerStatusEnum.GONE);
			if (gameControllerListener != null) {
				gameControllerListener.onPlayerLeft(found);
			}
		}
		if (isNeverReadyToStart()) {
			if (gameControllerListener != null) {
				gameControllerListener.onNeverReadyToStart();
			}
		}
	}

	protected InputStream getUIResource(int resourceId) {
		return getResourceProvider().getResource(resourceId);
	}
	
	public InputStream getUIResource(Long resourceAlias) {
		return getUIResource(resourceAlias.intValue());
	}

	/**
	 * Returns a z-sorted map of all selectables for current turn/conditions
	 * @param selection
	 * @return
	 */
	public abstract Map<Integer, GameSelection> getSelectables();
	
	protected abstract Map<Integer, GameSelection> getSelectables(int playerIndex);
	
}
