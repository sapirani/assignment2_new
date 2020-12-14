package bgu.spl.mics.application.services; // The package

// Imports:
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * The Class is the parent class of HanSolo and C3PO
 * Used to prevent duplicate code for the two threads.
 */
public abstract class Attackers extends MicroService
{
    /**
     * The Class's Constructor
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public Attackers(String name)
    {
        super(name);
    }

    /**
     * this method is called once when the event loop starts.
     * Input:
     *      none
     * Output:
     *      none
     */
    @Override
    protected void initialize()
    {
        // Subscribing to Attack event and creating appropriate Callback function
        subscribeEvent(AttackEvent.class, (attackMsg)->
        {
            try
            {
                // Get Ewoks for attack and try to acquire them
                Ewoks ewoks = Ewoks.getInstance(); // Get the Ewoks singleton instance
                ewoks.acquireEwoks(attackMsg.getSerials()); // Acquire the needed Ewoks for the attack

                // Simulate the attack
                Thread.sleep(attackMsg.getDuration()); // The attack - simulated by sleep
                this.complete(attackMsg, true); // The attack is completed,
                                                      // need to resolve the related Future by the MessageBs

                // Release Ewoks after attack
                ewoks.releaseEwoks(attackMsg.getSerials()); // Release the Ewoks that used for the attack

                // Update Diary Data
                Diary.getInstance().AddAttack(); // Updating the attacks number in the diary
                setFinished(); // Set the time the thread finished the attack
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });
    }

    /**
     * Abstract method so each MicroService (of the two) will update his finish time in the diary
     * Input:
     *      none
     * Output:
     *      none
     */
    protected abstract void setFinished();
}