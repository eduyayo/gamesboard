package com.pigdroid.hub.web.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pigdroid.hub.model.message.HubMessage;
import com.pigdroid.hub.model.persistent.Game;
import com.pigdroid.hub.service.GameService;
import com.pigdroid.hub.web.rest.dto.GameProfile;

@RestController
public class GameController {
	
	@Autowired
	private GameService gameService;
	
	@RequestMapping(value = "/user/games", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<GameProfile>> list() {
		String sessionLogin = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<GameProfile> list = gameService.list(sessionLogin);
		ResponseEntity<List<GameProfile>> ret = new ResponseEntity<List<GameProfile>>(list, HttpStatus.OK);
		return ret;
	}

	@RequestMapping(value = "/games", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<GameProfile>> listContacts() {
		return list();
	}
	
	@RequestMapping(value = "/game", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<GameProfile> add(@RequestBody HubMessage message) {
		GameProfile game = gameService.addGame(message);
		return new ResponseEntity<GameProfile>(game, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/game/{id}/join", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<GameProfile> joinGame(@PathVariable("id") String id) {
		String sessionLogin = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Game game = gameService.joinGame(sessionLogin, id);
		return new ResponseEntity<GameProfile>(
				GameProfile.builder().status(game.getEstate().toString()).saveGame(game.getSaveGame()).gameName(game.getGameName()).modelId(game.getId()).build(), 
				HttpStatus.CREATED);
	}

	@RequestMapping(value = "/game/{id}/leave", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Void> leaveGame(@PathVariable("id") String id) {
		String sessionLogin = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		gameService.leaveGame(sessionLogin, id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
}
