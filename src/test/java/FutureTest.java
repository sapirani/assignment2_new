//package bgu.spl.mics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;
    private String result;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
        result = "result";
    }

    @Test
    public void testGet()
    {
        future.resolve(result);
        assertEquals(future.get(),"result");
        assertTrue(future.isDone());
    }

    @Test
    public void testResolve(){
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertTrue(str.equals(future.get()));
    }

    @Test
    public void testIsDone()
    {
        assertFalse(future.isDone());
        future.resolve(result);
        assertTrue(future.isDone());
    }

    @Test
    public void testTimeOutGet() throws InterruptedException
    {
        future.get(100,TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.resolve(result);
        assertEquals(future.get(100,TimeUnit.MILLISECONDS),result);
        assertTrue(future.isDone());
    }
}