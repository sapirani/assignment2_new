package bgu.spl.mics.application.services; // The package

// Inputs:
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.LatchSingleton;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService
{
    // Private field
    private long deactivation_time;

    /**
     * The Class's Constructor.
     * @param duration = The duration of the Deactivation event.
     */
    public R2D2Microservice(long duration)
    {
        super("R2D2");
        this.deactivation_time = duration;
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
        // Subscribing to Deactivation event and creating appropriate Callback function using Lambda
        subscribeEvent(DeactivationEvent.class, (deactivationEvent)->{
            try
            {
                // Simulate the deactivation of the shield generator
                Thread.sleep(this.deactivation_time);
                this.complete(deactivationEvent, true);

                // Update the diary with the time when the deactivation finished.
                Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());

                // After R2D2 finish to deactivate the shield generator,
                // He sends BombDestroyer event message, so Lando will start his act in the application
                this.sendEvent(new BombDestroyerEvent());

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });

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
    protected void close()
    {
        Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
    }
}