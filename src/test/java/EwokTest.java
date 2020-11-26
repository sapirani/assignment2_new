import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok ewok;
    private int SerialNumber;

    @BeforeEach
    public void setUp()
    {
        ewok = new Ewok(SerialNumber);
    }

    @Test
    void acquire()
    {

    }

    @Test
    void release() {
    }

    @Test
    void isAvailable() {
    }
}