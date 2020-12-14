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
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends Attackers
{
    public HanSoloMicroservice() {
        super("Han");
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
                Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
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
    protected void setFinished() {
        Diary.getInstance().setHanSoloFinish(System.currentTimeMillis());
    }
}