package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;


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
    private Attack[] attacks;
    // need to take care of futures

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
    }

    @Override
    protected void initialize()
    {
        for(Attack attack : this.attacks)
        {
            AttackEvent event = new AttackEvent(attack);

            sendEvent(event); // what to do with return value?
        }

        // need to subscribe to broadcast msg
        subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
            @Override
            public void call(TerminateBroadcast terminateMsg)
            {
                terminate();
            }
        });
    }
}