package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

public abstract class Attackers extends MicroService
{
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public Attackers(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, (attackMsg)->
        {
            try
            {
                Ewoks ewoks = Ewoks.getInstance();
                // acquire Ewoks
                synchronized (ewoks) // need to find better solution - do not block all ewoks class
                {
                    System.out.println(getName() + " try acquire " + attackMsg.getSerials().toString());
                    while (!ewoks.canAcquire(attackMsg.getSerials()))
                        ewoks.wait();

                    System.out.println(getName() + " acquired " + attackMsg.getSerials().toString());
                    ewoks.acquireEwoks(attackMsg.getSerials());
                    Thread.sleep(attackMsg.getDuration());
                    this.complete(attackMsg, true);
                    System.out.println(getName() + " finished attack " + attackMsg.getDuration());
                    Diary.getInstance().AddAttack();
                    setFinished();
                    ewoks.notifyAll();
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });
    }

    protected abstract void setFinished();
}
