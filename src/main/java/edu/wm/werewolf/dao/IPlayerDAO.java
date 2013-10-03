package edu.wm.werewolf.dao;

import java.util.List;

import edu.wm.werewolf.exceptions.NoPlayerFoundException;
import edu.wm.werewolf.model.GPSLocation;
import edu.wm.werewolf.model.Player;

// Player Interface
public interface IPlayerDAO {
	
	public void createPlayer(Player player);
	
	public List<Player> getAllAlive();
	
	public void setDead(Player player);

	public Player getPlayerById(String id) throws NoPlayerFoundException;

	void setPlayerLocation(String id, GPSLocation loc);

	public void dropAllPlayers();

	public List<Player> getAllVotable();

	public void updatePlayer(Player victim);
}
