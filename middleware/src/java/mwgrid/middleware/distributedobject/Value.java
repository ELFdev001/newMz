package mwgrid.middleware.distributedobject;


public class Value<T> {
    private final T fValue;
    
    /**
     * @param pValue
     *            - value
     */
    @SuppressWarnings("unchecked")
    public Value(final T pValue) {
        this.fValue = pValue;
    }
    
    /**
     * @return (Class<?>) fType
     */
    public Class<?> getType() {
        return this.fValue.getClass();
    }
    
    /**
     * @return (T) fValue
     */
    @SuppressWarnings("unchecked")
    public T get() {
        return this.fValue;
    }
    
    /**
     * @see java.lang.Object#toString()
     * @return (String) string representation of Value
     */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("Value: { ");
        result.append("Value: " + this.fValue);
        result.append(" }");
        return result.toString();
    }
}
