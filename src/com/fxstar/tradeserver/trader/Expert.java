package com.fxstar.tradeserver.trader;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.fxcore2.O2GOrderTableRow;
import com.fxcore2.O2GOrdersTable;
import com.fxcore2.O2GRow;
import com.fxcore2.O2GTableType;
import com.fxcore2.O2GTableUpdateType;
import com.fxstar.tradeserver.listener.TableStatusListener;
import com.fxstar.tradeserver.listener.TableUpdateCallback;

public class Expert extends Trader {
	private final Logger logger = Logger.getLogger(Expert.class);
	
	private ArrayList<Follower> followers = null;
	
	public Expert(String id, String account, String password) {
		super(id, account, password);
		followers = new ArrayList<Follower>();
	}

	public void addFollower(Follower follower) {
		followers.add(follower);
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
		return new TableUpdateCallback() {
			@Override
			public void execute(O2GRow row) {
				O2GOrderTableRow r = (O2GOrderTableRow) row;
				if (r.getStatus().equals("F")) {
					logOrderTableRow(r);
					for (Follower follower : followers) {
						follower.parseOrder(r);
					}
				}
			}
		};
	}
}
