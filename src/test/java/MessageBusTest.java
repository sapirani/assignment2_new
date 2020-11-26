import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

    private Class<? extends Event<String>> type;
    private MicroService micro_service;
    private MessageBus messageBus;

    @BeforeEach
    void setUp()
    {
        micro_service = new HanSoloMicroservice();
        messageBus = new MessageBusImpl();
    }

    @Test
    void subscribeEventTest()
    {
        AttackEvent e1 = new AttackEvent();
        DeactivationEvent e2 = new DeactivationEvent();
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService c3po = new C3POMicroservice();
        MicroService r2d2 = new R2D2Microservice(100);
        c3po.subscribeEvent(AttackEvent.class, b -> {} );// subscribes with an empty callback
        leia.sendEvent(e1);
        r2d2.sendEvent(e2);
        Message resolvedMessage = null;
        try {
            resolvedMessage = messageBus.awaitMessage(c3po);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(resolvedMessage.getClass() == AttackEvent.class);
    }

    @Test
    void subscribeBroadcast()
    {

        //messageBus.subscribeBroadcast(type, micro_service);
    }

    @Test
    void complete()
    {
    }

    @Test
    void sendBroadcast()
    {
    }

    @Test
    void sendEvent()
    {
        AttackEvent e1 = new AttackEvent();
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService c3po = new C3POMicroservice();
        c3po.subscribeEvent(AttackEvent.class, b -> {} );// subscribes with an empty callback
        leia.sendEvent(e1);
        Message e2 = null;
        try {
            e2 = messageBus.awaitMessage(c3po);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(e1.equals(e2));
    }

    @Test
    void register()
    {
    }

    @Test
    void awaitMessage()
    {
        // need to deal with exception
    }
}