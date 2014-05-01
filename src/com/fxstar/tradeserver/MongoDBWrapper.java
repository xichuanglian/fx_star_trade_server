package com.fxstar.tradeserver;

import java.net.UnknownHostException;

import com.fxstar.tradeserver.trader.Expert;
import com.fxstar.tradeserver.trader.Follower;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class MongoDBWrapper {
	private final Logger logger = Logger.getLogger(MongoDBWrapper.class);
	private MongoClient mongoClient = null;
	public DB db = null;
	
	public MongoDBWrapper(String ip, int port, String dbName) throws UnknownHostException {
		mongoClient = new MongoClient( ip, port );
		db = mongoClient.getDB( dbName );
		logger.info("Database connected. " + ip + ":" + port + ":" + dbName);
	}
	
	public HashMap<String, Expert> getExperts(FollowShipManager fsm) {
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_type", "Trader");
		DBCursor cursor = coll.find(query);
		HashMap<String, Expert> experts = new HashMap<String, Expert>();

		try {
			while(cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next();
				BasicDBObject account = (BasicDBObject) obj.get("account");
				String id = obj.getString("_id");
				String accountId = account.getString("_id");
				experts.put(id, new Expert(id,
										   accountId,
										   account.getString("account_number"),
						                   account.getString("password"),
						                   account.getBoolean("real"),
						                   this,
						                   fsm
						                  ));
			}
		} catch (ClassCastException e) {
		    logger.error(e.toString());
		} finally {
			cursor.close();
		}
		
		return experts;
	}
	
	public HashMap<String, Follower> getFollowers() {
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("_type", "Follower");
		DBCursor cursor = coll.find(query);
		HashMap<String, Follower> followers = new HashMap<String, Follower>();

		try {
			while(cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next();
				BasicDBObject account = (BasicDBObject) obj.get("account");
				String id = obj.getString("_id");
				String accountId = account.getString("_id");
				followers.put(id, new Follower(id,
											   accountId,
						               	       account.getString("account_number"),
						               	       account.getString("password"),
						               	       account.getBoolean("real"),
						               	       this
						               	  	  ));
			}
		} catch (ClassCastException e) {
		    logger.error(e.toString());
		} finally {
			cursor.close();
		}
		
		return followers;
	}
	
	/*public void constructFollowships(FollowShipManager fsm, Map<String, Expert> experts, Map<String, Follower> followers) {	
		DBCollection coll = db.getCollection("followships");
		DBCursor cursor = coll.find();
		try {
			while(cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next();
				String eid = obj.getObjectId("trader_id").toString();
				String fid = obj.getObjectId("follower_id").toString();
				fsm.addFollowShip(experts.get(eid), followers.get(fid), obj.getString("_id"));
			}
		} finally {
			cursor.close();
		}
	}*/
	
	public List<FollowShip> getFollowShips(String eid, FollowShipManager fsm) {
		List<FollowShip> ret = new ArrayList<FollowShip>();
		DBCollection coll = db.getCollection("followships");
		BasicDBObject query = new BasicDBObject("trader_id", new ObjectId(eid));
		DBCursor cursor = coll.find(query);
		try {
			while (cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next();
				String fid = obj.getObjectId("follower_id").toString();
				FollowShip fs = fsm.createFollowShip(eid, fid, obj.getString("_id"));
				ret.add(fs);
			} 
		} finally {
			cursor.close();
		}
		return ret;
	}
	
	public void saveTradeRecord(String accountId, String offerId, double price, int amount, String buySell, Calendar timestamp) {
		DBCollection coll = db.getCollection("trade_records");
		BasicDBObject doc = new BasicDBObject("currency", offerId).
                append("price", price).
                append("amount", amount).
                append("operation", buySell).
                append("timestamp", timestamp.getTime()).
                append("account_id", new ObjectId(accountId));
		coll.insert(doc);
	}
	// getDBAccountID(), r.getTradeID(), r.getAccountID(), r.getAmount(), r.getBuySell(), r.getGrossPL(), r.getCommission(), r.getOpenRate(), r.getOpenQuoteID(), r.getOpenTime(), r.getCloseRate(), r.getCloseTime(), r.getValueDate());
	public void saveClosedTradeRecord(String dbAccountId, String tradeId, String FXaccountId, int amount, String buySell, double grossPL, double commission, double openRate, String openQuoteId, Calendar openTime, double closeRate, Calendar closeTime, String valueDate) {
		// TODO
		DBCollection collection = db.getCollection("closed_trade_records");
		BasicDBObject document = new BasicDBObject("tradeId", tradeId).
				append("fx_accountId", FXaccountId).
				append("amount", amount).
				append("buySell", buySell).
				append("grossPL", grossPL).
				append("commission", commission).
				append("openRate", openRate).
				append("openQuoteId", openQuoteId).
				append("openTime", openTime).
				append("closeRate", closeRate).
				append("closeTime", closeTime).
				append("valueDate", valueDate);
		collection.insert(document);
	}
}
