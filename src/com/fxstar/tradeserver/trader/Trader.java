package com.fxstar.tradeserver.trader;

import org.apache.log4j.Logger;

import com.fxcore2.O2GAccountRow;
import com.fxcore2.O2GAccountsTableResponseReader;
import com.fxcore2.O2GLoginRules;
import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GRequest;
import com.fxcore2.O2GRequestFactory;
import com.fxcore2.O2GResponse;
import com.fxcore2.O2GResponseReaderFactory;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GSessionStatusCode;
import com.fxcore2.O2GTable;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableManagerMode;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTransport;
import com.fxcore2.O2GValueMap;
import com.fxstar.tradeserver.MongoDBWrapper;
import com.fxstar.tradeserver.listener.Callback;
import com.fxstar.tradeserver.listener.SessionStatusListener;
import com.fxstar.tradeserver.listener.TableManagerStatusListener;

public abstract class Trader {
	private String objectID = "";
	private String dbAccountID = "";
	private String accountID = "";
	private String fAccount = "";
	private String fPassword = "";
	private O2GSession fSession = null;
	private O2GTableManager tableManager = null;
	private O2GRequestFactory rFactory = null;
	private SessionStatusListener statusListener = null;
	private TableManagerStatusListener tableManagerListener = null;
	private MongoDBWrapper dbWrapper = null;
	
	private final Logger logger = Logger.getLogger(Trader.class);
	
	protected Trader(String id, String aid, String account, String password, MongoDBWrapper db) {
		objectID = id;
		dbAccountID = aid;
		dbWrapper = db;
		if (account != null) {
			fAccount = account;
		} else {
			fAccount = "";
		}
		if (password != null) {
			fPassword = password;
		} else {
			fPassword = "";
		}
	}
	
	public String getObjectID() {
		return objectID;
	}
	
	public String getDBAccountID() {
		return dbAccountID;
	}
	
	public O2GSessionStatusCode getStatus() {
		return statusListener.getStatus();
	}
	
	public boolean isConnected() {
		return statusListener.isConnected();
	}
	
	public boolean isDisconnected() {
		return statusListener.isDisconnected();
	}
	
	public boolean hasError() {
		return statusListener.hasError();
	}
	
	public void login(String url, String conName, String DBName, String pin, Callback loginCb, Callback tableLoadedCb) {
		if (statusListener != null) {
			logger.debug("Trying to login again, abort!");
			return;
		}
		fSession = O2GTransport.createSession();
		tableManagerListener = new TableManagerStatusListener(tableLoadedCb);
		fSession.useTableManager(O2GTableManagerMode.YES, tableManagerListener);
		statusListener = new SessionStatusListener(fSession, DBName, pin, loginCb);
		fSession.subscribeSessionStatus(statusListener);
		fSession.login(fAccount, fPassword, url, conName);
	}
	
	public void logout() {
		fSession.logout();
		fSession.unsubscribeSessionStatus(statusListener);
		fSession.dispose();
	}
	
	public abstract void registerTableListeners();
	
	public void prepare() {
		tableManager = fSession.getTableManager();
		rFactory = fSession.getRequestFactory();
		O2GLoginRules loginRules = fSession.getLoginRules();
        // get first account from login
        O2GResponse accountResponse = loginRules.getTableRefreshResponse(O2GTableType.ACCOUNTS);
        O2GResponseReaderFactory factory = fSession.getResponseReaderFactory();
        O2GAccountsTableResponseReader accountsReader = factory.createAccountsTableReader(accountResponse);
        O2GAccountRow account = accountsReader.getRow(0);
        // get account id
        accountID = account.getAccountID();
	}
	
	protected String getAccountID() {
		return accountID;
	}
	
	protected O2GTable getTable(O2GTableType type) {	
		return tableManager.getTable(type);
	}
	
	protected O2GValueMap createValueMap() {
		return rFactory.createValueMap();
	}
	
	protected void createAndSendOrderRequest(O2GValueMap valuemap) {
		O2GRequest request = rFactory.createOrderRequest(valuemap);
		fSession.sendRequest(request);
	}
	
	protected void logOrderTableRow(O2GOrderTableRow row) {
		logger.info("Trader: " + getObjectID() +
					" OrderID: " + row.getOrderID() +
					" RequestID: " + row.getRequestID() +
					" OfferID: " + row.getOfferID() +
					" Open/Close: " + row.getStage() +
					" TradeID: " + row.getTradeID() +
					" BuySell= " + row.getBuySell() +
					" Rate= " + row.getRate() + 
					" Amount= " + row.getOriginAmount()
				   );
	}
	
	protected void saveTradeRecord(O2GOrderTableRow row) {
		dbWrapper.saveTradeRecord(getDBAccountID(), row.getOfferID(), row.getRate(), row.getOriginAmount(), row.getBuySell(), row.getStatusTime());
	}
}
