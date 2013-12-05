package edu.wm.werewolf.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.MongoException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

import edu.wm.werewolf.exceptions.NoPlayerFoundException;
import edu.wm.werewolf.model.GPSLocation;
import edu.wm.werewolf.model.Player;


public class MongoPlayerDAO implements IPlayerDAO {
	
	private final Logger logger = LoggerFactory.getLogger(MongoPlayerDAO.class);
	
	public static final String DATABASE_NAME = "werewolf";
	public static final String COLLECTION_NAME = "players";

	// Connects to instance of MongoClient
	@Autowired private DB db;

	// Private method to get collection "players" from "werewolf" mongoDB
	private DBCollection getCollection(){
		DBCollection coll = null;	

		if (db == null){
			logger.error("No mongo instance!");
		}
		
		try {
			
			// Get collection "players" from mongoDB
			coll = db.getCollection(COLLECTION_NAME);
		}
		catch (MongoException ex) {}
		
		// Return "players" collection
		return coll;
	}
	

	private Player convertFromObject(DBObject object) {
		ObjectId streamid = (ObjectId) object.get("_id");
		
		String id = streamid.toString();		
		
		Player player = new Player(id, (String) object.get("userId"), (boolean) object.get("isDead"),
				(boolean) object.get("isWerewolf"), (double) object.get("lat"), 
				(double) object.get("lng"), (String) object.get("votedAgainst"));
		
		return player;
	}
	

	@Override
	public Player getPlayerById(String userId) throws NoPlayerFoundException {
		DBCollection collection = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("userId", userId);
		
		DBObject object = collection.findOne(query);
		
		Player player = convertFromObject(object);
		
		return player;
	}	
	

	@Override
	public void createPlayer(Player player) {
		DBCollection collection = getCollection();
		
		// Create Player Object for User
		BasicDBObject playerDoc = new BasicDBObject();
		playerDoc.put("id", player.getId());
		playerDoc.put("userId", player.getUserId());
		playerDoc.put("isDead", player.isDead());
		playerDoc.put("isWerewolf", player.isWerewolf());
		playerDoc.put("lat", player.getLat());
		playerDoc.put("lng", player.getLng());
		playerDoc.put("votedAgainst", player.getVotedAgainst());
		
		collection.insert(playerDoc);
	}
	
	 
	public void updatePlayer(Player player){
		DBCollection collection = getCollection();
		
		// find User to add Player object to via ObjectId
		BasicDBObject query = new BasicDBObject();
		query.put("userId", player.getUserId());
		
		// Create Player object to be updated
		BasicDBObject playerDoc = new BasicDBObject();
	
		// Updating characteristics of player
		playerDoc.put("$set", new BasicDBObject().put("playerName", player));
		playerDoc.put("$set", new BasicDBObject().put("userId", player.getUserId()));
		playerDoc.put("$set", new BasicDBObject().put("werewolf", player.isWerewolf()));
		playerDoc.put("$set", new BasicDBObject().put("isDead", player.isDead()));
		playerDoc.put("$set", new BasicDBObject().put("lat", player.getLat()));
		playerDoc.put("$set", new BasicDBObject().put("lng", player.getLng()));
		playerDoc.put("$set", new BasicDBObject().put("votedAgainst", player.getVotedAgainst()));
		
		// Will change only the player that matches the searchQuery BasicDBObject player id
		collection.update(query, playerDoc);	
	}
	
	@Override
	public List<Player> getAllPlayers() {
		
		List<Player> players = new ArrayList<>();
		DBCollection collection = getCollection();
		DBCursor cursor = collection.find();
		
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			Player p = convertFromObject(obj);
			players.add(p);
		}
		
		return players;
	}
	
	@Override
	public List<Player> getAllAlive() {
		
		// Player object list
		List<Player> players = new ArrayList<>();
		
		// Java MongoDB cursor (Not a Spring MongoDB cursor!)
		DBCollection collection = getCollection();
		DBCursor cursor = collection.find();
		
		while(cursor.hasNext()){
			
			// Object found is a DBObject
			DBObject obj = cursor.next();
			
			// Convert DBObject to Player object
			Player p = convertFromObject(obj);
			
			// If player alive, add to List<Player>
			if (!p.isDead())
				players.add(p);
		}
		
		return players;
	}
	
	
	@Override
	public List<Player> getAllVotable() {
		List<Player> voters = new ArrayList<>();
		
		List<Player> players = getAllAlive();
		for(Player p : players) {
				voters.add(p);
		}
		
		return voters;
	}
	

	@Override
	public void setPlayerLocation(String id, GPSLocation loc){
		DBCollection collection = getCollection();
		
		// Find player to setLocation
		BasicDBObject query = new BasicDBObject();
		query.put("userId", id);
		
		// Set a Player's Location
		BasicDBObject locDoc = new BasicDBObject();
		locDoc.put("$set",  new BasicDBObject().put("lat", loc.getLat()));  
		locDoc.put("$set",  new BasicDBObject().put("lng", loc.getLng()));
		
		collection.update(query, locDoc);
	}
	
	
	@Override
	public void setDead(Player player) {
		DBCollection collection = getCollection();
		BasicDBObject newDocument = new BasicDBObject();
		
		// $set updates a particular value, instead of replacing entire document
		newDocument.put("$set",  new BasicDBObject().put("isDead", true));
		
		BasicDBObject searchQuery = new BasicDBObject().append("_id", player.getId());
		
		collection.update(searchQuery, newDocument);
	}

	// Deletes Player collection by dropping collection from mongoDB
	@Override
	public void dropAllPlayers() {
		DBCollection players = getCollection();
		players.drop();
	}

	
}
