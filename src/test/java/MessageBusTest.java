import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

    private Class<? extends Event<String>> type;
    private MicroService micro_service;
    private MessageBus messageBus;

    // Testing protected functions in MicroService - for Event
    Method subscribeEventTest;
    Method sendEventTest;
    // Testing protected functions in MicroService - for Broadcast
    Method subscribeBroadcastTest;
    Method sendBroadcastTest;

    @BeforeEach
    void setUp()
    {
        micro_service = new HanSoloMicroservice();
        messageBus = new MessageBusImpl();
        try
        {
            // Setting access to Event functions in MicroService
            subscribeEventTest = MicroService.class.getDeclaredMethod("subscribeEvent", Event.class, Callback.class);
            sendEventTest = MicroService.class.getDeclaredMethod("sendEvent", Event.class);

            subscribeEventTest.setAccessible(true);
            sendEventTest.setAccessible(true);

            // Setting access to Broadcast functions in MicroService
            subscribeBroadcastTest = MicroService.class.getDeclaredMethod("subscribeEvent", Broadcast.class, Callback.class);
            sendBroadcastTest = MicroService.class.getDeclaredMethod("sendEvent", Broadcast.class);

            subscribeBroadcastTest.setAccessible(true);
            sendBroadcastTest.setAccessible(true);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    void subscribeEventTest()
    {
        AttackEvent e1 = new AttackEvent();
        DeactivationEvent e2 = new DeactivationEvent();
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService c3po = new C3POMicroservice();
        MicroService r2d2 = new R2D2Microservice(100);
        Message resolvedMessage = null;

        // Try use the function subscribeEvent in MicroService
        Callback<Boolean> callback = b -> {};
        try
        {
            subscribeEventTest.invoke(c3po,e1, callback);

            sendEventTest.invoke(leia,e1); // Leia sends message from type AttackEvent
            sendEventTest.invoke(r2d2,e2); // R2D2 sends message from type deactivationEvent

            resolvedMessage = messageBus.awaitMessage(c3po); // C3PO gets message, the message shuold be from type AttackEvent
        } catch (Exception e)
        {
            e.printStackTrace();
        }


        assertTrue(resolvedMessage.getClass() == AttackEvent.class);
    }

    @Test
    void subscribeBroadcast()
    {
        TerminateBroadcast b1 = new TerminateBroadcast();
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService r2d2 = new R2D2Microservice(100);
        MicroService lando = new LandoMicroservice(100);
        Message resolvedMessageLeia = null;
        Message resolvedMessageR2D2 = null;

        // Try use the function subscribeEvent in MicroService
        Callback<Boolean> callback = b -> {};
        try
        {
            subscribeBroadcastTest.invoke(leia,b1, callback); // Leia subscribes to messages from type TerminateBroadcast
            subscribeBroadcastTest.invoke(r2d2,b1, callback); // R2D2 subscribes to messages from type TerminateBroadcast
            sendBroadcastTest.invoke(lando,b1); // Lando sends broadcast message from type Terminate

            resolvedMessageLeia = messageBus.awaitMessage(leia); // Leia gets broadcast message, the message shuold be from type TerminateBroadcast
            resolvedMessageR2D2 = messageBus.awaitMessage(r2d2); // R2D2 gets broadcast message, the message shuold be from type TerminateBroadcast
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(resolvedMessageLeia.getClass() == TerminateBroadcast.class);
        assertTrue(resolvedMessageR2D2.getClass() == TerminateBroadcast.class);
    }

    @Test
    void complete()
    {
    }

    @Test
    void sendBroadcast()
    {
        TerminateBroadcast b1 = new TerminateBroadcast();
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService r2d2 = new R2D2Microservice(100);
        MicroService lando = new LandoMicroservice(100);
        Message resolvedMessageLeia = null;
        Message resolvedMessageR2D2 = null;

        // Try use the function subscribeEvent in MicroService
        Callback<Boolean> callback = b -> {};
        try
        {
            subscribeBroadcastTest.invoke(leia,b1, callback); // Leia subscribes to messages from type TerminateBroadcast
            subscribeBroadcastTest.invoke(r2d2,b1, callback); // R2D2 subscribes to messages from type TerminateBroadcast
            sendBroadcastTest.invoke(lando,b1); // Lando sends broadcast message from type Terminate

            resolvedMessageLeia = messageBus.awaitMessage(leia); // Leia gets broadcast message, the message shuold be from type TerminateBroadcast
            resolvedMessageR2D2 = messageBus.awaitMessage(r2d2); // R2D2 gets broadcast message, the message shuold be from type TerminateBroadcast
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(resolvedMessageLeia.equals(b1));
        assertTrue(resolvedMessageR2D2.equals(b1));
    }

    @Test
    void sendEvent()
    {
        AttackEvent e1 = new AttackEvent();
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService c3po = new C3POMicroservice();
        Message resolvedMessage = null;

        // Try use the function subscribeEvent in MicroService
        Callback<Boolean> callback = b -> {};
        try
        {
            subscribeEventTest.invoke(c3po,e1, callback);

            sendEventTest.invoke(leia,e1); // Leia sends message from type AttackEvent

            resolvedMessage = messageBus.awaitMessage(c3po); // C3PO gets message, the message shuold be from type AttackEvent
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(e1.equals(resolvedMessage));
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