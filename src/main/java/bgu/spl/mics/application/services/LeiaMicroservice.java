package bgu.spl.mics.application.services; // The package

// Imports:
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.LatchSingleton;
import java.util.ArrayList;
import java.util.List;

/**
 * LeiaMicroservices Initialized with passiveObjects.Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService
{
    // Private fields
    private List<Future> attackFutures;
    private Attack[] attacks;

    /**
     * The Class's Constructor
     * @param attacks = The attacks that Leia sends to HanSolo and C3PO
     */
    public LeiaMicroservice(Attack[] attacks)
    {
        super("Leia");
        this.attacks = attacks;
        this.attackFutures = new ArrayList<>();
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
        // Need to subscribe to broadcast msg, Create appropriate Callback function, using Lambda expression.
        subscribeBroadcast(TerminateBroadcast.class, terminateBroadcast->this.terminate());

        // Wait until all the threads are subscribed to the Messages they need to receive.
        LatchSingleton.getInstance().countDown();
        try
        {
            LatchSingleton.getInstance().await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // Update the start time of the application
        Diary.getInstance().setStartTime();

        // After every MicroService is subscribed to his messages, Leia can send the AttackEvents
        for(Attack attack : this.attacks)
        {
            AttackEvent event = new AttackEvent(attack); // Create AttackEvent using the attack (which received from the input file)
            Future attackFuture = sendEvent(event); // Send the AttackEvent and receive Future object

            // If someone caught the message,
            // (Meaning, There is someone subscribe to AttackEvent messages)
            // Add the Future to the futures list
            if(!(attackFuture == null))
                this.attackFutures.add(attackFuture); // what to do with return value?
        }

        // After Leia sends all her Attacks, she wait until HanSolo and C3PO execute the AttackEvents
        // After the two are done, Leia sends DeactivationEvent to R2D2, to continue the flow of the application.
        waitUntilFinishAllAttacks();
        this.sendEvent(new DeactivationEvent());
    }

    /**
     * Close function that runs one time, after Lando terminates.
     * Writing to the diary the time when Lando terminated.
     * Input:
     *      none
     * Output:
     *      none
     */
    @Override
    protected void close() {
        Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
    }

    /**
     * The function checks if the all the futures received from sending the AttackEvents are resolved
     * If not, then Leia waits until it is done.
     * Input:
     *      none
     * Output:
     *      none
     */
    private void waitUntilFinishAllAttacks()
    {
        for (Future future : this.attackFutures) // Go through each Future received from sending AttackEvent
        {
            future.get(); // Try to get it's value (It will wait until the Future will be resolved)
        }
    }
}