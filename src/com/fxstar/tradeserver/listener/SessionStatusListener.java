package com.fxstar.tradeserver.listener;

import org.apache.log4j.Logger;

import com.fxcore2.IO2GSessionStatus;
import com.fxcore2.O2GSession;
import com.fxcore2.O2GSessionDescriptor;
import com.fxcore2.O2GSessionDescriptorCollection;
import com.fxcore2.O2GSessionStatusCode;

public class SessionStatusListener implements IO2GSessionStatus {

	private boolean mConnected = false;
    private boolean mDisconnected = false;
    private boolean mError = false;
    private String mDBName = "";
    private String mPin = "";
    private O2GSession mSession = null;
    private O2GSessionStatusCode mStatus = null;
    private Callback cbOnConnected;
    
    private final Logger logger = Logger.getLogger(SessionStatusListener.class);
    
    public SessionStatusListener(O2GSession session, String DBName, String pin, Callback cb) {
    	mSession = session;
    	mDBName = DBName;
    	mPin = pin;
    	cbOnConnected = cb;
    }
    
    public boolean isConnected() {
    	return mConnected;
    }
    
    public boolean isDisconnected() {
    	return mDisconnected;
    }
    
    public boolean hasError() {
    	return mError;
    }
    
    public O2GSessionStatusCode getStatus() {
    	return mStatus;
    }

	@Override
	public void onLoginFailed(String error) {
		mError = true;
		logger.error(error);
	}

	@Override
	public void onSessionStatusChanged(O2GSessionStatusCode status) {
		mStatus = status;
        logger.info("Session status: " + mStatus.toString());
        
        if (mStatus == O2GSessionStatusCode.CONNECTED) {
            mConnected = true;
            cbOnConnected.execute();
        } else {
            mConnected = false;
        }
        
        if (status == O2GSessionStatusCode.DISCONNECTED) {
            mDisconnected = true;
        } else {
            mDisconnected = false;
        }
        
        if (mStatus == O2GSessionStatusCode.TRADING_SESSION_REQUESTED) {
            /*O2GSessionDescriptorCollection descs = mSession.getTradingSessionDescriptors();
            logger.info("Session descriptors:");
            for (O2GSessionDescriptor desc : descs) {
                logger.info("id:" + desc.getId() + " name:" + desc.getName() +
                		    " desc:" + desc.getDescription() + " pinRequired:" + desc.isPinRequired());
            }
            if (mDBName.equals("")) {
                logger.error("Database name is missing");
            }
            else {
                mSession.setTradingSession(mDBName, mPin);
            }*/
        }
	}

}
