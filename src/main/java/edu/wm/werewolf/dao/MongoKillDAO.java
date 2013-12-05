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
import com.mongodb.MongoException;

import edu.wm.werewolf.model.Kill;

public class MongoKillDAO implements IKillDAO {
	
	private final Logger logger = LoggerFactory.getLogger(MongoKillDAO.class);
	
	public static final String DATABASE_NAME = "werewolf";
	public static final String COLLECTION_NAME = "kills";

	@Autowired private DB db;

	private DBCollection getCollection(){
		DBCollection coll = null;
		
		if (db == null){
			logger.error("No mongo instance!");
		}
		
		try {
			coll = db.getCollection(COLLECTION_NAME);
		}
		catch (MongoException ex) {}
		
		return coll;
	}
	
	private Kill convertFromObject(DBObject object) {
		Kill kill = new Kill();
		
		kill.setKillerId((String) object.get("killerId"));
		kill.setVictimId((String) object.get("victimId"));
		kill.setTimestamp((Date) object.get("timestamp"));
		kill.setLat((double) object.get("lat"));
		kill.setLng((double) object.get("lng"));
		
		return kill;
	}
	
	@Override
	public Kill getKillByKillerId(String killerId) {
		DBCollection collection = getCollection();
		
		BasicDBObject query = new BasicDBObject();
		query.put("killerId", new ObjectId(killerId));
		
		DBObject object = collection.findOne(query);
		
		Kill kill = convertFromObject(object);
		
		return kill;
	}	
	
	@Override
	public void createKill(Kill kill) {
		DBCollection collection = getCollection();
		
		// Create Player Object for User
		BasicDBObject killDoc = new BasicDBObject();
		killDoc.append("killerId", kill.getKillerId());
		killDoc.append("victimId", kill.getVictimId());
		killDoc.append("timestamp", kill.getTimestamp());
		killDoc.append("lat", kill.getLat());
		killDoc.append("lng", kill.getLng());
		
		collection.insert(killDoc);
	}
	
	@Override
	public List<Kill> getAllKills() {
		List<Kill> kills = new ArrayList<>();
		
		DBCollection collection = getCollection();
		DBCursor cursor = collection.find();
		
		while(cursor.hasNext()){	
			DBObject obj = cursor.next();
			
			Kill kill = convertFromObject(obj);
			kills.add(kill);
		}
		
		return kills;
	}
	
	
	@Override
	public void dropAllKills() {
		DBCollection kills = getCollection();
		kills.drop();
	}

}
