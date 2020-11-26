import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

    private Class<? extends Event<String>> type;
    private MicroService micro_service;

    @BeforeEach
    void setUp()
    {
        micro_service = new HanSoloMicroservice();
    }

    @Test
    void subscribeEventTest()
    {

    }

    @Test
    void subscribeBroadcast() {
    }

    @Test
    void complete() {
    }

    @Test
    void sendBroadcast() {
    }

    @Test
    void sendEvent() {
    }

    @Test
    void register() {
    }

    @Test
    void unregister() {
    }

    @Test
    void awaitMessage() {
    }
}