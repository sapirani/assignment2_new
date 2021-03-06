package bgu.spl.mics.application.passiveObjects; // The package

// Imports:
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary
{
    // Private fields
    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;
    private transient long startTime;

    /**
     * Implementation of thread safe singleton (as we learned in class)
     */
    private static class DiaryHolder
    {
        private static Diary instance = new Diary();
    }

    /**
     * The function gives access to the singleton instance
     * @return instance of the Diary
     */
    public static Diary getInstance()
    {
        return DiaryHolder.instance;
    }

    /**
     * The Class's Constructor
     */
    private Diary()
    {
        this.totalAttacks = new AtomicInteger(0);
    }

    /**
     * Initialize the start time of the program, after Leia starts to initialize herself.
     */
    public void setStartTime()
    {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Increasing the number of attacks.
     * This is shared field between HanSolo and C3PO
     * We used AtomicInteger to deal with Synchronization.
     */
    public void AddAttack() { this.totalAttacks.getAndIncrement(); }

    /**
     * HanSolo updates the time after he finishes to deal with AttackEvent.
     * After he takes care of the last attack, it's the time he finished his part in our application.
     * @param hanSoloFinish is the time when Han finished an event.
     */
    public void setHanSoloFinish(long hanSoloFinish)
    {
        this.HanSoloFinish = hanSoloFinish - this.startTime;
    }

    /**
     * C3PO updates the time after he finishes to deal with AttackEvent.
     * After he takes care of the last attack, it's the time he finished his part in our application.
     * @param C3POFinish is the time when C3PO finished an event.
     */
    public void setC3POFinish(long C3POFinish)
    {
        this.C3POFinish = C3POFinish - this.startTime;
    }

    /**
     * R2D2 updates the time after he finishes to deactivate the shield generator.
     * @param R2D2Deactivate is the time when R2D2 finished to do his pare in our application.
     */
    public void setR2D2Deactivate(long R2D2Deactivate)
    {
        this.R2D2Deactivate = R2D2Deactivate - this.startTime;
    }

    // *** Set the time when all the MicroServices terminated *** //

    /**
     * @param leiaTerminate is the time when Leia terminates.
     */
    public void setLeiaTerminate(long leiaTerminate)
    {
        this.LeiaTerminate = leiaTerminate - this.startTime;
    }

    /**
     * @param hanSoloTerminate is the time when Leia terminates.
     */
    public void setHanSoloTerminate(long hanSoloTerminate)
    {
        this.HanSoloTerminate = hanSoloTerminate - this.startTime;
    }

    /**
     * @param c3POTerminate is the time when Leia terminates.
     */
    public void setC3POTerminate(long c3POTerminate)
    {
        this.C3POTerminate = c3POTerminate - this.startTime;
    }

    /**
     * @param R2D2Terminate is the time when Leia terminates.
     */
    public void setR2D2Terminate(long R2D2Terminate)
    {
        this.R2D2Terminate = R2D2Terminate - this.startTime;
    }

    /**
     * @param landoTerminate is the time when Leia terminates.
     */
    public void setLandoTerminate(long landoTerminate)
    {
        this.LandoTerminate = landoTerminate - this.startTime;
    }
}