package edu.wm.werewolf.model;

import java.util.ArrayList;
import java.util.List;


// Return empty list object and error message
public class PlayerListResponse extends JsonResponse {
	
	private List<Player> playerList = new ArrayList<>();
	public String setStatus;
	
	// General Constructor
	public PlayerListResponse(List<Player> playerList){
		super();
		this.playerList = playerList;
	}

	public void setPlayerList(List<Player> playerList) {
		this.playerList = playerList;
		
	}

	public List<Player> getPlayerList() {
		return playerList;
	}
	

}
