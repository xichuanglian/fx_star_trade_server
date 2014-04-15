package com.fxstar.tradeserver;

import com.fxstar.tradeserver.trader.Expert;
import com.fxstar.tradeserver.trader.Follower;

public class FollowShip {
	
	private String id = null;
	private Expert expert = null;
	private Follower follower = null;
	
	public FollowShip(Expert expert, Follower follower, String id) {
		this.expert = expert;
		this.follower = follower;
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public Expert getExpert() {
		return expert;
	}
	
	public Follower getFollower() {
		return follower;
	}

}
