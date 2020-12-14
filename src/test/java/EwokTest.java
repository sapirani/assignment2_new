// Imports:
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import bgu.spl.mics.application.passiveObjects.Ewok;

class EwokTest
{
    // Private fields
    private Ewok ewok;
    private int SerialNumber;

    @BeforeEach
    public void setUp()
    {
        SerialNumber = 3;
        ewok = new Ewok(SerialNumber);
    }

    @Test
    void acquireTest() // Try to acquire an Ewok
    {
        assertTrue(ewok.isAvailable());
        ewok.acquire();
        assertFalse(ewok.isAvailable());
    }

    @Test
    void releaseTest() // Try to release an Ewok
    {
        ewok.acquire();
        assertFalse(ewok.isAvailable());
        ewok.release();
        assertTrue(ewok.isAvailable());
    }

    @Test
    void serialNumberTest() // Check serial number of an Ewok
    {
        for(int i = 0; i < 10; i++)
        {
            SerialNumber = i;
            ewok = new Ewok(SerialNumber);
            assertEquals(SerialNumber, ewok.getSerialNumber());
        }
    }
}