package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

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
    private int finishedAttacks;
    private List<Future> attackFutures;
    private Attack[] attacks;
    // need to take care of futures

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        this.attackFutures = new ArrayList<>();
        this.finishedAttacks = 0;
    }

    @Override
    protected void initialize()
    {
        System.out.println(this.getName() + " in initialize start");
        for(Attack attack : this.attacks)
        {
            AttackEvent event = new AttackEvent(attack);
            System.out.println(this.getName() + " sending attack " + event.getDuration());
            this.attackFutures.add(sendEvent(event)); // what to do with return value?
            System.out.println(this.getName() + " after sending attack " + event.getDuration());
        }

        // need to subscribe to broadcast msg
        subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
            @Override
            public void call(TerminateBroadcast terminateMsg)
            {
                terminate();
                Diary.getInstance().setLieaTerminate(System.currentTimeMillis());
            }
        });


        try {

            System.out.println(this.getName() + " in try finish");
            while (!this.finishedAllAttacks())
            {
                wait();

                System.out.println(this.getName() + " after wait");
                this.finishedAttacks++;
            }

            this.sendEvent(new DeactivationEvent());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean finishedAllAttacks()
    {
        return this.attacks.length == this.finishedAttacks;
    }
}