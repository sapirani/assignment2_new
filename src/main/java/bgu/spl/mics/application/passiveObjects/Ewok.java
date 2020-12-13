package bgu.spl.mics.application.passiveObjects; // The package

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok
{
    private int serialNumber;
    private boolean available;

    /**
     * Constructor
     */
    public Ewok(int serialNumber)
    {
        this.serialNumber = serialNumber;
        this.available = true;
    }

    /**
     * Try to catch an Ewok if its possible.
     * If it's not possible, wait until someone will release the Ewok.
     * Acquires an Ewok
     */
    public synchronized void acquire()
    {
        while (!isAvailable()) // While the Ewok is acquired already by some other thread
        {
            try
            {
                this.wait(); // Wait until the other thread release it.
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
        this.available = false; // acquire the Ewok
    }

    /**
     * release an Ewok and notify to other threads that the Ewok is available.
     */
    public synchronized void release()
    {
        this.available = true;
        this.notifyAll();
    }

    /**
     * check if the Ewok is available
     */
    public boolean isAvailable()
    {
        return available;
    }

    /**
     * get the Ewok's serial number
     */
    public int getSerialNumber()
    {
        return serialNumber;
    }
}