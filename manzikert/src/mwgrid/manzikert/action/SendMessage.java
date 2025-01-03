package mwgrid.manzikert.action;

import mwgrid.manzikert.agent.MWGridAgent;
import mwgrid.manzikert.agent.Officer;
import mwgrid.manzikert.messages.Message;

public class SendMessage extends Action{

	private final Message fMessage;
	private final MWGridAgent fRecipient;
	
	public SendMessage(final MWGridAgent pRecipient, final Message pMessage) {
		this.fRecipient = pRecipient;
		this.fMessage = pMessage;
	}
	
	public SendMessage(final SendMessage pMessage) {
		this.fRecipient = pMessage.fRecipient;
		this.fMessage = pMessage.fMessage;
	}

	@Override
	public double getCost() {
		return 0;
	}

	@Override
	public Action copy() {
		return new SendMessage(this);
	}

	@Override
	public boolean performAction(MWGridAgent pAgent) {
		Officer thisoff = (Officer) pAgent;
		if (thisoff.fColumnLeader > 0) {
			thisoff.fResting = fMessage.fResting;
		}
		this.fRecipient.receiveMessage(fMessage);
		return true;
	}

}
