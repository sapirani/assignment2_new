package bgu.spl.mics;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus
{
    private static MessageBus messageBus = null;

    private MessageBusImpl()
    {
        // init
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

    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {

    }

    @Override @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result)
    {

    }

    @Override
    public void sendBroadcast(Broadcast b) {

    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {


        return null;
    }

    @Override
    public void register(MicroService m)
    {

    }

    @Override
    public void unregister(MicroService m) {

    }


    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException
    {
        ///Attack = pop();
        //m.startCallback(Attck);

        return null;
    }
}