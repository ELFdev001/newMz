package mwgrid.manzikert;

//Speed in metres per tick! To get mph then multiply by 3 and divide by 5.
public enum ClassType {
    NO_CLASS (0, 0, 0, 0, 0, 0), 
    COLUMN_LEADER (20, 4, 70, 0, 45, 0), 
    OFFICER (5, 1, 70, 36, 10, 0), 
    SOLDIER (5, 1, 70, 36, 10, 0), 
    CAVALRY_OFFICER (20, 4, 70, 59, 20, 0), 
    CAVALRY_SOLDIER (20, 4, 70, 59, 20, 0), 
    MULE (4.16, 3, 0, 91, 20, 0), 
    HORSE (5, 4, 0, 91, 20, 0), 
    CAMEL (3.33, 5, 0, 159, 22, 0), 
    DONKEY (3.33, 3, 0, 45, 18, 0), 
    CART (4.16, 19, 0, 386, 0, 0),
    BAGGAGE_HANDLER (5, 1, 70, 36, 10, 0);

    private final double speed;
    private final int size;
    private final int weight;
    private final int carryingCapacity;
    private final int equipWeight;
    private final int caloriesProvided;
    
    ClassType(double speed, int size, int weight, int carryingCapacity, int equipweight, int caloriesProvided) {
    	this.speed = speed;
    	this.size = size;
    	this.weight = weight;
    	this.carryingCapacity = carryingCapacity;
    	this.equipWeight = equipweight;
    	this.caloriesProvided = caloriesProvided;
    }
    
    public int getClassTypeId() {
        return this.ordinal();
    }
    
    public int getSize() {
    	return this.size;
    }
    
    public int getWeight() {
    	return this.weight;
    }
    
    public double getSpeed() {
    	return this.speed;
    }
    
    public int getCarryingCapacity() {
    	return this.carryingCapacity;
    }
    
    public int getEquipWeight() {
    	return this.equipWeight;
    }
    
    public int getCaloriesProvided() {
    	return this.caloriesProvided;
    }
    
    /**
     * @param pClassTypeId
     *            - class type ID
     * @return (ClassType) class type
     */
    public static final ClassType getClassType(final int pClassTypeId) {
        for (final ClassType classType : ClassType.values())
            if (classType.getClassTypeId() == pClassTypeId)
                return classType;
        return NO_CLASS;
    }
}
