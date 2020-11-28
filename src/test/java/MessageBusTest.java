import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
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
        messageBus = MessageBusImpl.getInstance();
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
        Message receivedAttack = null;

        // Initialize Microservices
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService c3po = new C3POMicroservice();
        MicroService r2d2 = new R2D2Microservice(100);

        // register the MicroServices
        messageBus.register(leia);
        messageBus.register(c3po);
        messageBus.register(r2d2);

        try
        {
            messageBus.subscribeEvent(AttackEvent.class , c3po); // C3PO subscribes to messages of type AttackEvent

            messageBus.sendEvent(deactivation); // R2D2 sends message of type DeactivationEvent
            messageBus.sendBroadcast(new Broadcast() {}); // One of the microServices sends broadcast (c3po is not subscribed to this type of messages)
            messageBus.sendEvent(attack); // Leia sends message of type AttackEvent

            receivedAttack = messageBus.awaitMessage(c3po); // C3PO should get the message that Leia sent
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(receivedAttack.getClass() == AttackEvent.class);
    }

    @Test
    void subscribeBroadcastTest()
    {
        TerminateBroadcast terminate = new TerminateBroadcast();
        Message receivedMessageLeia = null;
        Message receivedMessageR2D2 = null;

        // Initialize MicroServices
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService r2d2 = new R2D2Microservice(100);
        MicroService lando = new LandoMicroservice(100);

        // register the MicroServices
        messageBus.register(leia);
        messageBus.register(r2d2);
        messageBus.register(lando);

        try
        {
            messageBus.subscribeBroadcast(TerminateBroadcast.class , leia); // Leia subscribes to messages of type TerminateBroadcast
            messageBus.subscribeBroadcast(TerminateBroadcast.class, r2d2); // R2D2 subscribes to messages of type TerminateBroadcast

            messageBus.sendEvent(new AttackEvent()); // Leia sends message of type AttackEvent,
                                                     // but there isn't any microService that subscribes for this type of messages.
            messageBus.sendBroadcast(new Broadcast() {}); // One of the microServices sent a broadcast message,
                                                          // The message is not of type TerminateBroadcast,
                                                          // so there isn't microService that subscribes to this type of messages
            messageBus.sendBroadcast(terminate); // Lando sends message of type TerminateBroadcast

            receivedMessageLeia = messageBus.awaitMessage(leia); // Leia should get the message that Lando sent
            receivedMessageR2D2 = messageBus.awaitMessage(r2d2); // R2D2 should get the message that Lando sent
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(receivedMessageLeia.getClass() == TerminateBroadcast.class);
        assertTrue(receivedMessageR2D2.getClass() == TerminateBroadcast.class);
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
            leiaFuture = messageBus.sendEvent(LeiaMsg); // leia sends an event.
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
    void sendBroadcastTest()
    {
        TerminateBroadcast terminate = new TerminateBroadcast();
        Message receivedMessageLeia = null;
        Message receivedMessageR2D2 = null;

        // Initialize MicroServices
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService r2d2 = new R2D2Microservice(100);
        MicroService lando = new LandoMicroservice(100);

        // register the MicroServices
        messageBus.register(leia);
        messageBus.register(r2d2);
        messageBus.register(lando);

        try
        {
            messageBus.subscribeBroadcast(TerminateBroadcast.class , leia); // Leia subscribes to messages of type TerminateBroadcast
            messageBus.subscribeBroadcast(TerminateBroadcast.class, r2d2); // R2D2 subscribes to messages of type TerminateBroadcast

            messageBus.sendBroadcast(terminate); // Lando sends message of type TerminateBroadcast

            receivedMessageLeia = messageBus.awaitMessage(leia); // Leia should get the message that Lando sent
            receivedMessageR2D2 = messageBus.awaitMessage(r2d2); // R2D2 should get the message that Lando sent
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // The message that lando sent needs to be equal to the messages that leia and r2d2 received.
        assertTrue(receivedMessageLeia.equals(terminate));
        assertTrue(receivedMessageR2D2.equals(terminate));
    }

    @Test
    void sendEventTest()
    {
        AttackEvent attack = new AttackEvent();
        Message receivedAttack = null;

        // Initialize MicroServices
        MicroService leia = new LeiaMicroservice(new Attack[0]);
        MicroService hanSolo = new HanSoloMicroservice();

        // Register Microservices
        messageBus.register(leia);
        messageBus.register(hanSolo);

        try
        {
            messageBus.subscribeEvent(AttackEvent.class , hanSolo); // HanSolo subscribes to messages of type AttackEvent
            messageBus.sendEvent(attack); // Leia sends message of type AttackEvent
            receivedAttack = messageBus.awaitMessage(hanSolo); // HanSolo should get the message that Leia sent
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(attack.equals(receivedAttack));
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

        try
        {
            messageBus.subscribeEvent(AttackEvent.class , c3po); // C3PO subscribes to messages from type AttackEvent
            messageBus.subscribeBroadcast(TerminateBroadcast.class , c3po); // C3PO subscribes to broadcast messages from type TerminateBroadcast

            messageBus.sendEvent(attack); // Leia sends message from type AttackEvent            resolvedMessage = messageBus.awaitMessage(c3po); // C3PO gets message, the message should be from type AttackEvent
            resolvedMessage = messageBus.awaitMessage(c3po); // C3PO gets message, the message should be from type AttackEvent

            messageBus.sendBroadcast(terminate); // Lando sends message from type TerminateBroadcast
            resolvedBroadcastMessage = messageBus.awaitMessage(c3po); // C3PO gets message, the message should be from type TerminateBroadcast
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        assertTrue(attack.equals(resolvedMessage));
        assertTrue(terminate.equals(resolvedBroadcastMessage));
    }
}