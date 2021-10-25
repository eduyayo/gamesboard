package com.pigdroid.hub.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigdroid.game.GameControllerProvider;
import com.pigdroid.game.controller.GameController;
import com.pigdroid.game.controller.GameControllerListenerAdapter;
import com.pigdroid.game.model.GameSelection;
import com.pigdroid.game.model.HumanPlayer;
import com.pigdroid.game.model.Player;
import com.pigdroid.game.model.PlayerStatusEnum;
import com.pigdroid.hub.model.message.GameMessage;
import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.persistent.Game;
import com.pigdroid.hub.model.persistent.GameEstateEnum;
import com.pigdroid.hub.model.persistent.GameStatistics;
import com.pigdroid.hub.model.persistent.Usr;
import com.pigdroid.hub.repository.GameRepository;
import com.pigdroid.hub.repository.GameStatisticsRepository;
import com.pigdroid.hub.service.GameService;
import com.pigdroid.hub.service.MessageService;
import com.pigdroid.hub.web.rest.dto.GameProfile;
import com.pigdroid.social.model.user.User;
import com.pigdroid.social.service.UserService;

@Service
@Transactional
public class GameServiceImpl extends BaseServiceImpl<Game> implements GameService {

	private static final Log LOG = LogFactory.getLog(GameServiceImpl.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	private UserService userService;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GameStatisticsRepository gameStatisticsRepository;

	@Override
	public void handleMessage(HubMessage hubMessage) {
		ObjectMapper mapper = new ObjectMapper();
		GameMessage message = null;
		try {
			message = mapper.convertValue(mapper.readTree(hubMessage.getPayload()), GameMessage.class);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Optional<Game> optionalGame = gameRepository.findById(Long.valueOf(message.getModelId()));
		Game game = optionalGame.get();
		GameController<?> gameController = GameControllerProvider.createController(game.getGameName());
		gameController.loadModelFromSerialized(game.getSaveGame());
		doGameMsg(gameController, message, hubMessage, game);

//		//game, here, just changed estate
//		game.setSaveGame(gameController.getSerializedModel());
//		gameRepository.save(game);

		//Message here may have a redirection for the other users to update their games:
		if (!hubMessage.getTo().isEmpty()) {
			messageService.sendMessage(hubMessage);
		}
	}

	protected ObjectMapper createSerializer() {
		ObjectMapper ret = new ObjectMapper();
		return ret.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private void doGameMsg(
			final GameController<?> controller,
			final GameMessage gameMessage,
			final HubMessage hubMessage,
			final Game game) {
		final String from = hubMessage.getFrom();
		hubMessage.clearTo();
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
				try {
					hubMessage.setPayload(createSerializer().writeValueAsString(gameMessage));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public boolean onIsPlayerLocal(List<GameSelection> moved) {
				return true; //TODO All users are local to the server? maybe filter out those not in the "from"?
			}

			@Override
			public void onEndGame(Player currentPlayer, boolean winner, boolean loser, boolean tie) {
				super.onEndGame(currentPlayer, winner, loser, tie);
				doStatisicsEndGame(
						(HumanPlayer) currentPlayer,
						winner,
						loser,
						tie,
						game.getId());
				Iterator<Player> it = controller.getPlayers();
				while (it.hasNext()) {
					HumanPlayer player = (HumanPlayer) it.next();
					if (player != currentPlayer) {
						doStatisicsEndGame(
								player,
								controller.isWinner(player),
								controller.isLoser(player),
								controller.isTie(player),
								game.getId());
					}
					//Delete the game. Should mark it as defunct in client implementations
					deleteGame(game);
				}
			}


		});

		List<GameSelection> gameSelections;
		try {
			gameSelections = createSerializer().readValue(gameMessage.getPayload(), new TypeReference<List<GameSelection>>() {
			});
			controller.select(gameSelections.toArray(new GameSelection[gameSelections.size()]));
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (gameRepository.existsById(game.getId())
				&& !GameEstateEnum.FINISHED.equals(game.getEstate())
						&& !GameEstateEnum.INVALID.equals(game.getEstate())) {
			gameRepository.save(game);
		}
	}

	private void deleteGame(Game game) {
		for (Usr usr : game.getUsers()) {
			usr.getGames().remove(game);
		}
		game.getUsers().clear();
		gameRepository.deleteById(game.getId());

	}

	@Override
	public GameProfile addGame(HubMessage message) {
		Game game = new Game();
		game.setEstate(GameEstateEnum.WAITING_PLAYERS);
		game.setGameName(message.getId());
		game.setSaveGame(message.getPayload());
		Set<Usr> users = new HashSet<Usr>();
		User from = userService.findByEmail(message.getFrom());
		users.add(from.getUsr());
		game.setUsers(users);
		gameRepository.save(game);
		GameController<?> gameController = GameControllerProvider.createController(game.getGameName());
		gameController.loadModelFromSerialized(game.getSaveGame());
		for (Iterator <Player> it = gameController.getPlayers(); it.hasNext(); ) {
			HumanPlayer player = (HumanPlayer) it.next();
			User user = userService.findByEmail(player.getEmail()); //TODO could be non human?
			users.add(user.getUsr());
		}
		processNextInvitation(game, gameController);
		GameProfile.Builder builder =
				GameProfile.builder().gameName(game.getGameName())
					.modelId(game.getId()).saveGame(game.getSaveGame());
		for (Iterator <Player> it = gameController.getPlayers(); it.hasNext(); ) {
			HumanPlayer player = (HumanPlayer) it.next();
				builder.player().email(player.getEmail()).status(player.getStatus().toString());
		}
//		for (Iterator <Player> it = gameController.getPlayers(); it.hasNext(); ) {
//			HumanPlayer player = (HumanPlayer) it.next();
//			if (PlayerStatusEnum.PRESENT.equals(player.getStatus())) {
//				sendAddPlayerMessage()
//				builder.player().email(player.getEmail()).status(player.getStatus().toString());
//			}
//		}
		return builder.gameEstate(game.getEstate()).build();
	}

	private void processNextInvitation(final Game game, final GameController<?> gameController) {
		final HumanPlayer player = (HumanPlayer) gameController.getInvitedPlayer();
		if (player != null) {
			GameProfile.Builder builder =
					GameProfile.builder().gameName(game.getGameName())
						.modelId(game.getId()).saveGame(game.getSaveGame()).gameEstate(game.getEstate());
			for (Iterator <Player> it = gameController.getPlayers(); it.hasNext(); ) {
				HumanPlayer p = (HumanPlayer) it.next();
				builder.player().email(player.getEmail()).status(p.getStatus().toString());
			}

			HubMessage message;
			try {
				message = HubMessage.builder().to(player.getEmail()).type(HubMessage.TYPE_ADD_GAME).payload(
						new ObjectMapper().writeValueAsString(builder.build())).from(((HumanPlayer) gameController.getPlayers().next()).getEmail()) .build();
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			message.setId(game.getGameName());
			messageService.sendMessage(message);
			gameRepository.save(game);
		}
		game.setSaveGame(gameController.getSerializedModel());
		gameRepository.save(game);
	}

	@Override
	public List<GameProfile> list(String sessionEmail) {
		User sessionUser = userService.findByEmail(sessionEmail);
		List<GameProfile> ret = new ArrayList<GameProfile>();
		for (Game game : sessionUser.getUsr().getGames()) {
			GameController<?> gameController = GameControllerProvider.createController(game.getGameName());
			gameController.loadModelFromSerialized(game.getSaveGame());
			GameProfile.Builder builder =
					GameProfile.builder().gameName(game.getGameName())
						.modelId(game.getId()).saveGame(game.getSaveGame()).gameEstate(game.getEstate());
			for (Iterator <Player> it = gameController.getPlayers(); it.hasNext(); ) {
				HumanPlayer player = (HumanPlayer) it.next();
				builder.player().email(player.getEmail()).status(player.getStatus().toString());
			}
			ret.add(builder.build());
		}
		return ret;
	}

	@Override
	public Game joinGame(final String sessionEmail, final String id) {
		Optional<Game> optionalGame = gameRepository.findById(Long.valueOf(id));
		Game game = null;
		if (optionalGame.isPresent()) {
			game = optionalGame.get();
			Game finalGame = game;
			final GameController<?> gameController = GameControllerProvider.createController(game.getGameName());
			gameController.loadModelFromSerialized(game.getSaveGame());
			gameController.setGameControllerListener(new GameControllerListenerAdapter() {

				@Override
				public void onPlayerJoined(Player invited) {
					// Whatever happens to the invitation, we save the player for he to have a relation to the game
					User user = userService.findByEmail(sessionEmail);
//created with them already
//					game.getUsers().add(user.getUsr());
//					user.getUsr().getGames().add(game);
					GameMessage payload = GameMessage.builder().type(GameMessage.TYPE_JOIN).modelId(finalGame.getId().toString()).payload(sessionEmail).build();
					HubMessage newMsg = null;
					try {
						newMsg = HubMessage.builder().from(sessionEmail).type(HubMessage.TYPE_MSG_GAME).payload(new ObjectMapper().writeValueAsString(payload)).build();
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
					for (Iterator<Player> iterator = gameController.getPlayers(); iterator.hasNext(); ) {
						Player player = iterator.next();
						if (player instanceof HumanPlayer) {
							HumanPlayer human = (HumanPlayer) player;
							if (PlayerStatusEnum.PRESENT.equals(human.getStatus()) && !sessionEmail.equals(human.getEmail())) {
								newMsg.addTo(human.getEmail());
							}
						}
					}
					messageService.sendMessage(newMsg);
				}

				@Override
				public void onStartGame() {
					finalGame.setEstate(GameEstateEnum.STARTED);
				}

			});
			gameController.joinPlayer(sessionEmail);
			game.setSaveGame(gameController.getSerializedModel());
			gameRepository.save(game);
			processNextInvitation(game, gameController);
		}
		return game;
	}

	@Override
	public void leaveGame(final String sessionEmail, final String id) {
		final Optional<Game> optionalGame = gameRepository.findById(Long.valueOf(id));
		if (optionalGame.isPresent()) {
			Game game = optionalGame.get();
			final GameController<?> gameController = GameControllerProvider.createController(game.getGameName());
			gameController.loadModelFromSerialized(game.getSaveGame());
			gameController.setGameControllerListener(new GameControllerListenerAdapter() {

				@Override
				public void onPlayerLeft(Player found) {
					super.onPlayerLeft(found);
					game.remove(userService.findByEmail(sessionEmail));
					gameRepository.save(game);
					// Send messages to the rest of the people about this guy leaving.
					// Their local implementations will handle the case when they cannot go on

					GameMessage payload = GameMessage.builder().type(GameMessage.TYPE_REJECT).modelId(game.getId().toString()).payload(sessionEmail).build();
					HubMessage newMsg = null;
					try {
						newMsg = HubMessage.builder().from(sessionEmail).type(HubMessage.TYPE_MSG_GAME).payload(new ObjectMapper().writeValueAsString(payload)).build();
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
					for (Iterator<Player> iterator = gameController.getPlayers(); iterator.hasNext(); ) {
						Player player = iterator.next();
						if (player instanceof HumanPlayer) {
							HumanPlayer human = (HumanPlayer) player;
							if (PlayerStatusEnum.PRESENT.equals(human.getStatus()) && !sessionEmail.equals(human.getEmail())) {
								newMsg.addTo(human.getEmail());
							}
						}
					}
					messageService.sendMessage(newMsg);
					game.setSaveGame(gameController.getSerializedModel());
					if (gameRepository.existsById(game.getId())
							&& !GameEstateEnum.FINISHED.equals(game.getEstate())
									&& !GameEstateEnum.INVALID.equals(game.getEstate())) {
						gameRepository.save(game);
					}
				}

				@Override
				public void onNeverReadyToStart() {
					super.onNeverReadyToStart();
					// The leave message will be handled by client implementations and the game state will be set to DEFUNCT in each case.
					// We just delete the game in here so for next refreshment it won´t be downloaded.
					// We don´t need it, we delete it.
					deleteGame(game);
				}

			});
			gameController.leavePlayer(sessionEmail);
		}
	}

	@Transactional
	private void doStatisicsEndGame(HumanPlayer currentPlayer, boolean winner, boolean loser, boolean tie, Long gameId) {
		if (winner) {
			doStatisticsWin(
					userService.findByEmail(currentPlayer.getEmail()),
					gameRepository.findById(gameId).get());
		} else if (loser) {
			doStatisticsLose(
					userService.findByEmail(currentPlayer.getEmail()),
					gameRepository.findById(gameId).get());
		} else if (tie) {
			doStatisticsTie(
					userService.findByEmail(currentPlayer.getEmail()),
					gameRepository.findById(gameId).get());
		}
	}

	private GameStatistics getStatistics(User user, Game game) {
		Map<String, GameStatistics> map = user.getUsr().getGameStatistics();
		GameStatistics stats = map.get(game.getGameName());
		if (stats == null) {
			stats = new GameStatistics();
			String name = game.getGameName();
			stats.setGameName(name);
			map.put(name, stats);
		}
		return stats;
	}

	@Transactional
	private void doStatisticsLeave(final User user, final Game game) {
		GameStatistics stats = getStatistics(user, game);
		stats.incRetired();
		gameStatisticsRepository.save(stats);
	}

	@Transactional
	private void doStatisticsWin(final User user, final  Game game) {
		GameStatistics stats = getStatistics(user, game);
		stats.incWon();
		gameStatisticsRepository.save(stats);
	}

	@Transactional
	private void doStatisticsLose(final User user, final Game game) {
		GameStatistics stats = getStatistics(user, game);
		stats.incLost();
		gameStatisticsRepository.save(stats);
	}

	@Transactional
	private void doStatisticsTie(final User user, final Game game) {
		GameStatistics stats = getStatistics(user, game);
		stats.incTied();
		gameStatisticsRepository.save(stats);
	}

}
