import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

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

        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void subscribeEventTest()
    {
        AttackEvent attack = new AttackEvent();
        DeactivationEvent deactivation = new DeactivationEvent();
        Message resolvedMessage = null;

        // Initialize Microservices
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService c3po = new C3POMicroservice();
        MicroService r2d2 = new R2D2Microservice(100);

        // register the MicroServices
        messageBus.register(leia);
        messageBus.register(c3po);
        messageBus.register(r2d2);

        // Try use the function subscribeEvent in MicroService
        Callback<Boolean> callback = b -> {};
        try
        {
            subscribeEventTest.invoke(c3po,AttackEvent.class, callback); // C3PO subscribes to events from type AttackEvent

            sendEventTest.invoke(leia,attack); // Leia sends message from type AttackEvent
            sendEventTest.invoke(r2d2,deactivation); // R2D2 sends message from type deactivationEvent

            resolvedMessage = messageBus.awaitMessage(c3po); // C3PO gets message, the message shuold be from type AttackEvent
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(resolvedMessage.getClass() == AttackEvent.class);
    }

    @Test
    void subscribeBroadcast()
    {
        TerminateBroadcast terminate = new TerminateBroadcast();
        Message resolvedMessageLeia = null;
        Message resolvedMessageR2D2 = null;

        // Initialize MicroServices
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService r2d2 = new R2D2Microservice(100);
        MicroService lando = new LandoMicroservice(100);

        // register the MicroServices
        messageBus.register(leia);
        messageBus.register(r2d2);
        messageBus.register(lando);

        // Try use the function subscribeBroadcast in MicroService
        Callback<Boolean> callback = b -> {};
        try
        {
            subscribeBroadcastTest.invoke(leia,TerminateBroadcast.class, callback); // Leia subscribes to messages from type TerminateBroadcast
            subscribeBroadcastTest.invoke(r2d2,TerminateBroadcast.class, callback); // R2D2 subscribes to messages from type TerminateBroadcast

            sendBroadcastTest.invoke(lando,terminate); // Lando sends broadcast message from type Terminate

            resolvedMessageLeia = messageBus.awaitMessage(leia); // Leia gets broadcast message, the message shuold be from type TerminateBroadcast
            resolvedMessageR2D2 = messageBus.awaitMessage(r2d2); // R2D2 gets broadcast message, the message shuold be from type TerminateBroadcast
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(resolvedMessageLeia.getClass() == TerminateBroadcast.class);
        assertTrue(resolvedMessageR2D2.getClass() == TerminateBroadcast.class);
    }

    @Test
    void completeTest()
    {
        AttackEvent LeiaMsg = new AttackEvent();
        Future<Boolean> leiaFuture = null;

        // Initialize and register a LeiaMicroService
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        messageBus.register(leia);

        try
        {
            leiaFuture = (Future<Boolean>) sendEventTest.invoke(leia, LeiaMsg); // leia sends an event.
                                                                                // The methods output is a Future object
            messageBus.complete(LeiaMsg, true); // when calling complete, The Future's result should be equal to the parameter
                                                      // In this case, the Future should be resolved and the result needs to be 'true'
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(leiaFuture.isDone()); // after complete, The Future should be done
        assertTrue(leiaFuture.get().equals(true));
    }

    @Test
    void sendBroadcast()
    {
        TerminateBroadcast terminate = new TerminateBroadcast();
        Message resolvedMessageLeia = null;
        Message resolvedMessageR2D2 = null;

        // Initialize MicroServices
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService r2d2 = new R2D2Microservice(100);
        MicroService lando = new LandoMicroservice(100);

        // register the MicroServices
        messageBus.register(leia);
        messageBus.register(r2d2);
        messageBus.register(lando);

        // Try use the function sendBroadcast in MicroService
        Callback<Boolean> callback = b -> {};
        try
        {
            subscribeBroadcastTest.invoke(leia,TerminateBroadcast.class, callback); // Leia subscribes to messages from type TerminateBroadcast
            subscribeBroadcastTest.invoke(r2d2,TerminateBroadcast.class, callback); // R2D2 subscribes to messages from type TerminateBroadcast

            sendBroadcastTest.invoke(lando,terminate); // Lando sends broadcast message from type Terminate

            resolvedMessageLeia = messageBus.awaitMessage(leia); // Leia gets broadcast message, the message shuold be from type TerminateBroadcast
            resolvedMessageR2D2 = messageBus.awaitMessage(r2d2); // R2D2 gets broadcast message, the message shuold be from type TerminateBroadcast
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // The message that lando sent needs to be equal to the messages that leia and r2d2 received.
        assertTrue(resolvedMessageLeia.equals(terminate));
        assertTrue(resolvedMessageR2D2.equals(terminate));
    }

    @Test
    void sendEvent()
    {
        AttackEvent attack = new AttackEvent();
        Message resolvedMessage = null;

        // Initialize MicroServices
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService c3po = new C3POMicroservice();

        // register the MicroServices
        messageBus.register(leia);
        messageBus.register(c3po);

        // Try use the function sendEvent in MicroService
        Callback<Boolean> callback = b -> {};
        try
        {
            subscribeEventTest.invoke(c3po,AttackEvent.class, callback); // C3PO subscribes to messages from type AttackEvent

            sendEventTest.invoke(leia,attack); // Leia sends message from type AttackEvent

            resolvedMessage = messageBus.awaitMessage(c3po); // C3PO gets message, the message shuold be from type AttackEvent
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(attack.equals(resolvedMessage));
    }

    @Test
    void register()
    {
        /*
            all the other test functions use the register method,
            so this method has to work if the other tests are working
         */
    }

    @Test
    void awaitMessage()
    {
        AttackEvent attack = new AttackEvent();
        TerminateBroadcast terminate = new TerminateBroadcast();

        Message resolvedMessage = null;
        Message resolvedBroadcastMessage = null;
        Message nullMessage = null;

        // Initialize MicroServices
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService c3po = new C3POMicroservice();
        MicroService lando = new LandoMicroservice(100);

        // register the MicroServices
        messageBus.register(leia);
        messageBus.register(c3po);
        messageBus.register(lando);

        // Try use the function subscribeEvent in MicroService
        Callback<Boolean> callback = b -> {};
        try
        {
            subscribeEventTest.invoke(c3po,AttackEvent.class, callback);
            subscribeBroadcastTest.invoke(c3po, TerminateBroadcast.class,callback);
            nullMessage = messageBus.awaitMessage(c3po);

            sendEventTest.invoke(leia,attack); // Leia sends message from type AttackEvent
            resolvedMessage = messageBus.awaitMessage(c3po); // C3PO gets message, the message shuold be from type AttackEvent
            sendBroadcastTest.invoke(lando, terminate);
            resolvedBroadcastMessage = messageBus.awaitMessage(c3po);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(nullMessage.equals(null));
        assertTrue(attack.equals(resolvedMessage));
        assertTrue(terminate.equals(resolvedBroadcastMessage));
    }
}