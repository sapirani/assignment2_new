package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary
{
    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LieaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;
    private transient long startTime;

    private static class DiaryHolder
    {
        private static Diary instance = new Diary();
    }

    public static Diary getInstance()
    {
        return DiaryHolder.instance;
    }

    public Diary() {
        this.totalAttacks = new AtomicInteger(0);
    }

    public void AddAttack() {
        this.totalAttacks.getAndIncrement();
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        this.HanSoloFinish = hanSoloFinish - this.startTime;
    }

    public void setC3POFinish(long c3POFinish) {
        this.C3POFinish = c3POFinish - this.startTime;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        this.R2D2Deactivate = r2D2Deactivate - this.startTime;
    }

    public void setLieaTerminate(long lieaTerminate) {
        this.LieaTerminate = lieaTerminate - this.startTime;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        this.HanSoloTerminate = hanSoloTerminate - this.startTime;
    }

    public void setC3POTerminate(long c3POTerminate) {
        this.C3POTerminate = c3POTerminate - this.startTime;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        this.R2D2Terminate = r2D2Terminate - this.startTime;
    }

    public void setLandoTerminate(long landoTerminate) {
        this.LandoTerminate = landoTerminate - this.startTime;
    }

    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    public String executionOutput()
    {
        long finish_attack = HanSoloFinish > C3POFinish ? HanSoloFinish : C3POFinish;
        String output = "";
        output += "There are " + totalAttacks + " attacks. \n";
        output += "HanSolo and C3PO finish their tasks ~" + (Math.abs(HanSoloFinish - C3POFinish)) +
                " milliseconds one after the other. \n";
        output += "R2D2 deactivated the shield generator after ~" + R2D2Deactivate + " milliseconds. \n";
        if(LieaTerminate == HanSoloTerminate && HanSoloTerminate == C3POTerminate && C3POTerminate == R2D2Terminate && R2D2Terminate == LandoTerminate)
            output += "All threads terminate ~" + (HanSoloTerminate - finish_attack) + " milliseconds after.";
        else
        {
            output += "Liea terminated ~" + (LieaTerminate - finish_attack) + " milliseconds after.\n" ;
            output += "HanSolo terminated ~" + (HanSoloTerminate - LieaTerminate) + " milliseconds after Liea. \n";
            output += "C3PO terminated ~" + (C3POTerminate - LieaTerminate) + " milliseconds after Liea. \n";
            output += "R2D2 terminated ~" + (R2D2Terminate - LieaTerminate) + " milliseconds after Liea. \n";
            output += "Lando terminated ~" + (LandoTerminate - R2D2Terminate) + " milliseconds after R2D2.";
        }
        return output;
    }
}