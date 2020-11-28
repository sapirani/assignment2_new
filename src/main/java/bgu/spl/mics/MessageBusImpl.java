package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus
{
    private static MessageBus messageBus = null;
    private Dictionary<MicroService, Queue<Message>> microServicesMessages;
    private Dictionary<Event, Future> eventsAndFutures;
    private Dictionary<Class<? extends Message> , List<MicroService>> subscribeMicroservice;

    private MessageBusImpl()
    {
        // init
        this.microServicesMessages = new Hashtable<>();
        this.eventsAndFutures = new Hashtable<>();
        this.subscribeMicroservice = new Hashtable<>();
    }

    public static MessageBus getInstance()
    {
        if (messageBus != null)
            return messageBus;

        return new MessageBusImpl();
    }


    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
    {
        this.subscribeMicroservice.get(type).add(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {
        this.subscribeMicroservice.get(type).add(m);
    }

    @Override @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result)
    {
        this.eventsAndFutures.get(e).resolve(result);
        this.eventsAndFutures.remove(e);
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
        this.microServicesMessages.remove(m);
    }


    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException
    {
        return this.microServicesMessages.get(m).remove();
    }
}