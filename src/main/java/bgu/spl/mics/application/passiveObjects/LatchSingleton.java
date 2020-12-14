package bgu.spl.mics.application.passiveObjects; // The package

// Imports:
import java.util.concurrent.CountDownLatch;

public class LatchSingleton
{
    // Private field
    private CountDownLatch latch;

    /**
     * Implementation of thread safe singleton (as we learned in class)
     */
    private static class LatchHolder
    {
        private static LatchSingleton instance = new LatchSingleton();
    }

    /**
     * The function gives access to the singleton instance
     * @return instance of the Latch
     */
    public static LatchSingleton getInstance()
    {
        return LatchHolder.instance;
    }

    /**
     * The Class's Constructor
     */
    private LatchSingleton() {}

    /**
     * Set function
     * @param numOfThreads number of threads that doing subscribe to some typed of messages
     */
    public void setLatch(int numOfThreads)
    {
        this.latch = new CountDownLatch(numOfThreads);
    }

    /**
     * Wrapping method to an exists implementation.
     * Decrease the number of threads to wait for.
     */
    public void countDown()
    {
        this.latch.countDown();
    }

    /**
     * Wrapping method to an exists implementation.
     * Wait until all the threads will done their part
     */
    public void await() throws InterruptedException
    {
        this.latch.await();
    }
}
