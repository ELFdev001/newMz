package mwgrid.manzikert;

import java.util.ArrayList;
import java.util.List;

import mwgrid.manzikert.agent.MWGridAgent;
import mwgrid.manzikert.messages.Message;
import mwgrid.manzikert.messages.Message.MessageType;
import mwgrid.manzikert.planning.MacroRoutePlan;
import mwgrid.middleware.distributedobject.Location;

public final class NullHandling {
    public static final MacroRoutePlan NULL_MACRO_ROUTE_PLAN = new MacroRoutePlan(Location.NULL_LOCATION, 0);
    public static final List<MWGridAgent> NULL_SUBORDINATES = new ArrayList<MWGridAgent>();
    public static final long NULL_OBJECT = -1;
    public static final String SEPARATOR = "#";
    public static final String NULL_UNITIDSTRING = "XXX";
    public static final Message NULL_MESSAGE = new Message(MessageType.NULL_MESSAGE, Location.NULL_LOCATION, false);

    
    /**
     * Constructor
     */
    private NullHandling() {
        // do nothing
    }
    
}
