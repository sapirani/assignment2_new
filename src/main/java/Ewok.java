package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok
{
    int serialNumber;
    boolean available;


    /**
     * Acquires an Ewok
     */
    public void acquire()
    {
        this.available = false;
    }

    /**
     * release an Ewok
     */
    public void release()
    {
        this.available = true;
    }

    /**
     * check if the Ewok is available
     */
    public boolean isAvailable()
    {
        return available == true;
    }
}