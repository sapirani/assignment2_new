package bgu.spl.mics.application.passiveObjects; // The package

// Imports:
import java.util.List;

/**
 * Passive data-object representing an attack object.
 * You must not alter any of the given public methods of this class.
 * <p>
 * Do not add any additional members/method to this class (except for getters).
 */
public class Attack {

    // private fields
    final List<Integer> serials;
    final int duration;

    /**
     * Constructor.
     */
    public Attack(List<Integer> serialNumbers, int duration)
    {
        this.serials = serialNumbers;
        this.duration = duration;
    }

    /**
     * @return the serial numbers for the ewoks of the attack
     */
    public List<Integer> getSerials() { return this.serials; }

    /**
     * @return the duration of the attack
     */
    public int getDuration() { return this.duration; }
}