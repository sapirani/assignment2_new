package bgu.spl.mics.application.messages; // The package

// Imports:
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.Event;
import java.util.List;

/**
 * Class that represents Attack event
 * Contains the attack that Leia sends.
 * Received only by HanSolo and C3PO
 */
public class AttackEvent implements Event<Boolean>
{
    private Attack attack;

    /**
     * The Class's Constructor
     * @param attack
     */
    public AttackEvent(Attack attack)
    {
        this.attack = attack;
    }

    /**
     * The Class's Getters
     * @return
     */

    public List<Integer> getSerials() { return attack.getSerials(); }

    public int getDuration() { return attack.getDuration(); }
}