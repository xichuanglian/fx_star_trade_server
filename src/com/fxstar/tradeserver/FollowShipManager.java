package com.fxstar.tradeserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fxstar.tradeserver.trader.Expert;
import com.fxstar.tradeserver.trader.Follower;

public class FollowShipManager {
	
	private MongoDBWrapper dbWrapper;
	//private Map<Expert, List<FollowShip>> followShipsOfExpert = null;
	//private Map<String, FollowShip> followShips = null;
	private Map<String, Follower> followers = null;
	private Map<String, Expert> experts = null;
	
	public FollowShipManager(MongoDBWrapper db) {
		dbWrapper = db;
		//followShipsOfExpert = new HashMap<Expert, List<FollowShip>>();
	}
	
	public void setFollowers(Map<String, Follower> followers) {
		this.followers = followers;
	}
	
	public void setExperts(Map<String, Expert> experts) {
		this.experts = experts;
	}
	
	public FollowShip createFollowShip(String eid, String fid, String id) {
		return new FollowShip(experts.get(eid), followers.get(fid), id);
	}
	
	/*public void addFollowShip(Expert e, Follower f, String fsId) {
		if (!followShipsOfExpert.containsKey(e)) {
			followShipsOfExpert.put(e, new ArrayList<FollowShip>());
		}
		FollowShip fs = new FollowShip(e, f, fsId);
		followShipsOfExpert.get(e).add(fs);
		followShips.put(fsId, fs);
	}*/
	
	public List<FollowShip> getFollowShips(Expert expert) {
		//return followShipsOfExpert.get(expert);
		return dbWrapper.getFollowShips(expert.getObjectID(), this);		
	}
	
	public void updateFollowShip(String fsId) {
		
	}
	
	/*public void removeFollowShip(String fsId) {
		FollowShip fs = followShips.remove(fsId);
		followShipsOfExpert.get(fs.getExpert()).remove(fs);
	}*/

}
