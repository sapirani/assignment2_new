package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus
{
    //private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> roundRobinQueuePerEventClass;
    //private ConcurrentHashMap<Class<? extends Event>, AtomicInteger> roundRobinIndexPerEventClass;
    private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServicesMessages;
    private ConcurrentHashMap<Event, Future> eventsAndFutures;
    //private ConcurrentHashMap<Class<? extends Message> , List<MicroService>> subscribeMicroservice;
    private ConcurrentHashMap<Class<? extends Message> , ConcurrentLinkedQueue<MicroService>> subscribeMicroservice;


    private Object subscribeLock;
    private Object broadcastLock;


    private static class MessageBusHolder
    {
        private static MessageBus instance = new MessageBusImpl();
    }

    private MessageBusImpl()
    {
        // init
        subscribeLock = new Object();
        broadcastLock = new Object();

        //this.roundRobinQueuePerEventClass = new ConcurrentHashMap<>();
        //this.roundRobinIndexPerEventClass = new ConcurrentHashMap<>();
        this.microServicesMessages = new ConcurrentHashMap<>();
        this.eventsAndFutures = new ConcurrentHashMap<>();
        this.subscribeMicroservice = new ConcurrentHashMap<>();
    }

    public static MessageBus getInstance()
    {
        return MessageBusHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
    {
        synchronized (this.subscribeLock) {
            if (!this.subscribeMicroservice.containsKey(type))
                //this.subscribeMicroservice.put(type, new LinkedList<>()); // create new list of microServices that can get this 'type' of events
                this.subscribeMicroservice.put(type, new ConcurrentLinkedQueue<>());

            this.subscribeMicroservice.get(type).add(m); // add the microservice to the list represented by the suitable type
            System.out.println(m.getName() + " Subscribed to event " + type);

            /*if (!this.roundRobinQueuePerEventClass.containsKey(type))
                this.roundRobinQueuePerEventClass.put(type, new ConcurrentLinkedQueue<>());

            this.roundRobinQueuePerEventClass.get(type).offer(m);*/
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {
        synchronized (this.broadcastLock)
        {
            if (!this.subscribeMicroservice.containsKey(type))
                //this.subscribeMicroservice.put(type, new LinkedList<>());
                this.subscribeMicroservice.put(type, new ConcurrentLinkedQueue<>());

            this.subscribeMicroservice.get(type).add(m);
            System.out.println(m.getName() + " Subscribed to broadcast " + type);
        }
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
    public /*synchronized*/ void sendBroadcast(Broadcast b) {
        // to avoid removing from that list (in unregister) while sending broadcast to other microservices

        //  insert broadcast to the queue of the right microservice
        for (MicroService microService : subscribeMicroservice.get(b.getClass())) {
            try {
                //Thread.sleep(10);
                this.microServicesMessages.get(microService).put(b);
                //this.notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public /*synchronized*/ <T> Future<T> sendEvent(Event<T> e)
    {
        Class<? extends Event> type = e.getClass();
        System.out.println("event " + type + " sent to the message bus");
        Future<T> futureEvent = new Future<>();
        this.eventsAndFutures.put(e,futureEvent);

        try {
            // insert event to the queue of the right microservice
            MicroService microService = roundRobinCurrentMicroservice(type);
            if(microService == null)
                return null;
            else {
                this.microServicesMessages.get(microService).put(e);
                //this.notifyAll();
            }
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
            //List<MicroService> value = this.subscribeMicroservice.get(key);
            ConcurrentLinkedQueue<MicroService> value = this.subscribeMicroservice.get(key);

            if (value.contains(m)) {
                value.remove(m);

                if (value.isEmpty()) {
                    keysOfEmptyLists.add(key);
                }
            }
        }


        for(Class<? extends Message> key : keysOfEmptyLists)
        {
            this.subscribeMicroservice.remove(key);
            //this.roundRobinQueuePerEventClass.remove(key);
        }
    }


    @Override
    public /*synchronized*/ Message awaitMessage(MicroService m) throws InterruptedException
    {
        if(!this.microServicesMessages.containsKey(m))
            throw new IllegalStateException();

        // if do not have messages to deal with right now
        System.out.println(m.getName() + " is waiting for message ");
        /*while (this.microServicesMessages.get(m).isEmpty())
            this.wait();

        Message message = this.microServicesMessages.get(m).remove();*/
        Message message = this.microServicesMessages.get(m).take();
        return message;
    }

    // need to fix synchronized - so someone could subscribe before choosing the msg
    private <T> MicroService roundRobinCurrentMicroservice(Class<? extends Event> type) throws InterruptedException {
        // no one subscribed to this message yet
        if (!this.subscribeMicroservice.containsKey(type))
        {
            System.out.println("There is no one subscribed to " + type);
            return null;
        }

        MicroService currentMicroservice = this.subscribeMicroservice.get(type).poll();
        this.subscribeMicroservice.get(type).offer(currentMicroservice);
        return currentMicroservice;

        /*MicroService currentMicroservice = this.roundRobinQueuePerEventClass.get(type).poll();
        this.roundRobinQueuePerEventClass.get(type).offer(currentMicroservice);
        return currentMicroservice;*/







        /*// what if C3PO subscribes to AttackEvent after liea sent all her attacks

        // if the type of message is not sent to anybody yet, and this is the first time
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

        this.roundRobinIndexPerEventClass.put(type, currentIndex); // update the index (it its suppose to be 0)
        int previous = currentIndex.getAndIncrement();
        this.roundRobinIndexPerEventClass.put(type, currentIndex); // update and increment the index
        return this.subscribeMicroservice.get(type).get(previous); // return the microservice in the previous index*/
    }
}