package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus
{
    private ConcurrentHashMap<Class<? extends Event>, AtomicInteger> roundRobinIndexPerEventClass;
    private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServicesMessages;
    private ConcurrentHashMap<Event, Future> eventsAndFutures;
    private ConcurrentHashMap<Class<? extends Message> , List<MicroService>> subscribeMicroservice;


    private static class MessageBusHolder
    {
        private static MessageBus instance = new MessageBusImpl();
    }

    private MessageBusImpl()
    {
        // init
        this.roundRobinIndexPerEventClass = new ConcurrentHashMap<>();
        this.microServicesMessages = new ConcurrentHashMap<>();
        this.eventsAndFutures = new ConcurrentHashMap<>();
        this.subscribeMicroservice = new ConcurrentHashMap<>();
    }

    public static MessageBus getInstance()
    {
        return MessageBusHolder.instance;
    }

    @Override
    public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
    {
        if(!this.subscribeMicroservice.containsKey(type))
            this.subscribeMicroservice.put(type,new LinkedList<>()); // create new list of microServices that can get this 'type' of events

        this.subscribeMicroservice.get(type).add(m); // add the microservice to the list represented by the suitable type
        System.out.println(m.getName() + " Subscribed to event " + type);
        notifyAll();
    }

    @Override
    public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {
        if(!this.subscribeMicroservice.containsKey(type))
            this.subscribeMicroservice.put(type,new LinkedList<>());

        this.subscribeMicroservice.get(type).add(m);
        System.out.println(m.getName() + " Subscribed to broadcast " + type);
        notifyAll();
    }

    @Override @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result)
    {
        if(this.eventsAndFutures.containsKey(e))
        {
            this.eventsAndFutures.get(e).resolve(result);
            this.eventsAndFutures.remove(e);
        }
    }

    @Override
    public void sendBroadcast(Broadcast b)
    {
        //  insert broadcast to the queue of the right microservice
        for (MicroService microService : subscribeMicroservice.get(b.getClass()))
        {
            try {
                this.microServicesMessages.get(microService).put(b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public synchronized <T> Future<T> sendEvent(Event<T> e)
    {
        Class<? extends Event> type = e.getClass();
        System.out.println("event " + type + " sent to the message bus");
        Future<T> futureEvent = new Future<>();
        this.eventsAndFutures.put(e,futureEvent);

        try {
            // insert event to the queue of the right microservice
            MicroService microService = roundRobinCurrentMicroservice(type);
            this.microServicesMessages.get(microService).put(e);
            this.notifyAll();
        } catch (InterruptedException exception) { // to do - what about interruption
            exception.printStackTrace();
        }

        return futureEvent;
    }

    @Override
    public void register(MicroService m)
    {
        this.microServicesMessages.put(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m)
    {
        // to do - if m is not registered yet, wait until it's register itself
        if(this.microServicesMessages.containsKey(m))
            this.microServicesMessages.remove(m);

        List<Class<?extends Message>> keysOfEmptyLists = new ArrayList<>();

        for(Class<? extends Message> key : subscribeMicroservice.keySet()) // if the Upper line doesnt work
        {
            List<MicroService> value = this.subscribeMicroservice.get(key);
            if(value.contains(m)) {
                value.remove(m);

                if (value.isEmpty()) {
                    keysOfEmptyLists.add(key);
                }
            }
        }

        for(Class<? extends Message> key : keysOfEmptyLists)
        {
            this.subscribeMicroservice.remove(key);
            this.roundRobinIndexPerEventClass.remove(key);
        }
    }


    @Override
    public synchronized Message awaitMessage(MicroService m) throws InterruptedException
    {
        if(!this.microServicesMessages.containsKey(m))
            return null;

        // if do not have messages to deal with right now
        System.out.println(m.getName() + " is waiting for message ");
        while (this.microServicesMessages.get(m).isEmpty())
            this.wait();

        Message message = this.microServicesMessages.get(m).remove();
        this.notifyAll(); // necessary?
        return message;
    }

    // need to fix synchronized - so someone could subscribe before choosing the msg
    private synchronized <T> MicroService roundRobinCurrentMicroservice(Class<? extends Event> type) throws InterruptedException {
        // no one subscribed to this message yet
        while (!this.subscribeMicroservice.containsKey(type))
        {
            System.out.println("waiting for someone take care the event " + type);
            this.wait();
        }

        // what if C3PO subscribes to AttackEvent after liea sent all her attacks
        if (!this.roundRobinIndexPerEventClass.containsKey(type))
        {
            this.roundRobinIndexPerEventClass.put(type, new AtomicInteger(0));
        }

        int OldValue;
        int newValue;
        int numberOfMicroservices = this.subscribeMicroservice.get(type).size();
        AtomicInteger currentIndex = this.roundRobinIndexPerEventClass.get(type);

        if (currentIndex.intValue() >= numberOfMicroservices)
        {

            do {
                OldValue = currentIndex.get();
                newValue = 0;
            } while (!currentIndex.compareAndSet(OldValue, newValue));
        }

        this.roundRobinIndexPerEventClass.put(type, currentIndex);
        int previous = currentIndex.getAndIncrement();
        this.roundRobinIndexPerEventClass.put(type, currentIndex);
        return this.subscribeMicroservice.get(type).get(previous);
    }
}