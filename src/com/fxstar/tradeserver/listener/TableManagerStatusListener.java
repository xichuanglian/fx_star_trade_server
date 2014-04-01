package com.fxstar.tradeserver.listener;

import org.apache.log4j.Logger;

import com.fxcore2.IO2GTableManagerListener;
import com.fxcore2.O2GTableManager;
import com.fxcore2.O2GTableManagerStatus;

public class TableManagerStatusListener implements IO2GTableManagerListener {
	private Callback cbOnTableLoaded;
	
	private final Logger logger = Logger.getLogger(TableManagerStatusListener.class);
	
	public TableManagerStatusListener(Callback cb) {
		cbOnTableLoaded = cb;
	}
	
	@Override
	public void onStatusChanged(O2GTableManagerStatus status, O2GTableManager manager) {
		switch(status) {
			case TABLES_LOADED:
				logger.info("Trading tables loaded.");
				cbOnTableLoaded.execute();
				break;
			case TABLES_LOAD_FAILED:
				logger.error("Trading tables load failed.");
				break;
			case TABLES_LOADING:
				break;
		}
	}

}
