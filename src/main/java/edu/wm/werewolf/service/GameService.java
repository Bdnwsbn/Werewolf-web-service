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

import edu.wm.werewolf.dao.IGameDAO;
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
	@Autowired private IGameDAO gameDao;
	

	public void restartGame(){
		logger.info("Game is restarting");
		
		playerDao.dropAllPlayers();
		
		List<MyUser> users = userDao.getAllUsers();
		List<Player> players = new ArrayList<>();
		
		// Create players for all Users unless User is admin
		for (MyUser u: users){
			if(!u.getIsAdmin()) {
				Player p = new Player();
				p.setUserId(u.getId());
				playerDao.createPlayer(p);
				players.add(p);
			}
		}	
		
		// Randomly shuffles list of players
		Collections.shuffle(players, new Random(System.currentTimeMillis()));
		
		// Index number to make 30% of players Werewolves
		int werewolfIndex = (int) (players.size()*0.30f);
		
		// Set players to Werewolves
		for (int i=0; i < werewolfIndex; i++ ){
			players.get(i).setWerewolf(true);
		}
		
		// Create a new game
		Date date = new Date();
		Game game = gameDao.getCurrentGame();
		Game newGame = new Game(game.getId(), date, game.getIsNight(), game.getDayNightFreq(),
								game.getIsRunning(), game.getTimer());
		gameDao.createGame(newGame);
	}
	
	public List<Player> getAllPlayers(){
		return playerDao.getAllPlayers();
	}
	
	public List<Player> getAllAlive() {
		return playerDao.getAllAlive();
	}
	
	public List<Player> getAllVotable() {
		return playerDao.getAllVotable();
	}
	

// change to not player werewolf but to find player object in mongoPlayerDAO via player name	
	public List<Player> getAllNearby(String username, List<Player> playersNearby) 
										throws NoPlayerFoundException {
		 MyUser u = userDao.getUserByUsername(username);
		 Player player = playerDao.getPlayerById(u.getId());
		
		if (player.isWerewolf()) {
			
			// Werewolf's Location
			GPSLocation wwLoc = new GPSLocation(player.getLat(), player.getLng());
			wwLoc = reportCurrentPosition(player.getUserId());
			
			// Search for nearby alive townspeople
			List<Player> players = getAllAlive();
			for (Player p : players){
				if (!p.isWerewolf()) {
					// Get current position of townsperson
					GPSLocation pLoc = new GPSLocation(p.getLat(), p.getLng());
					pLoc = reportCurrentPosition(p.getUserId());
					
					// Check to see within radius of Werewolf
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
		return playersNearby;
	}
	

	public void updatePosition(String username, GPSLocation location) throws NoPlayerFoundException{
		 MyUser u = userDao.getUserByUsername(username);
		 String id = (String) u.getId();
		 // **u.getId() is still large string!! not "1"
		 Player p = playerDao.getPlayerById(id);
		 
//		 GPSLocation location = reportCurrentPosition(username);
		 
		 if (!p.isDead())
			 playerDao.setPlayerLocation(u.getId(), location);
	}
	
	
	public GPSLocation reportCurrentPosition(String username) throws NoPlayerFoundException{
		GPSLocation location = new GPSLocation(); 
//		updatePosition(username);
		
		MyUser u = userDao.getUserByUsername(username);
		Player p = playerDao.getPlayerById(u.getId());
		location.setLat(p.getLat());
		location.setLng(p.getLng());
		
		return location;
	}
	

	public Boolean canKill(Player killer, Player victim) throws NoPlayerFoundException{
		Game game = gameDao.getCurrentGame();
		
		if (killer.isWerewolf() && game.getIsNight()){
			GPSLocation killerLoc = new GPSLocation(killer.getLat(), killer.getLng());
			GPSLocation victimLoc = new GPSLocation(victim.getLat(), victim.getLng());
			victimLoc = reportCurrentPosition(victim.getUserId());
			killerLoc = reportCurrentPosition(killer.getUserId());		
			
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

	// *****Does nothing with Kill object...maybe to keep track of kills list?*****
	// Will want to award points to Werewolf (killer) and then call updatePlayer.
	public void killPlayer(String killer, String victim) throws NoPlayerFoundException{
		Player playerKiller = playerDao.getPlayerById(killer);
		Player playerVictim = playerDao.getPlayerById(victim);
		if (canKill(playerKiller, playerVictim)){
			playerVictim.setDead(true);
			playerDao.updatePlayer(playerVictim);
			playerDao.updatePlayer(playerKiller);
			
			Date date = new Date();
			Kill k = new Kill(playerKiller.getUserId(), playerVictim.getUserId(), date,
					(double) playerKiller.getLat(), (double) playerKiller.getLng());
		}
	}
	
	
	
	// Possible problem - must reset votedAgainst or else wont be able to vote after
	// the first time the way I have this set up
	// Should I write something for the else?
	public void votePlayer(String voter, String votee) throws NoPlayerFoundException {
		Game game = gameDao.getCurrentGame();
		Player playerVoter = playerDao.getPlayerById(voter);
		Player playerVotee = playerDao.getPlayerById(votee);
		
		if (!game.getIsNight() && !playerVoter.isDead() && playerVoter.getVotedAgainst() == null){
			List<Player> votables = getAllVotable();
			if (votables.contains(playerVotee)) {
				
				playerVotee.setVotedAgainst(playerVotee.getId());
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
