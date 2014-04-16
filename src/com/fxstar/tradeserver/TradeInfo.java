package com.fxstar.tradeserver;

import java.net.UnknownHostException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.fxstar.tradeserver.listener.Callback;
import com.fxstar.tradeserver.trader.Expert;
import com.fxstar.tradeserver.trader.Follower;
import com.fxstar.tradeserver.trader.Trader;

public class TradeInfo{
final Logger logger = Logger.getLogger(TradeInfo.class);
	
	MongoDBWrapper dbWrapper = null;
	private Map<String, Expert> experts = null;
	private Map<String, Follower> followers = null;
	FollowShipManager followShipManager = null;
	public void start(String dbip, int dbport, String dbname) {
		try {
			dbWrapper = new MongoDBWrapper(dbip, dbport, dbname);
			followShipManager = new FollowShipManager(dbWrapper);
			experts = dbWrapper.getExperts(followShipManager);
			followers = dbWrapper.getFollowers();
			dbWrapper.constructFollowships(followShipManager, experts, followers);
			
			//Expert me = experts.get("534aaeaf4159317576030000");
			Expert me = experts.get("534cf07a4159317584020000");
			logger.info(me.getDBAccountID());
			
			Callback loginCb = getLoginCb(me);
			Callback tableLoadedCb = getRegisterTableListenersCb(me);
			me.login("http://www.fxcorporate.com/Hosts.jsp", "Demo", null, null, loginCb, tableLoadedCb);
			

			
			
		} catch (UnknownHostException e){
			logger.error(e.toString());
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
				trader.registerClosedTradeListener(); //get Account History
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
		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.start(args[1], Integer.parseInt(args[2]), args[3]);
		while(true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

}
