package com.pigdroid.hub.model.persistent;

public enum GameEstateEnum {
//	LOCAL_CREATED,		// Just created and told to server.
	WAITING_PLAYERS,	// Server responded or user accepted an invitation.
	STARTED,			// All players are in and we can start or continue.
	FINISHED,			// win or lose conditions.
	INVALID				// Too many players left or any other kind of trouble.
}
/*
 * flow goes:
 * LOCAL -> WAITING -> STARTED -> FINISHED
 * 							\> INVALID
 * 
 * Also could go invalid after whatever the trouble
 * */
