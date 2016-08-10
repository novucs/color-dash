package net.novucs.colordash.util;

/**
 * Allows safe asynchronous passing of immutable variables.
 * @param <T> the reference type.
 */
public class BlockingReference<T> {

    private T reference;

    /**
     * Waits until a valid reference is set, then returns and removes it.
     * @return the next valid reference.
     * @throws InterruptedException
     */
    public synchronized T take() throws InterruptedException {
        while (reference == null) {
            wait();
        }

        T target = reference;
        reference = null;
        return target;
    }

    /**
     * Sets the reference and informs any threads waiting to take.
     * @param reference the new reference.
     */
    public synchronized void set(T reference) {
        this.reference = reference;
        notify();
    }
}
