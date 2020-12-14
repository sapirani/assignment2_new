package bgu.spl.mics.application.passiveObjects; // The package

// Imports:
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks
{
    private List<Ewok> ewoks;

    /**
     * Implementation of thread safe singleton (as we learned in class)
     */
    private static class EwoksHolder
    {
        private static Ewoks instance = new Ewoks();
    }

    /**
     * The function gives access to the singleton instance
     * @return instance of the Ewoks
     */
    public static Ewoks getInstance()
    {
        return EwoksHolder.instance;
    }

    /**
     * The Class's Constructor
     */
    private Ewoks(){}

    /**
     * Insert Ewoks to the ewoks list.
     * Input:
     *      @param numOfEwoks is the number of Ewoks to insert.
     * Output:
     *      none
     */
    public void loadEwoks(int numOfEwoks)
    {
        this.ewoks = new ArrayList<Ewok>();
        this.ewoks.add(0, null); // We will not pay attention to the 0 index in the list

        // Ignore index 0 - serial number starts from 1 to numOfEwoks
        for(int i = 1; i <= numOfEwoks ;i++)
        {
            Ewok e = new Ewok(i);
            ewoks.add(i, e);
        }
    }

    /**
     * The thread tries to acquire the Ewoks he needs for the attack.
     * If one of the Ewoks is not available, he will wait until other thread will release it.
     * Input:
     *      @param serialNumbers is list of all the serial numbers of the Ewoks that the thread needs to acquire.
     * Output:
     *      none
     */
    public void acquireEwoks(List<Integer> serialNumbers)
    {
        Collections.sort(serialNumbers); // Sort the list of serial number
                                         // Implementation of Resource ordering, to prevent Deadlock
        for (int ewok : serialNumbers)
        {
            this.ewoks.get(ewok).acquire(); // Try to acquire the i'th Ewok
        }
    }

    /**
     * The thread releases all the Ewoks that he acquired for the attack.
     * It notifies all the other threads that those Ewoks are available now.
     * Input:
     *      @param serialNumbers - list of the needed Ewoks's serial numbers
     * Output:
     *       none
     */
    public void releaseEwoks(List<Integer> serialNumbers)
    {
        Collections.sort(serialNumbers, Collections.reverseOrder()); // Release the ewoks in the reversed order
                                                                     // As learned in class in the Philosopher's problem and solution
        for (int ewok : serialNumbers)
        {
            this.ewoks.get(ewok).release(); // Release the i'th Ewok
        }
    }
}