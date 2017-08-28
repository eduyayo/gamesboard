package com.pigdroid.hub.service.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pigdroid.game.GameControllerProvider;
import com.pigdroid.game.controller.GameController;
import com.pigdroid.game.controller.GameControllerListenerAdapter;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.PlayerStatusEnum;
import com.pigdroid.hub.dao.CRUDInterface;
import com.pigdroid.hub.dao.impl.GameDAO;
import com.pigdroid.hub.model.message.GameMessage;
import com.pigdroid.hub.model.message.GameMessageContext;
import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.message.MessageContext;
import com.pigdroid.hub.model.persistent.Game;
import com.pigdroid.hub.model.persistent.GameEstateEnum;
import com.pigdroid.hub.model.persistent.User;
import com.pigdroid.hub.service.GameServiceInterface;
import com.pigdroid.hub.service.MessageServiceInterface;
import com.pigdroid.hub.service.UserServiceInterface;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Service
@Transactional
public class GameService extends BaseService<Game> implements GameServiceInterface {
	
	private static final Log LOG = LogFactory.getLog(GameService.class);
	
	private MessageServiceInterface messageService = null;
	private UserServiceInterface userService = null;
	
	public void setGameDAO(CRUDInterface<Game> in) {
		this.dao = in;
	}	
	
	public void setUserService(UserServiceInterface userService) {
		this.userService = userService;
	}
	
	public void setMessageService(MessageServiceInterface messageService) {
		this.messageService = messageService;
	}

	@Override
	public void handleMessage(MessageContext context) {
		HubMessage hubMessage = context.getMessage();
		JSONDeserializer<GameMessage> deserializer = new JSONDeserializer<GameMessage>();
		GameMessage gameMessage = deserializer.deserialize(hubMessage.getPayload());
		GameController<?> gameController = null;

		gameController = GameControllerProvider.createController(hubMessage.getWhat());
//gameController = createGameController(context, );
		handleGameMessage(
				new GameMessageContext(gameMessage, context, gameController, hubMessage.getWhat()));
	}

	private void handleGameMessage(GameMessageContext gameMessageContext) {
		GameMessage gameMessage = gameMessageContext.getGameMessage();
		switch (gameMessage .getType()) {
		case GameMessage.TYPE_CREATE:
			doCreateGame(gameMessageContext); 
			break;
		case GameMessage.TYPE_JOIN:
			doJoinGame(gameMessageContext);
			break;
		case GameMessage.TYPE_REJECT:
			doRejectGame(gameMessageContext);
			break;
		case GameMessage.TYPE_TURN:
			doGameTurn(gameMessageContext);
			break;
		case GameMessage.TYPE_MSG:
			doGameMsg(gameMessageContext);
			break;
		default:
			break;
		}
	}

