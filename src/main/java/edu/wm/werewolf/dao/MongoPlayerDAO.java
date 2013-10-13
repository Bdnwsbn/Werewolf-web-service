package edu.wm.werewolf.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

import edu.wm.werewolf.exceptions.NoPlayerFoundException;
import edu.wm.werewolf.model.GPSLocation;
import edu.wm.werewolf.model.Player;
import edu.wm.werewolf.model.User;


public class MongoPlayerDAO implements IPlayerDAO {
	
	private final Logger logger = LoggerFactory.getLogger(MongoPlayerDAO.class);
	
	public static final String DATABASE_NAME = "werewolf";
	public static final String COLLECTION_NAME = "players";

	// Connects to instance of MongoClient
	@Autowired private MongoClient mongoClient;

	// Private method to get collection "players" from "werewolf" mongoDB
	private DBCollection getCollection(){
		DB db;
		DBCollection coll = null;	

		if (mongoClient == null){
			logger.error("No mongo instance!");
		}
		
		try {
			// Connecting to "werewolf" mongoDB 
			db = mongoClient.getDB(DATABASE_NAME);
			
			// Get collection "players" from mongoDB
			coll = db.getCollection(COLLECTION_NAME);
		}
		catch (MongoException ex) {}
		
		// Return "players" collection
		return coll;
	}
	

	private Player convertFromObject(DBObject object) {
		Player player = new Player();
		
		ObjectId streamid = (ObjectId) object.get("_id");
		
		String id = streamid.toString();
		player.setId(id);
		player.setUserId((String) object.get("userId"));
		player.setDead((Boolean) object.get("isDead"));
		player.setLat((double) object.get("lat"));
		player.setLng((double) object.get("lng"));
		player.setWerewolf((boolean) object.get("isWerewolf"));
		player.setVotedAgainst((String) object.get("votedAgainst"));
		
		return player;
	}
	

	@Override
	public Player getPlayerById(String id) throws NoPlayerFoundException {
		DBCollection collection = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		
		DBObject object = collection.findOne(query);
		
		Player player = convertFromObject(object);
		
		return player;
	}	
	

	@Override
	public void createPlayer(Player player) {
		DBCollection collection = getCollection();
		
		// Create Player Object for User
		BasicDBObject playerDoc = new BasicDBObject();
		playerDoc.append("id", player);
		playerDoc.append("userId", player.getUserId());
		playerDoc.append("werewolf", player.isWerewolf());
		playerDoc.append("isDead", player.isDead());
		playerDoc.append("lat", player.getLat());
		playerDoc.append("lng", player.getLng());
		playerDoc.append("votedAgainst", player.getVotedAgainst());
		
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
		playerDoc.append("$set", new BasicDBObject().append("playerName", player));
		playerDoc.append("$set", new BasicDBObject().append("userId", player.getUserId()));
		playerDoc.append("$set", new BasicDBObject().append("werewolf", player.isWerewolf()));
		playerDoc.append("$set", new BasicDBObject().append("isDead", player.isDead()));
		playerDoc.append("$set", new BasicDBObject().append("lat", player.getLat()));
		playerDoc.append("$set", new BasicDBObject().append("lng", player.getLng()));
		
		// Will change only the player that matches the searchQuery BasicDBObject player id
		collection.update(query, playerDoc);	
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
		locDoc.append("$set",  new BasicDBObject().append("lat", loc.getLat()));  
		locDoc.append("$set",  new BasicDBObject().append("lng", loc.getLng()));
		
		collection.update(query, locDoc);
	}
	
	
	@Override
	public void setDead(Player player) {
		DBCollection collection = getCollection();
		BasicDBObject newDocument = new BasicDBObject();
		
		// $set updates a particular value, instead of replacing entire document
		newDocument.append("$set",  new BasicDBObject().append("isDead", true));
		
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
