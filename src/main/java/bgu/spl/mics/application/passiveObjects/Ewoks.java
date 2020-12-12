package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
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
    private static class EwoksHolder
    {
        private static Ewoks instance = new Ewoks();
    }

    private List<Ewok> ewoks;

    public Ewoks(){}

    public void loadEwoks(int numOfEwoks)
    {
        this.ewoks = new ArrayList<Ewok>(numOfEwoks);
        for(int i = 0; i<numOfEwoks ;i++)
        {
            Ewok e = new Ewok(i);
            ewoks.add(e);
        }
    }

    public static Ewoks getInstance()
    {
        return EwoksHolder.instance;
    }

    public void addEwok(Ewok ewok)
    {
        ewoks.set(ewok.getSerialNumber(), ewok);
    }

    public void acquireEwoks(List<Integer> serialNumbers)
    {
        for (int ewok : serialNumbers)
        {
            this.ewoks.get(ewok).acquire();
        }
    }

    public boolean canAcquire(List<Integer> serialNumbers)
    {
        for (int ewok : serialNumbers)
        {
            if (!this.ewoks.get(ewok).isAvailable())
                return false;
        }
        return true;
    }
}