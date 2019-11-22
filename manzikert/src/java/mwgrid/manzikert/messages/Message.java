package mwgrid.manzikert.messages;

import mwgrid.middleware.distributedobject.Location;

public class Message {

	public enum MessageType {
		NULL_MESSAGE,
		GO_TO_CAMP,
		GO_TO_POINT,
		FOLLOW;
	}
	
	private final MessageType fMsgType;
	private final Location fMsgLocation;
	public final boolean fResting;
	
	public Message(final MessageType pMsgType, final Location pMsgLocation, final boolean pResting) {
		fMsgType = pMsgType;
		fMsgLocation = pMsgLocation;
		fResting = pResting;
	}
	
	public Message(final Message pMsg){
		fMsgType = pMsg.fMsgType;
		fMsgLocation = pMsg.fMsgLocation;
		fResting = pMsg.fResting;
	}
	
	public MessageType getMsgType(){
		return fMsgType;
	}
	
	public Location getMsgLocation(){
		return fMsgLocation;
	}
}
