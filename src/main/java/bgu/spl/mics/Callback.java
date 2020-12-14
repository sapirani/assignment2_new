package bgu.spl.mics; // The package

/**
 * a callback is a function designed to be called when a message is received.
 */
public interface Callback<T>
{

    public void call(T c);
}