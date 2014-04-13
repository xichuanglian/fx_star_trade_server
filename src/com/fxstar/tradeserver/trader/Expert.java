package com.fxstar.tradeserver.trader;

import java.util.List;

import org.apache.log4j.Logger;

import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GOrdersTable;
import com.fxcore2.O2GRow;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTableUpdateType;
import com.fxstar.tradeserver.FollowShip;
import com.fxstar.tradeserver.FollowShipManager;
import com.fxstar.tradeserver.MongoDBWrapper;
import com.fxstar.tradeserver.listener.TableStatusListener;
import com.fxstar.tradeserver.listener.TableUpdateCallback;

public class Expert extends Trader {
	private final Logger logger = Logger.getLogger(Expert.class);
	
	private FollowShipManager fsManager = null;
	
	public Expert(String id, String aid, String account, String password, MongoDBWrapper db, FollowShipManager fsm) {
		super(id, aid, account, password, db);
		fsManager = fsm;
	}
	
	@Override
	public void registerTableListeners() {
		registerOrdersTableListener();		
	}
	
	private void registerOrdersTableListener() {
		O2GOrdersTable ordersTable = (O2GOrdersTable) getTable(O2GTableType.ORDERS);
		TableStatusListener tableListener = new TableStatusListener();
		TableUpdateCallback cb = getOrderDeletedCallback();
		tableListener.setRowDeletedCallback(cb);
		ordersTable.subscribeUpdate(O2GTableUpdateType.DELETE, tableListener);
	}
	
	private TableUpdateCallback getOrderDeletedCallback() {
		final List<FollowShip> followships = fsManager.getFollowShips(this);
		return new TableUpdateCallback() {
			@Override
			public void execute(O2GRow row) {
				O2GOrderTableRow r = (O2GOrderTableRow) row;
				if (r.getStatus().equals("F")) {
					logOrderTableRow(r);
					saveTradeRecord(r);
					for (FollowShip fs : followships) {
						fs.getFollower().parseOrder(r);
					}
				}
			}
		};
	}
}
