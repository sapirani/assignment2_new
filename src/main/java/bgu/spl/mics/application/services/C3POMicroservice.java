package bgu.spl.mics.application.services; // The package

// Imports:
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.LatchSingleton;

/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends Attackers {

    /**
     * The Class's Constructor
     */
    public C3POMicroservice() { super("C3PO"); }

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
        super.initialize(); // Call the parent method (subscribing to AttackEvent)

        // Need to subscribe to broadcast msg, Create appropriate Callback function, using Lambda expression.
        subscribeBroadcast(TerminateBroadcast.class, terminateBroadcast->this.terminate());

        // Wait until all the threads are subscribed to the Messages they need to receive.
        LatchSingleton.getInstance().countDown();
        try
        {
            LatchSingleton.getInstance().await();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Close function that runs one time, after C3PO terminates.
     * Writing to the diary the time when C3PO terminated.
     * Input:
     *      none
     * Output:
     *      none
     */
    @Override
    protected void close() { Diary.getInstance().setC3POTerminate(System.currentTimeMillis()); }

    /**
     * C3PO updates his finish time of the attacks in the diary
     * Input:
     *      none
     * Output:
     *      none
     */
    @Override
    protected void setFinished() { Diary.getInstance().setC3POFinish(System.currentTimeMillis()); }
}