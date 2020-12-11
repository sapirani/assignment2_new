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
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e)
    {
        Future<T> futureEvent = new Future<>();
        this.eventsAndFutures.put(e,futureEvent);

        //  insert event to the queue of the right microservice

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
        if(this.microServicesMessages.containsKey(m))
            this.microServicesMessages.remove(m);

        for(Class key: subscribeMicroservice.keySet())
        {
            if(this.subscribeMicroservice.get(key).contains(m))
                this.subscribeMicroservice.get(key).remove(m);
        }
    }


    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException
    {
        if(this.microServicesMessages.containsKey(m))
            return this.microServicesMessages.get(m).remove();

        return null; // NEED TO CHANGE - run until you find a message
    }
}