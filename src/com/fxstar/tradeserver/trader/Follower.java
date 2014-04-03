package com.fxstar.tradeserver.trader;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.fxcore2.Constants;
import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GOrdersTable;
import com.fxcore2.O2GRequestParamsEnum;
import com.fxcore2.O2GRow;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTableUpdateType;
import com.fxcore2.O2GValueMap;
import com.fxstar.tradeserver.MongoDBWrapper;
import com.fxstar.tradeserver.listener.TableStatusListener;
import com.fxstar.tradeserver.listener.TableUpdateCallback;

public class Follower extends Trader{
	private final Logger logger = Logger.getLogger(Follower.class);
	// Expert trade id -> Follower trade id
	private HashMap<String, String> tradeIdMap = null;
	private final String CUSTOM_ID = "FX_STAR_AUTO_TRADE";
	
	public Follower(String id, String aid, String account, String password, MongoDBWrapper db) {
		super(id, aid, account, password, db);
		tradeIdMap = new HashMap<String, String>();
	}

	@Override
	public void registerTableListeners() {
		registerOrderTableInsertedListener();
	}
	
	public void parseOrder(O2GOrderTableRow row) {
		if (row.getStage().equals("O")) {
			parseOpenOrder(row);
		} else {
			parseCloseOrder(row);
		}
	}
	
	private void parseOpenOrder(O2GOrderTableRow row) {
		O2GValueMap valuemap = createValueMap();
		valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.CreateOrder);
		valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.TrueMarketOpen);
		valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, getAccountID());
		valuemap.setString(O2GRequestParamsEnum.OFFER_ID, row.getOfferID());
		if (row.getBuySell().equals("B")) {
			valuemap.setString(O2GRequestParamsEnum.BUY_SELL, Constants.Buy);
		} else {
			valuemap.setString(O2GRequestParamsEnum.BUY_SELL, Constants.Sell);
		}
		valuemap.setInt(O2GRequestParamsEnum.AMOUNT, row.getOriginAmount());
		valuemap.setString(O2GRequestParamsEnum.TIME_IN_FORCE, Constants.TIF.FOK);
		valuemap.setString(O2GRequestParamsEnum.CUSTOM_ID, CUSTOM_ID + row.getTradeID());
		createAndSendOrderRequest(valuemap);
	}
	
	private void parseCloseOrder(O2GOrderTableRow row) {
		String tradeId = null;
		if (tradeIdMap.containsKey(row.getTradeID())) {
			tradeId = tradeIdMap.get(row.getTradeID());
		} else {
			return;
		}
		O2GValueMap valuemap = createValueMap();
	    valuemap.setString(O2GRequestParamsEnum.COMMAND, Constants.Commands.CreateOrder);
	    valuemap.setString(O2GRequestParamsEnum.ORDER_TYPE, Constants.Orders.TrueMarketClose);	 
	    valuemap.setString(O2GRequestParamsEnum.ACCOUNT_ID, getAccountID());
	    valuemap.setString(O2GRequestParamsEnum.OFFER_ID, row.getOfferID());
	    valuemap.setString(O2GRequestParamsEnum.TRADE_ID, tradeId);
	    valuemap.setInt(O2GRequestParamsEnum.AMOUNT, row.getOriginAmount());
	    if (row.getBuySell().equals("B")) {
			valuemap.setString(O2GRequestParamsEnum.BUY_SELL, Constants.Buy);
		} else {
			valuemap.setString(O2GRequestParamsEnum.BUY_SELL, Constants.Sell);
		}
	    valuemap.setString(O2GRequestParamsEnum.TIME_IN_FORCE, Constants.TIF.GTC);
		createAndSendOrderRequest(valuemap);
	}
	
	private void registerOrderTableInsertedListener() {
		O2GOrdersTable ordersTable = (O2GOrdersTable) getTable(O2GTableType.ORDERS);
		TableStatusListener tableListener = new TableStatusListener();
		TableUpdateCallback cb = getOrderDeletedCallback();
		tableListener.setRowDeletedCallback(cb);
		ordersTable.subscribeUpdate(O2GTableUpdateType.DELETE, tableListener);
	}
	
	private TableUpdateCallback getOrderDeletedCallback() {
		return new TableUpdateCallback() {
			@Override
			public void execute(O2GRow row) {
				O2GOrderTableRow r = (O2GOrderTableRow) row;			
				if (r.getStatus().equals("F")) {
					logOrderTableRow(r);
					saveTradeRecord(r);
					if (r.getRequestTXT().startsWith(CUSTOM_ID)) {
						String id = r.getRequestTXT().substring(CUSTOM_ID.length());
						tradeIdMap.put(id, r.getTradeID());
					}
				}
			}			
		};
	}
}
