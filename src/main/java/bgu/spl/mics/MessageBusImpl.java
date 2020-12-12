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
    private int roundRobinIndex;
    private HashMap<MicroService, BlockingQueue<Message>> microServicesMessages;
    private HashMap<Event, Future> eventsAndFutures;
    private HashMap<Class<? extends Message> , List<MicroService>> subscribeMicroservice;

    private static class MessageBusHolder
    {
        private static MessageBus instance = new MessageBusImpl();
    }

    private MessageBusImpl()
    {
        // init
        this.roundRobinIndex = 0;
        this.microServicesMessages = new HashMap<>();
        this.eventsAndFutures = new HashMap<>();
        this.subscribeMicroservice = new HashMap<>();
    }

    public static MessageBus getInstance()
    {
        return MessageBusHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
    {
        if(!this.subscribeMicroservice.containsKey(type))
            this.subscribeMicroservice.put(type,new LinkedList<>()); // create new list of microServices that can get this 'type' of events

        this.subscribeMicroservice.get(type).add(m); // add the microservice to the list represented by the suitable type
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {
        if(!this.subscribeMicroservice.containsKey(type))
            this.subscribeMicroservice.put(type,new LinkedList<>());

        this.subscribeMicroservice.get(type).add(m);
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
        for (MicroService microService : subscribeMicroservice.get(b))
        {
            try {
                this.microServicesMessages.get(microService).put(b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e)
    {
        Future<T> futureEvent = new Future<>();
        this.eventsAndFutures.put(e,futureEvent);

        try {
            //  insert event to the queue of the right microservice
            MicroService microService = roundRobinCurrentMicroservice(e);
            this.microServicesMessages.get(microService).put(e);
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

        this.subscribeMicroservice.values().removeAll(Collections.singleton(m));

        /*for(List<MicroService> list : subscribeMicroservice.values()) // if the Upper line doesnt work
        {
            if(list.contains(m))
                list.remove(m);
        }*/
    }


    @Override
    public synchronized Message awaitMessage(MicroService m) throws InterruptedException
    {
        if(!this.microServicesMessages.containsKey(m))
            return null;

        while (this.microServicesMessages.get(m).isEmpty())
            this.wait();

        Message message = this.microServicesMessages.get(m).remove();
        this.notifyAll();
        return message;
    }

    private synchronized <T> MicroService roundRobinCurrentMicroservice(Event<T> e) throws InterruptedException {
        while (!this.subscribeMicroservice.containsKey(e))
            this.wait();

        int numberOfMicroservices = this.subscribeMicroservice.get(e).size();

        if (this.roundRobinIndex >= numberOfMicroservices)
            this.roundRobinIndex = 0;

        return this.subscribeMicroservice.get(e).get(this.roundRobinIndex++);
    }
}