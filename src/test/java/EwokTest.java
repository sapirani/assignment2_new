import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok ewok;
    private int SerialNumber;

    @BeforeEach
    public void setUp()
    {
        SerialNumber = 3;
        ewok = new Ewok(SerialNumber);
    }

    @Test
    void acquireTest()
    {
        assertTrue(ewok.isAvailable());
        ewok.acquire();
        assertFalse(ewok.isAvailable());
    }

    @Test
    void releaseTest()
    {
        ewok.acquire();
        assertFalse(ewok.isAvailable());
        ewok.release();
        assertTrue(ewok.isAvailable());
    }

    @Test
    void isAvailableTest()
    {
        assertTrue(ewok.isAvailable());
        ewok.acquire();
        assertFalse(ewok.isAvailable());
        ewok.release();
        assertTrue(ewok.isAvailable());
    }

    @Test
    void serialNumberTest()
    {
        assertEquals(SerialNumber, ewok.getSerialNumber());
        for(int i = 0; i < 10; i++)
        {
            SerialNumber = i;
            ewok = new Ewok(SerialNumber);
            assertEquals(SerialNumber, ewok.getSerialNumber());
        }
    }
}