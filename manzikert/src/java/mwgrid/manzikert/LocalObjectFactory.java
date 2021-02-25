package mwgrid.manzikert;

import mwgrid.manzikert.agent.BaggageHandler;
import mwgrid.manzikert.agent.Camel;
import mwgrid.manzikert.agent.Cart;
import mwgrid.manzikert.agent.CavalryOfficer;
import mwgrid.manzikert.agent.CavalrySoldier;
import mwgrid.manzikert.agent.ColumnLeader;
import mwgrid.manzikert.agent.Donkey;
import mwgrid.manzikert.agent.Horse;
import mwgrid.manzikert.agent.Mule;
import mwgrid.manzikert.agent.Officer;
import mwgrid.manzikert.agent.Soldier;
import mwgrid.middleware.distributedobject.Location;

public final class LocalObjectFactory {
    private LocalObjectFactory() {
        // Constructor
    }
    
    public static ColumnLeader createColumnLeader(final Location pLocation, final int pObjectID) {
        return new ColumnLeader(pLocation, pObjectID);
    }

    public static Officer createOfficer(final Location pLocation,
            final int pUnitID, final int pObjectID, final int pSetoffSpacing, final int pColLdr) {
        return new Officer(ClassType.OFFICER, pLocation, pUnitID, pObjectID, pSetoffSpacing, pColLdr, ClassType.OFFICER.getSpeed());
    }
    
    public static BaggageHandler createBaggageHandler(final Location pLocation,
            final int pUnitID, final int pObjectID, final int pSetoffSpacing, final int pColLdr, final double pSpeed) {
        return new BaggageHandler(ClassType.BAGGAGE_HANDLER, pLocation, pUnitID, pObjectID, pSetoffSpacing, pColLdr, pSpeed);
    }
    
    public static Officer createCavalryOfficer(final Location pLocation,
            final int pUnitID, final int pObjectID, final int pSetoffSpacing, final int pColLdr) {
        return new CavalryOfficer(ClassType.CAVALRY_OFFICER, pLocation, pUnitID, pObjectID, pSetoffSpacing, pColLdr);
    }

    public static Soldier createSoldier(final Location pLocation,
            final int pUnitID, final int pObjectID) {
        return new Soldier(ClassType.SOLDIER, pLocation, pUnitID, pObjectID);
    }
    
    public static Soldier createCavalrySoldier(final Location pLocation,
            final int pUnitID, final int pObjectID) {
        return new CavalrySoldier(ClassType.CAVALRY_SOLDIER, pLocation, pUnitID, pObjectID);
    }
    
    public static Mule createMule(final Location pLocation,
            final int pUnitID, final int pObjectID) {
    	return new Mule(pLocation, pUnitID, pObjectID);
    }

    public static Mule createMule(final Location pLocation,
            final int pUnitID, final int pObjectID, final double pSpeed) {
    	return new Mule(pLocation, pUnitID, pObjectID, pSpeed);
    }

    public static Horse createHorse(final Location pLocation,
            final int pUnitID, final int pObjectID) {
    	return new Horse(pLocation, pUnitID, pObjectID);
    }
    
    public static Donkey createDonkey(final Location pLocation,
            final int pUnitID, final int pObjectID) {
    	return new Donkey(pLocation, pUnitID, pObjectID);
    }
    
    public static Camel createCamel(final Location pLocation,
            final int pUnitID, final int pObjectID) {
    	return new Camel(pLocation, pUnitID, pObjectID);
    }
    
    public static Cart createCart(final Location pLocation,
            final int pUnitID, final int pObjectID) {
    	return new Cart(pLocation, pUnitID, pObjectID);
    }
    
}
