package bgu.spl.mics.application.services; // The package

// Imports:
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.LatchSingleton;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService
{
    // Private field
    private long bomb_star_destroyer_time;

    /**
     * The Class's Constructor
     * @param duration = The duration of the bombDestroyer event.
     */
    public LandoMicroservice(long duration)
    {
        super("Lando");
        this.bomb_star_destroyer_time = duration;
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
        // Subscribing to BombDestroyer event and creating appropriate Callback function using Lambda
        subscribeEvent(BombDestroyerEvent.class, (bombDestroyerEvent)->{
            try
            {
                // Simulate the bomb of the star destroyer
                Thread.sleep(this.bomb_star_destroyer_time);
                this.complete(bombDestroyerEvent, true);

                // After Lando finish to bomb the destroyer, he sends broadcast message, so all the threads will terminate
                this.sendBroadcast(new TerminateBroadcast());

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
        } catch (InterruptedException e)
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
    protected void close() {
        Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
    }
}