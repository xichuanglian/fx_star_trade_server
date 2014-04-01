package com.fxstar.tradeserver;

import java.net.UnknownHostException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.fxstar.tradeserver.listener.Callback;
import com.fxstar.tradeserver.trader.Expert;
import com.fxstar.tradeserver.trader.Follower;
import com.fxstar.tradeserver.trader.Trader;

public class TradeServer {
	final Logger logger = Logger.getLogger(TradeServer.class);
	
	private Map<String, Follower> followers = null;
	private Map<String, Expert> experts = null;
	//MongoDBWrapper dbWrapper = null;
	DemoWrapper dbWrapper = null;
	
	public void start() {
		try {
			//dbWrapper = new MongoDBWrapper("192.168.56.102", 27017, "fx_star_development");
			dbWrapper = new DemoWrapper();
			experts = dbWrapper.getExperts();
			followers = dbWrapper.getFollowers();
			dbWrapper.constructFollowships(experts, followers);
			
			for (Expert expert : experts.values()) {
				Callback loginCb = getLoginCb(expert);
				Callback tableLoadedCb = getRegisterTableListenersCb(expert);
				expert.login("http://www.fxcorporate.com/Hosts.jsp", "Demo", null, null, loginCb, tableLoadedCb);
			}
			
			for (Follower follower : followers.values()) {
				Callback loginCb = getLoginCb(follower);
				Callback tableLoadedCb = getRegisterTableListenersCb(follower);
				follower.login("http://www.fxcorporate.com/Hosts.jsp", "Demo", null, null, loginCb, tableLoadedCb);
			}
		//} catch (UnknownHostException e){
		//	logger.error(e.toString());
		} finally {
			logger.info("Server started.");
		}
	}	
	
	private Callback getRegisterTableListenersCb(final Trader trader) {
		return new Callback() {
			@Override
			public void execute() {
				logger.info("Registering table listeners for trader: " + trader.getObjectID());
				trader.prepare();
				trader.registerTableListeners();
			}
		};
	}
	
	private Callback getLoginCb(final Trader trader) {
		return new Callback() {
			@Override
			public void execute() {
				logger.info("Trader: " + trader.getObjectID() +" login succeeded.");
			}
		};
	}
	
	public static void main(String[] args) {
		DOMConfigurator.configure(args[0]);
		TradeServer tradeServer = new TradeServer();
		tradeServer.start();
		while(true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

}
