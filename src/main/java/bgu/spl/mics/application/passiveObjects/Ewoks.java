package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
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

    public Ewoks(int number_of_ewoks)
    {
        ewoks = new ArrayList<>(number_of_ewoks + 1);
    }

    public void addEwok(Ewok ewok)
    {
        ewoks.set(ewok.getSerialNumber(), ewok);
    }
}