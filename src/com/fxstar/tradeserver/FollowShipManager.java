package com.fxstar.tradeserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fxstar.tradeserver.trader.Expert;
import com.fxstar.tradeserver.trader.Follower;

public class FollowShipManager {
	
	private MongoDBWrapper dbWrapper;
	private Map<Expert, List<FollowShip>> followShipsOfExpert = null;
	private Map<String, FollowShip> followShips = null;
	
	public FollowShipManager(MongoDBWrapper db) {
		dbWrapper = db;
		followShipsOfExpert = new HashMap<Expert, List<FollowShip>>();
	}
	
	public void addFollowShip(Expert e, Follower f, String fsId) {
		if (!followShipsOfExpert.containsKey(e)) {
			followShipsOfExpert.put(e, new ArrayList<FollowShip>());
		}
		FollowShip fs = new FollowShip(e, f, fsId);
		followShipsOfExpert.get(e).add(fs);
		followShips.put(fsId, fs);
	}
	
	public List<FollowShip> getFollowShips(Expert expert) {
		return followShipsOfExpert.get(expert);
	}
	
	public void updateFollowShip(String fsId) {
		
	}
	
	public void removeFollowShip(String fsId) {
		FollowShip fs = followShips.remove(fsId);
		followShipsOfExpert.get(fs.getExpert()).remove(fs);
	}

}
