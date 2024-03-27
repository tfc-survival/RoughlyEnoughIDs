package org.dimdev.jeid.debug;

/**
 * Interface to help register an arbitrary number of instances of a class
 *
 * @author jchung01
 */
public interface IDebugClass {
    /**
     * @return number of instances of this class to make and register
     */
    int getNumInstances();

    /**
     * Makes and registers an instance of the class.
     *
     * @param id id to append to the instance's name
     */
    void makeInstance(int id);

    /**
     * @return true if instances of this class should be made for debugging,
     * false otherwise.
     */
    boolean shouldDebug();
}
