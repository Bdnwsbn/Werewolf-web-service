package edu.wm.werewolf.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import edu.wm.werewolf.dao.IPlayerDAO;
import edu.wm.werewolf.dao.IUserDAO;
import edu.wm.werewolf.exceptions.NoPlayerFoundException;
import edu.wm.werewolf.model.GPSLocation;
import edu.wm.werewolf.model.Game;
import edu.wm.werewolf.model.Kill;
import edu.wm.werewolf.model.MyUser;
import edu.wm.werewolf.model.Player;
import edu.wm.werewolf.model.User;

/**
 * All logic for the game is stored here. Uses DAO with Models to perform game tasks. 
 */
public class GameService {
	
	private static final Logger logger = LoggerFactory.getLogger(GameService.class);
	
	@Autowired private IPlayerDAO playerDao;
	@Autowired private IUserDAO userDao;

	// Dont think I need this yet...
/*	public void startGame(){
		logger.info("Game is starting");
		
		List<MyUser> users = userDao.getAllUsers();
		List<Player> players = new ArrayList<>();
		
		for (MyUser u: users){
			Player p = new Player();
			p.setUserId(u.getId());
			playerDao.createPlayer(p);
			players.add(p);
		}	
		
		// So that players aren't always a werewolf or townsperson
		Collections.shuffle(players, new Random(System.currentTimeMillis()));
		
		// Index range of players in players list to be Werewolves
		// Aka: If werewolfIndex = 3, then take top three players from player list
		int werewolfindex = (int) (players.size()*0.30f);
	}*/
	
	
	// Create players: Check
	// Make some players WWs:Check
	// Reset game properties (createdDate, dayNightFreq): Check  (Supposed to do this??) 
		// -- or pass through Game properties for restartGame? 
	// Make someone an Admin?
	
	// Method to restart the game
	public void restartGame(){
		logger.info("Game is restarting");
		
		playerDao.dropAllPlayers();
		
		List<MyUser> users = userDao.getAllUsers();
		List<Player> players = new ArrayList<>();
		
		// Create players for all Users
		for (MyUser u: users){
			Player p = new Player();
			p.setUserId(u.getId());
			playerDao.createPlayer(p);
			players.add(p);
		}	
		
		// Randomly shuffles list of players
		Collections.shuffle(players, new Random(System.currentTimeMillis()));
		
		// Index number to make 30% of players Werewolves
		int werewolfIndex = (int) (players.size()*0.30f);
		
		// Set players to Werewolves
		for (int i=0; i < werewolfIndex; i++ ){
			players.get(i).setWerewolf(true);
		}
		
		// Supposed to do this??
		//---------------------------Reset Game Properties-----------------------------//
		Game game = new Game(12, new Date());
	}
	
	public List<Player> getAllAlive() {
		return playerDao.getAllAlive();
	}
	
	// Returns list of playerIds
	public List<Player> getAllVotable() {
		return playerDao.getAllVotable();
	}
	
	// MAKE IN PLAYERDAO - but can't since use reportCurrentPosition!
	// For now I am assuming that the player is a werewolf and not validating that....
	// Finds all townspeople near the Werewolf
	public List<Player> getAllNearby(Player werewolf) throws NoPlayerFoundException{
		List<Player> playersNearby = new ArrayList<>();
		
		// Werewolf's Location
		GPSLocation wwLoc = new GPSLocation(werewolf.getLat(), werewolf.getLng());
		wwLoc = reportCurrentPosition(werewolf.getUserId(), wwLoc);
		
		// Search for nearby alive townspeople
		List<Player> players = getAllAlive();
		for (Player p : players){
			if (!p.isWerewolf()) {
				// Get current position of townsperson
				GPSLocation pLoc = new GPSLocation(p.getLat(), p.getLng());
				pLoc = reportCurrentPosition(p.getUserId(),pLoc);
				
				// Check to see if near Werewolf
				double distance = Math.sqrt( (wwLoc.getLat() - pLoc.getLat())*
						(wwLoc.getLat() - pLoc.getLat())
						+ (wwLoc.getLng() - pLoc.getLng())*
						(wwLoc.getLng() - pLoc.getLng()));
				
				// Scent Radius (2000) Kill Radius (15000)
				if (distance <= 2000)
					playersNearby.add(p);
			}
		}
		return playersNearby;
	}
	
