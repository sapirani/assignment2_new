package bgu.spl.mics.application.services;
import java.util.List;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
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

    public C3POMicroservice() {
        super("C3PO");
    }

    @Override
    protected void initialize()
    {
        super.initialize();

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
        Diary.getInstance().setC3POTerminate(System.currentTimeMillis());
    }

    @Override
    protected void setFinished() {
        Diary.getInstance().setC3POFinish(System.currentTimeMillis());
    }
}