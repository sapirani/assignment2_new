package bgu.spl.mics.application.passiveObjects; // Package

/**
 * The class keeps all the data that is in the input file.
 * We will use this data to initialize the program and the threads.
 */
public class Input
{
    // Private fields
    private Attack[] attacks;
    int R2D2;
    int Lando;
    int Ewoks;

    /**
     * Get and Set the Attacks array
     */
    public Attack[] getAttacks() { return attacks; }
    public void setAttacks(Attack[] attacks) { this.attacks = attacks; }

    /**
     * Get and Set R2D2's duration
     */
    public int getR2D2() { return R2D2; }
    public void setR2D2(int r2d2) { R2D2 = r2d2; }

    /**
     * Get and Set the number of Ewoks
     */
    public int getEwoks() { return Ewoks; }
    public void setEwoks(int ewoks) { Ewoks = ewoks; }

    /**
     *  Get and Set Lando's duration.
     */
    public int getLando() { return Lando; }
    public void setLando(int lando) { Lando = lando; }
}