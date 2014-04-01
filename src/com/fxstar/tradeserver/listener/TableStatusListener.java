package com.fxstar.tradeserver.listener;

import org.apache.log4j.Logger;

import com.fxcore2.IO2GTableListener;
import com.fxcore2.O2GRow;
import com.fxcore2.O2GTableStatus;

public class TableStatusListener implements IO2GTableListener {
	private TableUpdateCallback rowAddedCb = null;
	private TableUpdateCallback rowChangedCb = null;
	private TableUpdateCallback rowDeletedCb = null;
	private Callback tableRefreshedCb = null;
	
	private Logger logger = Logger.getLogger(TableStatusListener.class);
	
	public void setRowAddedCallback(TableUpdateCallback cb) {
		rowAddedCb = cb;
	}
	
	public void setRowChangedCallback(TableUpdateCallback cb) {
		rowChangedCb = cb;
	}
	
	public void setRowDeletedCallback(TableUpdateCallback cb) {
		rowDeletedCb = cb;
	}
	
	public void setTableRefreshedCallback(Callback cb) {
		tableRefreshedCb = cb;
	}

	@Override
	public void onAdded(String rowID, O2GRow row) {
		if (rowAddedCb != null) {
			rowAddedCb.execute(row);
		} else {
			logger.warn("Missing callback for row added event.");
		}
	}

	@Override
	public void onChanged(String rowID, O2GRow row) {
		if (rowChangedCb != null) {
			rowChangedCb.execute(row);
		} else {
			logger.warn("Missing callback for row changed event.");
		}
	}

	@Override
	public void onDeleted(String rowID, O2GRow row) {
		if (rowDeletedCb != null) {
			rowDeletedCb.execute(row);
		} else {
			logger.warn("Missing callback for row deleted event.");
		}
	}

	@Override
	public void onStatusChanged(O2GTableStatus status) {
		switch(status) {
			case FAILED:
				logger.error("Error occured with table.");
				break;
			case INITIAL:
				logger.info("Table initializing...");
				break;
			case REFRESHING:
				logger.info("Table refreshing...");
				break;
			case REFRESHED:
				logger.info("Table refreshed.");
				if (tableRefreshedCb != null) {
					tableRefreshedCb.execute();
				}
				break;
		}
	}

}
