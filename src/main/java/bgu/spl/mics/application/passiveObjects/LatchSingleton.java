package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class LatchSingleton {

    //private static int numOfThreads;
    private CountDownLatch latch;

    private static class LatchHolder
    {
        private static LatchSingleton instance = new LatchSingleton();
    }

    private LatchSingleton() {}

    public static LatchSingleton getInstance()
    {
        return LatchHolder.instance;
    }

    public void setLatch(int numOfThreads)
    {
        this.latch = new CountDownLatch(numOfThreads);
    }

    public void countDown()
    {
        this.latch.countDown();
    }

    public void await() throws InterruptedException
    {
        this.latch.await();
    }
}
