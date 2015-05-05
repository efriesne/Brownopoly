package edu.brown.cs.cmen.brownopoly.ownable;

import java.util.Comparator;

/**
 * This comparator is used to sort properties by their position
 */
public class CompareProperty implements Comparator<Property> {

    /**
     * Compares two properties
     * @param property1
     * @param property2
     * @return an int denoting if property1 is located before property2
     */
    public int compare(Property property1, Property property2) {
        return property1.getId() - property2.getId();
    }
}
