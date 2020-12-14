package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.LatchSingleton;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    private long deactivation_time;
    public R2D2Microservice(long duration)
    {
        super("R2D2");
        this.deactivation_time = duration;
    }

    @Override
    protected void initialize()
    {
        subscribeEvent(DeactivationEvent.class, (deactivationEvent)->{
            try {

                Thread.sleep(this.deactivation_time);
                this.complete(deactivationEvent, true);
                Diary.getInstance().setR2D2Deactivate(System.currentTimeMillis());
                this.sendEvent(new BombDestroyerEvent());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // need to subscribe to broadcast msg
        subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
            @Override
            public void call(TerminateBroadcast terminateMsg)
            {
                terminate();
                Diary.getInstance().setR2D2Terminate(System.currentTimeMillis());
            }
        });

        LatchSingleton.getInstance().countDown();
        try {
            LatchSingleton.getInstance().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*this.latch.countDown();
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}