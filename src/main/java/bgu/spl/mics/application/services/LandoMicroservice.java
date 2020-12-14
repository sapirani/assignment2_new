package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.LatchSingleton;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    private long bomb_star_destroyer_time;
    public LandoMicroservice(long duration)
    {
        super("Lando");
        this.bomb_star_destroyer_time = duration;
    }

    @Override
    protected void initialize()
    {
        subscribeEvent(BombDestroyerEvent.class, (bombDestroyerEvent)->{
            try {

                Thread.sleep(this.bomb_star_destroyer_time);
                this.complete(bombDestroyerEvent, true);
                this.sendBroadcast(new TerminateBroadcast());

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

    @Override
    protected void close() {
        Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
    }
}