	private void doGameMsg(final GameMessageContext context) {
		final GameMessage gameMessage = context.getGameMessage();
		final GameController<?> controller = context.getGameController();
		final Game game = get(gameMessage.getModelId());
		final HubMessage hubMessage = context.getMessage();
		final String from = hubMessage.getFrom();
		hubMessage.clearTo();
		hubMessage.setFrom(context.getUserEmail());
		controller.setGameControllerListener(new GameControllerListenerAdapter() {
			@Override
			public void onSendSelections(List<GameSelection> selectionsCopy) {
				for (Iterator<Player> iterator = controller.getPlayers(); iterator.hasNext(); ) {
					Player player = iterator.next();
					if (player instanceof HumanPlayer) {
						HumanPlayer human = (HumanPlayer) player;
						if (!human.getEmail().equals(from)) {
							hubMessage.addTo(human.getEmail());
						}
					}
				}
				hubMessage.setPayload(new JSONSerializer().serialize(gameMessage));
				messageService.sendMessage(context, hubMessage, true);
			}
			
			@Override
			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				return true;
			}
		});
		controller.loadModelFromSerialized(game.getSaveGame());
		List<GameSelection> gameSelections = 
				new JSONDeserializer<List<GameSelection>>().deserialize(
						gameMessage.getPayload());
		controller.select(gameSelections.toArray(new GameSelection[gameSelections.size()]));
		game.setSaveGame(controller.getSerializedModel());
	}

	private void doGameTurn(GameMessageContext context) {
		// TODO Auto-generated method stub
	}

	private void doRejectGame(GameMessageContext context) {
		// TODO Auto-generated method stub
	}

	private void doJoinGame(final GameMessageContext context) {
		final GameController<?> controller = context.getGameController();
		final Game game = get(context.getGameMessage().getModelId());
		controller.setGameControllerListener(new GameControllerListenerAdapter() {
			@Override
			public void onPlayerJoined(final Player invited) {
				Player player = controller.getInvitedPlayer();
				if (player != null && player instanceof HumanPlayer) {
					HumanPlayer human = (HumanPlayer) player;
					HubMessage message = new HubMessage(HubMessage.TYPE_MSG);
					message.addTo(human.getEmail());
					JSONSerializer serializer = new JSONSerializer();
					GameMessage payload = 
							new GameMessage(GameMessage.TYPE_CREATE, null, game.getId(), game.getSaveGame());
					message.setPayload(serializer.deepSerialize(payload));
					messageService.sendMessage(context, message, true);
				}
				
				// And notify the rest of the players:
				Iterator<Player> players = controller.getPlayers();
				while(players.hasNext()) {
					Player recipient = players.next();
					if (recipient instanceof HumanPlayer) {
						HumanPlayer human = (HumanPlayer) recipient;
						HubMessage message = context.getMessage();
						if (PlayerStatusEnum.PRESENT.equals(recipient.getStatus())) {
							message.clearTo().addTo(human.getEmail());
							messageService.sendMessage(context, message, true);
						}
					}
				}
				
				//
				HubMessage message = new HubMessage(HubMessage.TYPE_ACK, HubMessage.TYPE_MSG, game.getId());
				message.addTo(context.getUserEmail());
				messageService.sendMessage(context, message);
			}
			
			@Override
			public void onStartGame() {
				game.setEstate(GameEstateEnum.STARTED);
			}
		});
		controller.loadModelFromSerialized(game.getSaveGame());
		controller.joinPlayer(context.getUserEmail());
		game.setSaveGame(controller.getSerializedModel());
		save(game);
	}

	private void doCreateGame(GameMessageContext context) {
		GameMessage message = context.getGameMessage();
		GameController<?> controller = createGameControllerFromPayload(context, message);
		
		Game game = new Game();
		game.setGameName(context.getMessage().getWhat());
		game.setSaveGame(controller.getSerializedModel());
		game.setId(message.getModelId());
		
		//Add the players to the game
		for (Iterator<Player> it = controller.getPlayers(); it.hasNext(); ) {
			Player player = it.next();
			if (player instanceof HumanPlayer) {
				HumanPlayer human = (HumanPlayer) player;
				User user = userService.getByEmail(human.getEmail());
				game.add(user);
			}
		}
		save(game);
		
		//Let´s kick off the player invitations round by send the first message
		Player player = controller.getInvitedPlayer();
		if (player == null) {
			throw new RuntimeException("No players where invited.");
		}
		HubMessage hubMessage = context.getMessage();
		hubMessage.clearTo().addTo(((HumanPlayer) player).getEmail());
		messageService.sendMessage(context, hubMessage, true);
	}

	private GameController<?> createGameControllerFromPayload(GameMessageContext context,
			GameMessage message) {
		String saveGame = message.getPayload();
		GameController<?> controller = context.getGameController();
		controller.loadModelFromSerialized(saveGame);
		return controller;
	}

//	private GameController<?> createGameController(GameMessageContext context, String modelId) {
//		Game game = get(modelId);
//		GameController<?> controller = context.getGameController();
//		controller.loadModelFromSerialized(game.getSaveGame());
//		return controller;
//	}

}
