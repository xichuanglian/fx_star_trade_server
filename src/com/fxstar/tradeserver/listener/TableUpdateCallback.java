package com.fxstar.tradeserver.listener;

import com.fxcore2.O2GRow;

public interface TableUpdateCallback {
	void execute(O2GRow row);
}