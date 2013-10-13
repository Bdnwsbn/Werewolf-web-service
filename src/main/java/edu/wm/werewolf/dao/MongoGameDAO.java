package edu.wm.werewolf.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import edu.wm.werewolf.exceptions.UserAlreadyExistsException;
import edu.wm.werewolf.model.Game;
import edu.wm.werewolf.model.MyUser;

public class MongoGameDAO implements IGameDAO {

	private final Logger logger = LoggerFactory.getLogger(MongoUserDAO.class);
	
	public static final String DATABASE_NAME = "werewolf";
	public static final String COLLECTION_NAME = "games";

	@Autowired private MongoClient mongoClient;

	private DBCollection getCollection(){
		DB db;
		DBCollection coll = null;
		
		if (mongoClient == null){
			logger.error("No mongo instance!");
		}
		
		try {
			db = mongoClient.getDB(DATABASE_NAME);
			coll = db.getCollection(COLLECTION_NAME);
		}
		catch (MongoException ex) {}
		
		return coll;
	}
	
	private Game convertFromObject(DBObject object) {
		Game game = new Game();
		
		ObjectId streamid = (ObjectId) object.get("id");
		
		String id = streamid.toString();
		game.setId(id);
		game.setCreatedDate((Date) object.get("createdDate"));
		game.setDayNightFreq((int) object.get("dayNightFreq"));
		game.setIsRunning((Boolean) object.get("isRunning"));
		game.setTimer((long) object.get("timer"));
		
		return game;
	}
	
	
	//right?
	@Override
	public void createGame(Game game) {
		DBCollection collection = getCollection();
		
		BasicDBObject gameDoc = new BasicDBObject();
		gameDoc.append("id", game.getId());
		gameDoc.append("createdDate", game.getCreatedDate());
		gameDoc.append("isNight", game.getIsNight());
		gameDoc.append("dayNightFreq", game.getDayNightFreq());
		gameDoc.append("isRunning", game.getIsRunning());
		gameDoc.append("timer", game.getTimer());

		collection.insert(gameDoc);	
	}
	
	
	@Override
	public void updateGame(Game game) {
		DBCollection collection = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("id", game.getId());
		
		BasicDBObject gameDoc = new BasicDBObject();

		gameDoc.append("$set", new BasicDBObject().append("dayNightFreq", game.getDayNightFreq()));
		gameDoc.append("$set", new BasicDBObject().append("isRunning", game.getIsRunning()));
		gameDoc.append("$set", new BasicDBObject().append("timer", game.getTimer()));

		collection.update(query, gameDoc);
	}
	
	
	@Override
	public void setGameFinished(Game game) {
		DBCollection collection = getCollection();
		BasicDBObject newDocument = new BasicDBObject();
		
		newDocument.append("$set",  new BasicDBObject().append("isRunning", false));
		
		BasicDBObject searchQuery = new BasicDBObject().append("_id", game.getCreatedDate());
		
		collection.update(searchQuery, newDocument);
		
	}
	
	
	@Override
	public Game getCurrentGame() {
		DBCollection coll = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("isRunning", true);
		DBCursor cursor = coll.find(query);
		
		while(cursor.hasNext()) {
			if (cursor!=null){
				Game g = convertFromObject(cursor.next());
				return g;
			}
		}
		
		return null;
	}
	

	@Override
	public Game getGameByDate(Date date) {
		DBCollection coll = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("createdDate", date);
		DBCursor cursor = coll.find(query);
		
		while(cursor.hasNext()) {
			if (cursor!=null){
				Game g = convertFromObject(cursor.next());
				return g;
			}
		}
		
		return null;
	}
	
	
	@Override
	public List<Game> getAllGames() {
		List<Game> gameList = new ArrayList<>();
		
		DBCollection collection = getCollection();
		DBCursor cursor = collection.find();
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			
			Game g = convertFromObject(obj);		
			gameList.add(g);
		}
		
		return gameList;
	}
	
	
	@Override
	public void dropAllGames() {
		DBCollection games = getCollection();
		games.drop();
	}
}
