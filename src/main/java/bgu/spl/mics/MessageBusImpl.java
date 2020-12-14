package bgu.spl.mics; // The package

// Imports:
import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus
{
    // Private fields
    private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServicesMessages;
    private ConcurrentHashMap<Event, Future> eventsAndFutures;
    private ConcurrentHashMap<Class<? extends Message> , ConcurrentLinkedQueue<MicroService>> subscribeMicroservice;

    // Locks
    private final Object subscribeLock;
    private final Object broadcastLock;
    private final Object unregisterLock;

    /**
     * Implementation of thread safe singleton (as we learned in class)
     */
    private static class MessageBusHolder
    {
        private static MessageBus instance = new MessageBusImpl();
    }

    /**
     * The function gives access to the singleton instance
     * @return instance of the MessageBus
     */
    public static MessageBus getInstance()
    {
        return MessageBusHolder.instance;
    }

    /**
     * The Class's Constructor
     */
    private MessageBusImpl()
    {
        // Initialize all fields
        this.subscribeLock = new Object();
        this.broadcastLock = new Object();
        this.unregisterLock = new Object();

        this.microServicesMessages = new ConcurrentHashMap<>();
        this.eventsAndFutures = new ConcurrentHashMap<>();
        this.subscribeMicroservice = new ConcurrentHashMap<>();
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
    {
        /*
        Why we use synchronized?
        - to avoid issues when few threads are trying to subscribe to specific type of event
        - for example:
            when first thread subscribed to attack event and initialized new queue (in the if condition).
            later, he added itself to the queue.
            but right at that time, another thread tries to subscribe to attack event, so it creates new queue
            and 'throws' the other thread from the queue (because it initialize new queue)
         */
        synchronized (this.subscribeLock)
        {
            // If no one is subscribed to the given type of message yet, create new queue in our data structure
            if (!this.subscribeMicroservice.containsKey(type))
                this.subscribeMicroservice.put(type, new ConcurrentLinkedQueue<>());

            this.subscribeMicroservice.get(type).add(m); // add the microservice to the queue represented by the suitable type
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {
        /*
        Why we use synchronized?
        - The same reason as above.
         */
        synchronized (this.broadcastLock)
        {
            // If no one is subscribed to the given type of message yet, create new queue in our data structure
            if (!this.subscribeMicroservice.containsKey(type))
                this.subscribeMicroservice.put(type, new ConcurrentLinkedQueue<>());

            this.subscribeMicroservice.get(type).add(m); // add the microservice to the queue represented by the suitable type
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e)
    {
        /*
        Why we don't use synchronized?
        - We are using ConcurrentHashMap for the eventsAndFutures,
         so its thread safe and it takes care of adding new variable to it.
         - We are using BlockingQueue for the microServicesMessages.get(microService),
         so its thread safe and it takes care of adding new variable to it,
         and also the function put notifies the other threads about the change.
         */
        Class<? extends Event> type = e.getClass();
        Future<T> futureEvent = new Future<>();

        // Add the event that needs to be sent and the future related to it
        this.eventsAndFutures.put(e,futureEvent);

        try
        {
            // Find which microservice should get the message by round robin manner
            // Insert event to the queue of the right microservice
            // If there isn't microservice registered to this type of message, return null
            MicroService microService = roundRobinCurrentMicroservice(type);
            if(microService == null)
                return null;
            else
            {
                // We are using thread safe object (BlockingQueue) so insert new message is synchronized,
                // It means that if the message is added to the queue, it notifies that a change happened.
                this.microServicesMessages.get(microService).put(e);
            }
        }
        catch (InterruptedException exception)
        {
            exception.printStackTrace();
        }

        return futureEvent; // Return the future of the event
    }

    @Override
    public void sendBroadcast(Broadcast b)
    {
         /*
        Why we don't use synchronized?
         - We are using BlockingQueue for the microServicesMessages.get(microService),
         so its thread safe and it takes care of adding new variable to it,
         and also the function put notifies the other threads about the change.
         - We run on the values in the subscribeMicroservice.get(b.Class) and
         the value is changing while thread unregister itself, so it's causing issues.
          (reading and removing at the same time from the same object) -
          to fix it we are using ConcurrentLinkedQueue, that is thread safe so this case won't happen.
         */

        // If there isn't thread that subscribed to the Broadcast type message, don't do anything
        if (!subscribeMicroservice.containsKey(b.getClass()))
            return;

        //  Insert the broadcast message to the queue of all the microservices that subscribed to this type of message
        for (MicroService microService : subscribeMicroservice.get(b.getClass())) // Go through each subscribed microservice
        {
            try
            {
                this.microServicesMessages.get(microService).put(b); // Insert the message to the microservice's queue
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result)
    {
        // Check if the event than needs to be resolved is exists
        if(this.eventsAndFutures.containsKey(e))
        {
            // Resolve the future object related to the event
            this.eventsAndFutures.get(e).resolve(result);

            // Remove the event from the data structure
            // We use ConcurrentHashMap so remove from it is safe
            this.eventsAndFutures.remove(e);
        }
    }

    @Override
    public void register(MicroService m)
    {
        // Insert the new microservice to the messages data structure and create new queue for his messages
        this.microServicesMessages.put(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m)
    {
        // If m is registered before, remove it's messages queue
        this.microServicesMessages.remove(m);

        /*
        Why we use synchronized?
        - To avoid issues when few threads are trying to unregister.
        for example:
        first thread subscribed to attackEvent tries to unregister.
        It remove itself from one queue in the subscribeMicroservices,
        and removes the queue related to the key 'AttackEvent' and the key itself.
        Before first thread finished the method,
        second thread that is registered to 'DeactivationEvent' goes through the same loop.
        but, when trying to do subscribeMicroservice.get(AttackEvent),
        it gets null because it is removed already by the first thread..
         */
        synchronized (this.unregisterLock)
        {
            // Unsubscribe the microservice from all the messages he has subscribed before
            List<Class<? extends Message>> keysOfEmptyLists = new ArrayList<>();
            for (Class<? extends Message> key : subscribeMicroservice.keySet())
            {
                ConcurrentLinkedQueue<MicroService> value = this.subscribeMicroservice.get(key);

                // If the microservice subscribed to the key type of message than he is in the value queue
                if (value.contains(m))
                {
                    value.remove(m);

                    // If now the queue is empty
                    if (value.isEmpty())
                    {
                        keysOfEmptyLists.add(key);
                    }
                }
            }

            // Remove all the empty queues from the data structure
            // Meaning that the appropriate key represents type of message that no body is subscribed to.
            for (Class<? extends Message> key : keysOfEmptyLists)
            {
                this.subscribeMicroservice.remove(key);
            }
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException
    {
        // If the microservice is not register yet
        if(!this.microServicesMessages.containsKey(m))
            throw new IllegalStateException();

        // We are using thread safe object (BlockingQueue) so remove an element from the queue is synchronized,
        // It means that if the message is removed from the queue, it notifies that a change happened.
        return this.microServicesMessages.get(m).take();
    }

    private MicroService roundRobinCurrentMicroservice(Class<? extends Event> type)
    {
        /*
        Why we use synchronized?
        - To avoid not sending a message to one subscribed microservice.
        for example:
        first thread subscribes to AttackEvent messages.
        second thread send AttackEvent.
        when the first thread tries to fetch the message, it remove itself from the subscribed queue.
        after the remove, the second thread sends another AttackEvent
        but there won't be microservice to receive the message, because the queue is empty
        (first thread didn't add itself to the queue yet)
         */
        // If there isn't a microservice that subscribed to the given type of message return null
        if (!this.subscribeMicroservice.containsKey(type))
            return null;

        MicroService currentMicroservice = null;
        synchronized (this.subscribeLock)
        {
            // Remove the microservice from the round robin queue and insert it again (the order is changing)
            currentMicroservice = this.subscribeMicroservice.get(type).poll();
            this.subscribeMicroservice.get(type).offer(currentMicroservice);
        }
        return currentMicroservice;
    }
}