	// Updates an alive player's position
	// Potential Bug: If player is not alive, location not updated. May say player is still at last location
	public void updatePosition(String username, GPSLocation location) throws NoPlayerFoundException{
		 User u = userDao.getUserByName(username);
		 Player p = playerDao.getPlayerById(u.getId());
		 
		 if (!p.isDead())
			 playerDao.setPlayerLocation(u.getId(), location);
	}
	
	public GPSLocation reportCurrentPosition(String username, GPSLocation location) throws NoPlayerFoundException{
		updatePosition(username,location);
		
		 User u = userDao.getUserByName(username);
		 Player p = playerDao.getPlayerById(u.getId());
		 location.setLat(p.getLat());
		 location.setLng(p.getLng());
		
		 return location;
	}
	
	// Determines if a Werewolf can kill a townsperson
	// Player can kill if isWerewolf, game is at night stage, and victim is within kill radius
	public Boolean canKill(Player killer, Player victim) throws NoPlayerFoundException{
		
		Game game = new Game(0, null);
		
		if (killer.isWerewolf() && game.isNight()){
			GPSLocation killerLoc = new GPSLocation(killer.getLat(), killer.getLng());
			GPSLocation victimLoc = new GPSLocation(victim.getLat(), victim.getLng());
			victimLoc = reportCurrentPosition(victim.getUserId(), victimLoc);
			killerLoc = reportCurrentPosition(killer.getUserId(), killerLoc);		
			
			double distance = Math.sqrt( (killerLoc.getLat() - victimLoc.getLat())*
					(killerLoc.getLat() - victimLoc.getLat())
					+ (killerLoc.getLng() - victimLoc.getLng())*
					(killerLoc.getLng() - victimLoc.getLng()));
			
			// If victim within scent radius, false
			if (distance <= 2000 && distance >= 1500)
				return false;
			
			// If victim within kill radius, true
			if (distance <= 1500) {
				return true;
			}
			else
				return false;
		}
		return false;
	}

	// Will want to award points to Werewolf (killer) and then call updatePlayer.
	// Kills player if canKill true
	public void killPlayer(Player killer, Player victim) throws NoPlayerFoundException{
		
		if (canKill(killer, victim)){
			victim.setDead(true);
			playerDao.updatePlayer(victim);
			playerDao.updatePlayer(killer);
			
			Date date = new Date();
			Kill k = new Kill(killer.getUserId(), victim.getUserId(), date,
					(float) killer.getLat(), (float) killer.getLng());
		}
	}
	
	
	
	// Possible problem - must reset votedAgainst or else wont be able to vote after
	// the first time the way I have this set up
	// Fix game
	// Should I write something for the else?
	public void votePlayer(Player voter, Player votee) {
		Game game = new Game(12, null);
		
		// If voter hasnt voted, is alive, and its day time, and votee can be voted for
		if (!game.isNight() && !voter.isDead() && voter.getVotedAgainst() == null){
			List<Player> votables = getAllVotable();
			if (votables.contains(votee)) {
				
				// Vote is valid
				voter.setVotedAgainst(votee.getId());
			}
		}	
	}
	
	public void getHighScore(){
		
	}
	
	@Scheduled(fixedDelay=5000)
	public void checkGameOperation(){
		//something that should execute periodically
		logger.info("Checking game operation...");
		
		//check every player in game, if update time stamp exceeds X time, update
		// ^^^ not implemented here, elsewhere
	}

}
