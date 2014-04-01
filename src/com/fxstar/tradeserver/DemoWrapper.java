package com.fxstar.tradeserver;

import java.util.HashMap;
import java.util.Map;

import com.fxstar.tradeserver.trader.Expert;
import com.fxstar.tradeserver.trader.Follower;

public class DemoWrapper{
	public HashMap<String, Expert> getExperts() {
		HashMap<String, Expert> experts = new HashMap<String, Expert>();
		experts.put("demo_expert", new Expert("demo_expert", "1993368", "sa6dngp"));
		return experts;
	}
	
	public HashMap<String, Follower> getFollowers() {
		HashMap<String, Follower> followers = new HashMap<String, Follower>();
		followers.put("demo_follower", new Follower("demo_follower", "1993369", "cch2len"));
		return followers;
	}
	
	public void constructFollowships(Map<String, Expert> experts, Map<String, Follower> followers) {
		for (Expert e : experts.values()) {
			for (Follower f : followers.values()) {
				e.addFollower(f);
			}
		}
	}
}
