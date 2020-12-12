package bgu.spl.mics.application.messages;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.Event;

import java.util.List;

public class AttackEvent implements Event<Boolean>
{
    private Attack attack;

    public AttackEvent(Attack attack)
    {
        this.attack = attack;
    }

    public List<Integer> getSerials()
    {
        return attack.getSerials();
    }

    public int getDuration()
    {
        return attack.getDuration();
    }
}